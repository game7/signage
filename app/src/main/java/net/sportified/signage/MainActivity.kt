package net.sportified.signage

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider


class MainActivity : FragmentActivity() {

    private lateinit var viewModel: SignageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // required to enable http request
        // https://stackoverflow.com/questions/22395417/error-strictmodeandroidblockguardpolicy-onnetwork
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[SignageViewModel::class.java]

        val root = findViewById<FrameLayout>(R.id.root)
        val content = findViewById<FrameLayout>(R.id.content)

        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                applyRotation(root, content)
            }
        })

        viewModel.rotation.observe(this) {
            applyRotation(root, content)
        }

        if(savedInstanceState == null) {
            showWebView()
        }
    }

    override fun onBackPressed() {
        if(viewModel.showSettings) {
            showWebView();
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Exit Signage")
            .setMessage("Would you like to exit?")
            .setPositiveButton("Yes") {
                    _, _ -> super.onBackPressed()
            }
            .setNegativeButton("No") { _,_ -> {} }
            .setNeutralButton("Settings") { _,_ -> showSettings() }
            .show();
    }

    private fun applyRotation(root: FrameLayout, content: FrameLayout) {
        root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val rotation = viewModel.rotation.value!!

        viewModel.log("APPLYING ROTATION OF $rotation DEGREES")

        content.rotation = rotation.toFloat()

        val isPortrait = arrayOf(
            SignageViewModel.ROTATION_PORTRAIT,
            SignageViewModel.ROTATION_REVERSE_PORTRAIT
        ).contains(rotation)

        if(isPortrait) {
            viewModel.log("PORTRAIT MODE")
            content.translationX = ((root.width - root.height) / 2).toFloat()
            content.translationY = ((root.height - root.width) / 2).toFloat()
            content.layoutParams.width = root.height
            content.layoutParams.height = root.width
        } else {
            viewModel.log("LANDSCAPE MODE")
            content.translationX = 0F
            content.translationY = 0F
            content.layoutParams.width = root.width
            content.layoutParams.height = root.height
        }
        content.requestLayout()
    }

    private fun showWebView() {
        viewModel.showSettings = false

        supportFragmentManager.commit {
            add(R.id.content, WebViewFragment())
            setReorderingAllowed(true)
        }
    }

    private fun showSettings() {
        viewModel.showSettings = true

        supportFragmentManager.commit {
            replace(R.id.content, SettingsFragment())
        }
    }

}