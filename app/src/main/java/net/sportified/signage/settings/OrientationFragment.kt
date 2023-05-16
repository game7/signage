package net.sportified.signage.settings

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import net.sportified.signage.R
import net.sportified.signage.SignageViewModel

class OrientationFragment : Fragment() {

    private val viewModel by activityViewModels<SignageViewModel>()

    private lateinit var btnTopPortrait: Button
    private lateinit var btnTopLandscape: Button
    private lateinit var btnTopReversePortrait: Button
    private lateinit var btnTopReverseLandscape: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_orientation, container, false)

        btnTopPortrait = view.findViewById(R.id.btnTopPortrait)
        btnTopLandscape = view.findViewById(R.id.btnTopLandscape)
        btnTopReversePortrait = view.findViewById(R.id.btnTopReversePortrait)
        btnTopReverseLandscape = view.findViewById(R.id.btnTopReverseLandscape)

        if(savedInstanceState == null) {
            registerListeners()
        }

        viewModel.orientation.value?.let { setButtonVisibility(it) }

        return view
    }

    private fun registerListeners() {
        btnTopPortrait.setOnClickListener {
            if(viewModel.rotation.value == SignageViewModel.ROTATION_LANDSCAPE) {
                changeRotation(SignageViewModel.ROTATION_PORTRAIT)
                return@setOnClickListener
            }
            if(viewModel.rotation.value == SignageViewModel.ROTATION_REVERSE_PORTRAIT) {
                changeRotation(SignageViewModel.ROTATION_LANDSCAPE)
                return@setOnClickListener
            }
        }

        btnTopReversePortrait.setOnClickListener {
            if(viewModel.rotation.value == SignageViewModel.ROTATION_LANDSCAPE) {
                changeRotation(SignageViewModel.ROTATION_REVERSE_PORTRAIT)
                return@setOnClickListener
            }
            if(viewModel.rotation.value == SignageViewModel.ROTATION_PORTRAIT) {
                changeRotation(SignageViewModel.ROTATION_LANDSCAPE)
                return@setOnClickListener
            }
        }

        btnTopReverseLandscape.setOnClickListener {
            if(viewModel.rotation.value == SignageViewModel.ROTATION_PORTRAIT) {
                changeRotation(SignageViewModel.ROTATION_REVERSE_PORTRAIT)
                return@setOnClickListener
            }
            if(viewModel.rotation.value == SignageViewModel.ROTATION_REVERSE_PORTRAIT) {
                changeRotation(SignageViewModel.ROTATION_PORTRAIT)
                return@setOnClickListener
            }
        }
    }

    private fun changeRotation(rotation: Int) {
        setButtonVisibility(rotation)
        viewModel.rotation.value = rotation
    }

    private fun setButtonVisibility(rotation: Int) {
        if(rotation == SignageViewModel.ROTATION_PORTRAIT) {
            btnTopPortrait.visibility = View.INVISIBLE
            btnTopLandscape.visibility = View.INVISIBLE
            btnTopReversePortrait.visibility = View.VISIBLE
            btnTopReverseLandscape.visibility = View.VISIBLE
            return
        }
        if(rotation == SignageViewModel.ROTATION_LANDSCAPE) {
            btnTopPortrait.visibility = View.VISIBLE
            btnTopLandscape.visibility = View.INVISIBLE
            btnTopReversePortrait.visibility = View.VISIBLE
            btnTopReverseLandscape.visibility = View.INVISIBLE
            return
        }
        if(rotation == SignageViewModel.ROTATION_REVERSE_PORTRAIT) {
            btnTopPortrait.visibility = View.VISIBLE
            btnTopLandscape.visibility = View.INVISIBLE
            btnTopReversePortrait.visibility = View.INVISIBLE
            btnTopReverseLandscape.visibility = View.VISIBLE
            return
        }
    }

}