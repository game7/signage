package net.sportified.signage

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignageViewModel(app: Application) : AndroidViewModel(app) {
    private val DEFAULT_URL = "https://screen.sportified.net/screen"

    var showSettings = false

    val orientation by lazy {
        val live = MutableLiveData(preferences.getInt("ORIENTATION", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE))
        live.observeForever {
            with(preferences.edit()) {
                putInt("ORIENTATION", live.value!!)
                commit()
            }
        }
        return@lazy live
    }

    val rotation by lazy {
        val live = MutableLiveData(preferences.getInt("ROTATION", ROTATION_LANDSCAPE))
        live.observeForever {
            with(preferences.edit()) {
                putInt("ROTATION", live.value!!)
                commit()
            }
        }
        return@lazy live
    }

    val launchAtStartup by lazy {
        val live = MutableLiveData(preferences.getBoolean("LAUNCH_AT_STARTUP", false))
        live.observeForever {
            with(preferences.edit()) {
                putBoolean("LAUNCH_AT_STARTUP", live.value!!)
                commit()
            }
        }
        return@lazy live
    }

    val refreshIntervalInSeconds by lazy {
        val live = MutableLiveData(preferences.getInt("REFRESH_INTERVAL", 5 * 60))
        live.observeForever {
            with(preferences.edit()) {
                putInt("REFRESH_INTERVAL", live.value!!)
                commit()
            }
        }
        return@lazy live
    }

    val url by lazy {
        val live = MutableLiveData(preferences.getString("URL", DEFAULT_URL))
        live.observeForever {
            with(preferences.edit()) {
                putString("URL", live.value!!)
                commit()
            }
        }
        return@lazy live
    }

    // Demo mode defaults to true so a fresh install shows bundled demo content
    // instead of an unpaired (device-key) screen. Choosing a real URL in
    // settings switches it off.
    val demoMode by lazy {
        val live = MutableLiveData(preferences.getBoolean("DEMO_MODE", true))
        live.observeForever {
            with(preferences.edit()) {
                putBoolean("DEMO_MODE", live.value!!)
                commit()
            }
        }
        return@lazy live
    }

    private val preferences: SharedPreferences
        get() = getApplication<Application>().getSharedPreferences("SIGNAGE", Context.MODE_PRIVATE)

    fun setDefaultUrl() {
        url.value = DEFAULT_URL
        demoMode.value = false
    }

    fun log(message: String) {
        Log.i("SIGNAGE", message)
    }

    companion object {
        const val ROTATION_LANDSCAPE = 0
        const val ROTATION_PORTRAIT = -90
        const val ROTATION_REVERSE_PORTRAIT = 90
        const val ROTATION_REVERSE_LANDSCAPE = 180
        // Bundled, network-free demo content shown on first launch so a fresh
        // install always displays something meaningful (e.g. for Appstore review).
        const val DEMO_URL = "file:///android_asset/demo.html"
    }

}