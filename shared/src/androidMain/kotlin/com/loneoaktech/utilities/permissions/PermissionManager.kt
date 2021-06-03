package com.loneoaktech.utilities.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by BillH on 6/2/2021
 *
 * **Experimental**
 * Compact way to handle permission request
 */
class PermissionManager(
    val permissions: List<String>,
    private val lifecycle: Lifecycle,
    private val requestScope: CoroutineScope,
    private val activityProvider: ()->ComponentActivity
) {
    companion object {
        private const val KEY_WAS_DENIED = "was-denied"
    }

    private val diskIoDispatcher = Dispatchers.Default   // should be injected

    private enum class RequestState { FRESH, GRANTED, DENIED_ONCE, DENIED_MULTIPLE }

    private val activity
        get() = activityProvider()

    private lateinit var requestLauncher: ActivityResultLauncher<Array<String>>

    private var resultHandler: ((Map<String,Boolean>)->Unit)? = null

    private val observer = object: DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            requestLauncher = activity.activityResultRegistry.register(
                "request",
                owner,
                ActivityResultContracts.RequestMultiplePermissions()){ result ->
                    resultHandler?.invoke(result)
            }
        }
    }

    val isGranted: Boolean
        get() {
            return permissions.all {  activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        }

    /**
     * Asks the user to grant permission it it hasn't already been granted.
     * ** Will fail as a suspend fn if this activity gets recreated during the permission grant process. **
     * However, should work correctly when the resurrected activity enters the same flow.
     */
    suspend fun checkAndRequestPermission(): Boolean {

        if (isGranted)
            return true

        return CoroutineScope(requestScope.coroutineContext + SupervisorJob()).async {
            try {
                runStateMachine()
            } catch (ce: CancellationException){
                Timber.e("request state machine cancelled")
                throw ce
            }
        }.await()
    }

    private suspend fun runStateMachine(): Boolean {
        while( !isGranted ) {
            when( determineRequestState() ) {
                RequestState.GRANTED -> return true
                RequestState.FRESH ->  requestPermission()
                RequestState.DENIED_ONCE -> requestPermission() // TODO show explaination
                RequestState.DENIED_MULTIPLE -> openSettings()
            }
        }

        return true
    }

    private suspend fun openSettings() {
        delay(500) // TODO
    }

    private suspend fun requestPermission(): Boolean {
        return suspendCoroutine<Map<String,Boolean>> { continuation ->
            resultHandler = { continuation.resume(it) }
            requestLauncher.launch( permissions.toTypedArray() )
        }.values.all { it }
    }

    /**
     * Determines the request state, basically to deal with the permanently denied state where the
     * OS refuses to display the grant dialog.
     */
    private suspend fun determineRequestState(): RequestState {
        return when {
            isGranted  -> RequestState.GRANTED
            shouldShowRationale() -> RequestState.DENIED_ONCE // only true after the first denial
            wasPreviouslyDenied() -> RequestState.DENIED_MULTIPLE
            else -> RequestState.FRESH
        }.also { state ->
            Timber.i("determinedRequestState: $state")
        }
    }

    private fun shouldShowRationale(): Boolean {
        return permissions.any { activity.shouldShowRequestPermissionRationale(it) }
    }

    private val prefsName by lazy { javaClass.name +  ":" + permissions.joinToString("-")}

    private suspend fun persistDenial( denied: Boolean ) {
        withContext(diskIoDispatcher){
            activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE )?.edit {
                if ( denied )
                    putBoolean(KEY_WAS_DENIED, true)
                else
                    remove(KEY_WAS_DENIED)
            }
        }
    }

    private suspend fun wasPreviouslyDenied(): Boolean {
        return withContext(diskIoDispatcher) {
            activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE).getBoolean(KEY_WAS_DENIED, false)
        }
    }

}

fun Fragment.createPermissionManager(permissions: List<String>, scope: CoroutineScope): PermissionManager =
    PermissionManager( permissions, lifecycle, scope ) { requireActivity() }