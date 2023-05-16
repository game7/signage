package net.sportified.signage

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebView
import android.window.OnBackInvokedDispatcher

class OldActivity : Activity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        setupWebView()

    }

    override fun onBackPressed() {
        var dialog = AlertDialog.Builder(this);
        dialog.setTitle("Exit Signage")
            .setMessage("Would you like to exit?")
            .setPositiveButton("Yes") {
                    _, _ -> super.onBackPressed()
            }
            .setNegativeButton("No") { _,_ -> {} }
            .setNeutralButton("Settings") { _,_ -> {} }
            .show()
    }

    private fun setupWebView() {
        webView = WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.settings.displayZoomControls = false
//        webView.settings.cacheMode = 2
        webView.settings.domStorageEnabled = true

        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = false
        webView.settings.builtInZoomControls = false
        webView.settings.setSupportZoom(false)
        webView.setInitialScale(100)


//        webView.settings.loadWithOverviewMode = false
//        webView.settings.useWideViewPort = true
//        webView.settings.builtInZoomControls = true
//        webView.settings.setSupportZoom(true)
//        webView.setInitialScale(0)

        webView.settings.mediaPlaybackRequiresUserGesture = false

        webView.loadUrl("http://10.0.2.2:3000/screen")
        setContentView(webView)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if(event?.action == KeyEvent.ACTION_UP ) {
            event?.keyCode?.let { setOrientation(it) }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun logKey(key: Int) {
        when (key) {
            KeyEvent.KEYCODE_DPAD_UP ->     Log.i("KEY", "UP")
            KeyEvent.KEYCODE_DPAD_RIGHT ->  Log.i("KEY", "RIGHT")
            KeyEvent.KEYCODE_DPAD_DOWN ->  Log.i( "KEY", "DOWN")
            KeyEvent.KEYCODE_DPAD_LEFT ->   Log.i("KEY", "LEFT")
        }
    }

    private fun logOrientation(value: Int) {
        when (value) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ->     Log.i("KEY", "LANDSCAPE")
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ->  Log.i("KEY", "PORTRAIT")
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE ->  Log.i( "KEY", "REVERSE_LANDSCAPE")
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT ->   Log.i("KEY", "REVERSE_PORTRAIT")
        }
    }

    private fun setOrientation(keyEvent: Int) {
        Log.i("KEY", "-------------------------------------")
        logOrientation((requestedOrientation))
        logKey(keyEvent)


        if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
             val orientation = when (keyEvent) {
                KeyEvent.KEYCODE_DPAD_UP ->     ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_RIGHT ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                KeyEvent.KEYCODE_DPAD_DOWN ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_LEFT ->   ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else ->                         null
             }
            if(orientation != null) { requestedOrientation = orientation }
            logOrientation((requestedOrientation))
            return
        }
        if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            val orientation = when (keyEvent) {
                KeyEvent.KEYCODE_DPAD_RIGHT ->     ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_DOWN ->   ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                KeyEvent.KEYCODE_DPAD_LEFT ->   ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_UP ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                else ->                         null
            }
            if(orientation != null) { requestedOrientation = orientation }
            logOrientation((requestedOrientation))
            return
        }
        if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            val orientation = when (keyEvent) {
                KeyEvent.KEYCODE_DPAD_DOWN ->     ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_LEFT ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                KeyEvent.KEYCODE_DPAD_UP ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_RIGHT ->   ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else ->                         null
            }
            if(orientation != null) { requestedOrientation = orientation }
            logOrientation((requestedOrientation))
            return
        }
        if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            val orientation = when (keyEvent) {
                KeyEvent.KEYCODE_DPAD_RIGHT ->     ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_DOWN ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                KeyEvent.KEYCODE_DPAD_LEFT ->   ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                KeyEvent.KEYCODE_DPAD_UP ->   ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else ->                         null
            }
            if(orientation != null) { requestedOrientation = orientation }
            logOrientation((requestedOrientation))
            return
        }
    }



}