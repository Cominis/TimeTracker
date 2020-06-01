package com.dmt.timetracker

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

import android.app.Application
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import kotlinx.coroutines.*

class App : Application() {

    var iconPack: IconPack? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var packLoadJob: Job? = null
    private lateinit var iconPackLoader: IconPackLoader

    private fun initializeAd() {
        MobileAds.initialize(this) { status-> {}}

        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        testDevices.add("3E29BAC22EF61527EAFE8F110C4F83F6")

        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDevices)
            .build()

        MobileAds.setRequestConfiguration(requestConfiguration)
    }
    override fun onCreate() {
        super.onCreate()
        iconPackLoader = IconPackLoader(this)
        // Load the icon pack on application start.
        loadIconPack()

        initializeAd()
    }

    private fun loadIconPack() {
        if (iconPack != null) {
            // Icon pack is already loaded.
            return
        }



        val pack = createDefaultIconPack(iconPackLoader)
        // Load drawables
        pack.loadDrawables(iconPackLoader.drawableLoader)
        iconPack = pack

        // Start new job to load icon pack.
        packLoadJob?.cancel()
        packLoadJob = coroutineScope.launch(Dispatchers.Main) {
//            iconPack = loadIconPackAsync()
//            packLoadJob = null
        }
    }

    private suspend fun loadIconPackAsync(): IconPack {
        return withContext(Dispatchers.Default) {
            // Create pack from XML
            val pack = createDefaultIconPack(iconPackLoader)
            // Load drawables
            pack.loadDrawables(iconPackLoader.drawableLoader)
            pack
        }
    }
}