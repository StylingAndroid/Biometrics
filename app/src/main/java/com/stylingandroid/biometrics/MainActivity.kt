package com.stylingandroid.biometrics

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle(getString(R.string.biometric_prompt_title))
            .setNegativeButton(
                getString(R.string.biometric_prompt_negative_text),
                mainExecutor,
                DialogInterface.OnClickListener { _, _ ->
                    println()
                    finish()
                })
            .build()

        authenticate()
    }

    private fun authenticate() {
        biometricPrompt.authenticate(cancellationSignal, mainExecutor, authCallback)
    }

    private val cancellationSignal = CancellationSignal()
        .apply {
            setOnCancelListener(cancelListener)
        }

    private val cancelListener = CancellationSignal.OnCancelListener {
        println("Cancelled")
        finish()
    }

    private val authCallback = object : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            println("Error: $errorCode: $errString")
            finish()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            println("Failed")
            finish()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            println("Success")
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
            super.onAuthenticationHelp(helpCode, helpString)
            println("Authentication help: $helpString")
            Snackbar.make(main_activity, helpString, Snackbar.LENGTH_LONG).show()
            authenticate()
        }
    }
}
