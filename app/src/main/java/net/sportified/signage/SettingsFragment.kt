package net.sportified.signage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout.TabView
import net.sportified.signage.settings.LaunchFragment
import net.sportified.signage.settings.OrientationFragment
import net.sportified.signage.settings.RefreshFragment
import net.sportified.signage.settings.UrlFragment

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SignageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SignageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val btnOrientation = view.findViewById<Button>(R.id.btnOrientation)
        val btnLaunch = view.findViewById<Button>(R.id.btnLaunch)
        val btnRefresh = view.findViewById<Button>(R.id.btnRefresh)
        val btnUrl = view.findViewById<Button>(R.id.btnUrl)

        val buttons = arrayOf(
            btnOrientation,
            btnLaunch,
            btnRefresh,
            btnUrl
        )

        val activateButton = { toActivate:Button ->
            buttons.forEach { button -> button.isActivated = button == toActivate }
        }

        btnOrientation.setOnClickListener {
            activateButton(btnOrientation)
            showFragment(OrientationFragment())
        }
        btnLaunch.setOnClickListener {
            activateButton(btnLaunch)
            showFragment(LaunchFragment())
        }
        btnRefresh.setOnClickListener {
            activateButton(btnRefresh)
            showFragment(RefreshFragment())
        }
        btnUrl.setOnClickListener {
            activateButton(btnUrl)
            showFragment(UrlFragment())
        }

        if(savedInstanceState == null) {
            activateButton(btnOrientation)
            showFragment(OrientationFragment())
        }

        return view
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.commit {
            replace(R.id.settingsScreen, fragment)
        }
    }

}