package net.sportified.signage.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.sportified.signage.R
import net.sportified.signage.SignageViewModel
import java.net.HttpURLConnection
import java.net.URL

class UrlViewModel : ViewModel() {
    val url = MutableStateFlow("").debounce(500)
}

class UrlFragment : Fragment() {

    private val viewModel by activityViewModels<SignageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_url, container, false)
        var editUrlLayout = view.findViewById<TextInputLayout>(R.id.editUrlLayout)
        var editUrl = view.findViewById<TextInputEditText>(R.id.editUrl)

        viewModel.log("URL_VIEW_CREATED")
        viewModel.log("VIEW_MODEL_URL_VALUE: ${viewModel.url.value}")
        viewModel.log("EDIT_URL_VALUE: ${editUrl.text}")

        // capture url change
        editUrl.setOnFocusChangeListener {
                v, hasFocus -> if(!hasFocus) {
                    val url = (v as? TextInputEditText)?.text.toString()
                    val isValid = urlIsValid(url)
                    viewModel.log(isValid.toString())
                    if(isValid) {
                        editUrlLayout.error = null
                        editUrlLayout.isErrorEnabled = false
                        viewModel.url.value = (v as? TextInputEditText)?.text.toString()
                    } else {
                        editUrlLayout.error = "Invalid URL"
                        editUrlLayout.isErrorEnabled = true
                    }
                }
        }

        // set default button click
        view.findViewById<Button>(R.id.btnSportifiedDefault).setOnClickListener {
            viewModel.setDefaultUrl()
            editUrl.setText(viewModel.url.value ?: "")
        }

        editUrl.setText(viewModel.url.value ?: "")

        return view

    }

    private fun urlIsValid(url: String): Boolean {
        val conn = URL(url).openConnection() as HttpURLConnection
        var isValid = false;
        try {
            isValid = (conn.responseCode == 200)
        } finally {
            conn.disconnect()
        }
        return isValid
    }

}