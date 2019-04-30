package com.stylingandroid.biometrics

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.os.Build
import androidx.core.os.BuildCompat

sealed class BiometricChecker {

    abstract val hasBiometrics: Boolean

    @TargetApi(Build.VERSION_CODES.Q)
    private class QBiometricChecker(
        private val biometricManager: BiometricManager
    ) : BiometricChecker() {

        private val availableCodes = listOf(
                BiometricManager.BIOMETRIC_SUCCESS,
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
        )

        override val hasBiometrics: Boolean
            get() = availableCodes.contains(biometricManager.canAuthenticate())

        companion object {

            fun getInstance(context: Context): QBiometricChecker? =
                context.getSystemService(BiometricManager::class.java)?.let {
                    QBiometricChecker(it)
                }
        }
    }

    @Suppress("DEPRECATION")
    private class LegacyBiometricChecker(
        private val fingerprintManager: android.hardware.fingerprint.FingerprintManager
    ) : BiometricChecker() {

        override val hasBiometrics: Boolean
            @SuppressLint("MissingPermission")
            get() = fingerprintManager.isHardwareDetected

        companion object {

            fun getInstance(context: Context): LegacyBiometricChecker? =
                    context.getSystemService(
                            android.hardware.fingerprint.FingerprintManager::class.java
                    )?.let {
                        LegacyBiometricChecker(it)
                    }
        }
    }

    private class DefaultBiometricChecker : BiometricChecker() {

        override val hasBiometrics: Boolean = false
    }

    companion object {

        @SuppressLint("ObsoleteSdkInt")
        fun getInstance(context: Context): BiometricChecker {
            return when {
                // We should change this when Android Q is fully released
                // as this BuildCompat lookup will be deprecated
                BuildCompat.isAtLeastQ() ->
                    QBiometricChecker.getInstance(context)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    LegacyBiometricChecker.getInstance(context)
                else -> null
            } ?: DefaultBiometricChecker()
        }
    }
}
