package com.loneoaktech.utilities.permissions

import androidx.annotation.StringRes

/**
 * Created by BillH on 6/3/2021
 */
interface PermissionSpec {
    /**
     * The Android key for the permission
     */
    val key: String

    /**
     * Optional message that is displayed before the permission is first requested.
     * Null to skip.
     */
    val permissionExplanation: Int?

    /**
     * Message displayed when the user has denied the permission once.
     */
    val rationaleText: Int

    /**
     * Message displayed before sending user to the app settings to set this permission
     */
    val settingsAdviceText: Int
}