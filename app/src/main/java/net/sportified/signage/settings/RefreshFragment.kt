package net.sportified.signage.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import net.sportified.signage.R
import net.sportified.signage.SignageViewModel

class RefreshFragment : Fragment() {

    private val viewModel by activityViewModels<SignageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_refresh, container, false)

        view.findViewById<RadioGroup>(R.id.radioGroup).setOnCheckedChangeListener {
                _, checkedId ->  setRefreshForSelectionOf(checkedId)
        }

        if(savedInstanceState == null) {
            initialize(view)
        }

        return view
    }

    private fun setRefreshForSelectionOf(id: Int) {
        viewModel.refreshIntervalInSeconds.value = when(id) {
            R.id.radioRefreshNever -> 0
            R.id.radioRefresh5 -> 5
            R.id.radioRefresh10 -> 10
            R.id.radioRefresh30-> 30
            R.id.radioRefresh60 -> 60
            else -> 0
        } * 60
    }

    private fun initialize(view: View) {
        val buttonId = when((viewModel.refreshIntervalInSeconds.value ?: 0) / 60) {
            0 -> R.id.radioRefreshNever
            5 -> R.id.radioRefresh5
            10 -> R.id.radioRefresh10
            30 -> R.id.radioRefresh30
            60 -> R.id.radioRefresh60
            else ->  R.id.radioRefreshNever
        }
        view.findViewById<RadioGroup>(R.id.radioGroup).check(buttonId)
    }

}