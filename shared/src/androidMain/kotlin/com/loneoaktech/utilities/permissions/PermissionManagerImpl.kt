package com.loneoaktech.utilities.permissions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.loneoaktech.tests.shared.R
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by BillH on 6/2/2021
 *
 * **Experimental**
 * Compact way to handle permission request
 *
 * Usage:
 *  In fragment:
 *
 *  val permissionManager = createPermissionManager( listOf(permissions), appCoroutineScope )
 *
 *  Design:
 *      Uses the Activity Result Registry (recently added to AndroidX) to handle the OS permission
 *      request and activity launch process, eliminating the need for multiple overrides in the app.
 *
 *      Runs a state machine to handle the full permission request options, including if the the user
 *      denies permissions multiple times.
 *
 *      Runs as a single suspend fun, that should be launched in the scope of the UI component.
 *      *** This is experimental, since in edge cases the calling activity can be destroyed during
 *      the permission request.  It should recover when the restarted app re-verifies permission. ***
 *
 */
class PermissionManagerImpl internal constructor(

    /**
     * Permissions to be requested by this manager.
     */
    val permissions: List<PermissionSpec>,

    /**
     * Lifecycle of the UI component which will make the request (Activity or Fragment)
     */
    private val lifecycle: Lifecycle,

    /**
     * The application scope. Should be a long lived supervisor job that is used to run the specific request.
     * Should not be the UI component scope, as this can cancel when the UI component goes into the background.
     * Normally injected.
     */
    private val requestScope: CoroutineScope,

    /**
     * A lambda to get the activity, runs as a lambda since the activity isn't know at Fragment construction time.
     */
    private val activityProvider: () -> ComponentActivity
) : PermissionManager {
    companion object {
        private const val KEY_WAS_DENIED = "was-denied"
    }

    private val diskIoDispatcher = Dispatchers.Default   // should be injected

    private enum class RequestState { FRESH, GRANTED, DENIED_ONCE, DENIED_MULTIPLE }

    private val activity
        get() = activityProvider()

    private lateinit var requestLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>

    private var permissionResultHandler: ((Map<String, Boolean>) -> Unit)? = null
    private var settingsResultHandler: ((ActivityResult) -> Unit)? = null

    private val observer = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            Timber.i("Observer: onCreate")
            requestLauncher = activity.activityResultRegistry.register(
                "request",
                owner,
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                permissionResultHandler?.invoke(result)
            }

            settingsLauncher = activity.activityResultRegistry.register(
                "settings",
                owner,
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                settingsResultHandler?.invoke(result)
            }

        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            Timber.i("Observer: onDestroy")
            lifecycle.removeObserver(this)
        }
    }

    init {
        lifecycle.addObserver(observer)
    }

    /**
     * Provides a non-suspending check to see if permission is already granted.
     */
    override val isGranted: Boolean
        get() {
            return permissions.isEmpty() ||
                    permissions.map { it.key }.all { key ->
                        activity.checkSelfPermission(key) == PackageManager.PERMISSION_GRANTED
                    }
        }

    /**
     * Asks the user to grant permission it it hasn't already been granted.
     * ** Will fail as a suspend fn if this activity gets recreated during the permission grant process. **
     * However, should work correctly when the resurrected activity reenters the same flow.
     */
    override suspend fun checkAndRequestPermission(): Boolean {
        Timber.i("checkAndRequestPermission, isGranted=$isGranted")
        if (isGranted) {
            persistDenial(false)
            return true
        }

        return CoroutineScope(requestScope.coroutineContext + SupervisorJob() + Dispatchers.Main).async {
            try {
                runStateMachine()
            } catch (ce: CancellationException) {
                Timber.e("request state machine cancelled")
                throw ce
            }
        }.await()
    }

    /**
     * The heart of the request process. Mediates the correct display to the user.
     */
    private suspend fun runStateMachine(): Boolean {
        while (!isGranted) {
            when (determineRequestState().apply { Timber.i("run state machine, state=$this") }) {
                RequestState.GRANTED -> return true
                RequestState.FRESH -> {
                    showPermissionExplanation(permissions.first())
                    requestPermission()
                }
                RequestState.DENIED_ONCE -> {
                    showMustHaveDialog(permissions.first()) // TODO deal with partial approval of a list of permissions
                    requestPermission()
                }
                RequestState.DENIED_MULTIPLE -> {
                    showSettingsWarning(permissions.first())  // TODO same as above
                    openAppSettings()
                }
            }
        }

        return true
    }

    private suspend fun requestPermission(): Boolean {
        return suspendCoroutine<Map<String, Boolean>> { continuation ->
            permissionResultHandler = {
                permissionResultHandler = null
                continuation.resume(it)
            }
            requestLauncher.launch(permissions.map { it.key }.toTypedArray())
        }.values.all { it }.also { granted ->
            persistDenial(!granted)
        }
    }

    /**
     * Determines the request state, basically to deal with the permanently denied state where the
     * OS refuses to display the grant dialog.
     */
    private suspend fun determineRequestState(): RequestState {
        return when {
            isGranted -> RequestState.GRANTED
            shouldShowRationale() -> RequestState.DENIED_ONCE // only true after the first denial
            wasPreviouslyDenied() -> RequestState.DENIED_MULTIPLE
            else -> RequestState.FRESH
        }.also { state ->
            Timber.i("determinedRequestState: $state")
        }
    }

    private fun shouldShowRationale(): Boolean {
        return permissions.any { activity.shouldShowRequestPermissionRationale(it.key) }
    }

    private val prefsName by lazy { javaClass.name + ":" + permissions.joinToString("-") }

    private suspend fun persistDenial(denied: Boolean) {
        withContext(diskIoDispatcher) {
            activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE)?.edit {
                if (denied)
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

    private suspend fun showPermissionExplanation(permission: PermissionSpec) {
        permission.permissionExplanation?.let { sid ->
            showWarningDialog(sid)
        }
    }

    private suspend fun showMustHaveDialog(permission: PermissionSpec) {
        showWarningDialog(permission.rationaleText)
    }

    private suspend fun showSettingsWarning(permission: PermissionSpec) {
        showWarningDialog(permission.settingsAdviceText)
    }

    private suspend fun showWarningDialog(titleSid: Int) {
        suspendCoroutine<Unit> { continuation ->

            AlertDialog.Builder(activity)
                .setCancelable(false)
                .setMessage(titleSid)
                .setPositiveButton(R.string.button_label_ok) { dialog, _ ->
                    dialog.dismiss()
                    continuation.resume(Unit)
                }
                .show()
        }
    }

    private suspend fun openAppSettings() {
        suspendCoroutine<ActivityResult> { continuation ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", activity.packageName, null)
            }

            settingsResultHandler = { result ->
                settingsResultHandler = null
                continuation.resume(result)
            }

            settingsLauncher.launch(intent)
        }
    }
}

