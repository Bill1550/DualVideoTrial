package com.loneoaktech.utilities.permissions

import android.Manifest
import androidx.annotation.StringRes
import com.loneoaktech.tests.shared.R

/**
 * Created by BillH on 6/3/2021
 */
enum class Permissions(
    /**
     * The Android key for the permission
     */
    override val key: String,

    /**
     * Optional message that is displayed before the permission is first requested.
     * Null to skip.
     */
    @StringRes override val permissionExplanation: Int?,

    /**
     * Message displayed when the user has denied the permission once.
     */
    @StringRes override val rationaleText: Int,

    /**
     * Message displayed before sending user to the app settings to set this permission
     */
    @StringRes override val settingsAdviceText: Int
) : PermissionSpec {
    CAMERA(Manifest.permission.CAMERA, R.string.permission_explanation_ble, R.string.permission_rationale_camera, R.string.permission_advice_camera),
    BLE(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_explanation_ble, R.string.permission_rationale_ble, R.string.permission_advice_ble)
}