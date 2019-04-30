package com.stylingandroid.biometrics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var authenticator: Authenticator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticator = Authenticator(this, ::handleResult)
        authenticate()
    }

    private fun handleResult(result: AuthenticationResult) {
        when (result) {
            is AuthenticationResult.Success ->
                displayFragment(SuccessFragment::class.java) {
                    it.logout = ::authenticate
                }
            is AuthenticationResult.RecoverableError ->
                displaySnackbar(result.message)
            is AuthenticationResult.UnrecoverableError -> displayErrorFragment(result.message)
            AuthenticationResult.Cancelled ->
                displayErrorFragment(getString(R.string.biometric_prompt_cancelled))
            AuthenticationResult.Failure -> {}
        }
    }

    private fun authenticate() {
        displayFragment(AuthenticatingFragment::class.java)
        authenticator.authenticate()
    }

    private fun displayErrorFragment(errorString: CharSequence) {
        displayFragment(ErrorFragment::class.java) {
            it.errorMessage = errorString
            it.retry = ::authenticate
        }
    }

    private fun <T : Fragment> displayFragment(clazz: Class<T>, initialiser: (T) -> Unit = {}): T? {
        return clazz.canonicalName?.let { className ->
            @Suppress("UNCHECKED_CAST")
            supportFragmentManager.fragmentFactory.instantiate(classLoader, className) as? T
        }?.also {
            initialiser(it)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.content_main, it)
                commit()
            }
        }
    }

    private fun displaySnackbar(text: CharSequence) {
        Snackbar.make(content_main, text, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.biometric_prompt_retry) {
                authenticator.authenticate()
            }
            .show()
    }
}