/**
 * Creates a permission manager. Must be created in Fragment's constructor (init period) so it exists before
 * onCreate runs.
 */
fun Fragment.createPermissionManager(
    /**
     * Permissions to be requested by this manager.
     * Can be a list of Permissions enum values, or custom objects implementing the PermissionSpec interface.
     * Specifies the permission and the UI text strings.
     */
    permissions: List<PermissionSpec>,

    /**
     * The application scope. Should be a long lived supervisor job that is used to run the specific request.
     * Should not be the UI component scope, as this can cancel when the UI component goes into the background.
     * Normally injected.
     */
    scope: CoroutineScope
): PermissionManager =
    PermissionManagerImpl(permissions, lifecycle, scope) { requireActivity() }

/**
 * Creates a permission manager. Must be created in the Activity's constructor (init period) so it exists before
 * onCreate runs.
 */
fun ComponentActivity.createPermissionManager(
    /**
     * Permissions to be requested by this manager.
     * Can be a list of Permissions enum values, or custom objects implementing the PermissionSpec interface.
     * Specifies the permission and the UI text strings.
     */
    permissions: List<PermissionSpec>,

    /**
     * Permissions to be requested by this manager.
     */
    scope: CoroutineScope)
: PermissionManager =
    PermissionManagerImpl(permissions, lifecycle, scope ) { this }