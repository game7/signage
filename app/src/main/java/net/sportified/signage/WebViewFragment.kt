package net.sportified.signage

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WebViewFragment : Fragment() {

    private lateinit var viewModel: SignageViewModel
    private val reloadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var reloadJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SignageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_web_view, container, false)

        var webView = view.findViewById<WebView>(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.displayZoomControls = false
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

        webView.loadUrl(viewModel.url.value!!)

        setupReload(webView, viewModel.refreshIntervalInSeconds.value!!)

        return view
    }

    private fun setupReload(webView: WebView, delayInSeconds: Int) {
        if(delayInSeconds == 0) { return }

        cancelReload()

        reloadJob = viewModel.viewModelScope.launch {
            delay(delayInSeconds.toLong() * 1000)
            webView.reload()
        }
    }

    private fun cancelReload() {
        reloadJob?.cancel()
    }

    override fun onDestroy() {
        cancelReload()
        super.onDestroy()
    }


}