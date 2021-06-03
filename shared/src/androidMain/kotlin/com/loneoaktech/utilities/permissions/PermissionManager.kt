package com.loneoaktech.utilities.permissions

/**
 * A compact method to request and manage app permissions.
 *
 * ** Experimental **
 *
 * Created by BillH on 6/3/2021
 */
interface PermissionManager {

    /**
     * Provides a non-suspending check to see if permission is already granged.
     */
    val isGranted: Boolean

    /**
     * Asks the user to grant permission it it hasn't already been granted.
     * ** Will fail as a suspend fn if this activity gets recreated during the permission grant process. **
     * However, should work correctly when the resurrected activity reenters the same flow.
     */
    suspend fun checkAndRequestPermission(): Boolean
}