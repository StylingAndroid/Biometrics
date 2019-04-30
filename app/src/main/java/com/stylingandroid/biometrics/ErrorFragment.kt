package com.stylingandroid.biometrics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_error.*

class ErrorFragment : Fragment() {

    var errorMessage: CharSequence = ""
        set(value) {
            field = value
            if (error != null) {
                error.text = value
            }
        }

    var retry: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (errorMessage.isNotEmpty()) {
            error.text = errorMessage
        }
        button_retry.setOnClickListener { retry() }
    }
}
