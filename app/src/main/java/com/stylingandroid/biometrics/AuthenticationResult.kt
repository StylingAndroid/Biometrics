package com.stylingandroid.biometrics

import android.hardware.biometrics.BiometricPrompt

internal sealed class AuthenticationResult {
    data class Success(val cryptoObject: BiometricPrompt.CryptoObject?) : AuthenticationResult()
    data class RecoverableError(val code: Int, val message: CharSequence) : AuthenticationResult()
    data class UnrecoverableError(val code: Int, val message: CharSequence) : AuthenticationResult()
    object Failure : AuthenticationResult()
    object Cancelled : AuthenticationResult()
}
