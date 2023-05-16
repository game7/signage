package net.sportified.signage.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Switch
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import net.sportified.signage.R
import net.sportified.signage.SignageViewModel


class LaunchFragment : Fragment() {

    private val viewModel by activityViewModels<SignageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_launch, container, false)
        var switch = view.findViewById<Switch>(R.id.switchAutoLaunch)

        switch.setOnCheckedChangeListener {
                _, isChecked -> viewModel.launchAtStartup.value = isChecked
        }

        if(savedInstanceState == null) {
            switch.isChecked = viewModel.launchAtStartup.value ?: false
        }

        return view
    }

}