package com.stylingandroid.biometrics

import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import timber.log.Timber

internal class Authenticator(
    private val context: Context,
    private val callback: (AuthenticationResult) -> Unit,
    private val biometricChecker: BiometricChecker = BiometricChecker.getInstance(context)
) {

    private val biometricPrompt: BiometricPrompt = BiometricPrompt.Builder(context)
        .setTitle(context.getString(R.string.biometric_prompt_title))
        .setNegativeButton(
            context.getString(R.string.biometric_prompt_negative_text),
            context.mainExecutor,
            DialogInterface.OnClickListener { _, _ ->
                Timber.d("Negative Button")
                callback(AuthenticationResult.Cancelled)
            }
        )
        .build()

    fun authenticate() {
        if (!biometricChecker.hasBiometrics) {
            callback(AuthenticationResult.UnrecoverableError(
                0,
                context.getString(R.string.biometric_prompt_no_hardware)
            ))
        } else {
            biometricPrompt.authenticate(cancellationSignal, context.mainExecutor, authCallback)
        }
    }

    private val cancellationSignal = CancellationSignal()
        .apply {
            setOnCancelListener(cancelListener)
        }

    private val cancelListener = CancellationSignal.OnCancelListener {
        Timber.d("Cancelled")
        callback(AuthenticationResult.Cancelled)
    }

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

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
            super.onAuthenticationHelp(helpCode, helpString)
            Timber.d("Authentication help: $helpString")
        }
    }
}
