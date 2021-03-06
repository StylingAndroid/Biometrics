package com.stylingandroid.biometrics

import android.os.Handler
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

internal class Authenticator(
    private val fragmentActivity: FragmentActivity,
    private val callback: (AuthenticationResult) -> Unit,
    private val biometricChecker: BiometricChecker = BiometricChecker.getInstance(fragmentActivity)
) {

    private val handler = Handler()

    private val authCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            Timber.d("Error: $errorCode: $errString")
            callback(AuthenticationResult.UnrecoverableError(errorCode, errString))
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Timber.d("Failed")
            callback(AuthenticationResult.Failure)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            Timber.d("Success")
            callback(AuthenticationResult.Success(result.cryptoObject))
        }
    }

    private val biometricPrompt = BiometricPrompt(
        fragmentActivity,
        { runnable -> handler.post(runnable) },
        authCallback
    )

    private val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(fragmentActivity.getString(R.string.biometric_prompt_title))
        .setNegativeButtonText(fragmentActivity.getString(R.string.biometric_prompt_negative_text))
        .build()

    fun authenticate() {
        if (!biometricChecker.hasBiometrics) {
            callback(AuthenticationResult.UnrecoverableError(
                0,
                fragmentActivity.getString(R.string.biometric_prompt_no_hardware)
            ))
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}
