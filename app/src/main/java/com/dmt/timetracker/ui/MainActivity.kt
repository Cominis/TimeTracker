package com.dmt.timetracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.dmt.timetracker.R
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.dmt.timetracker.viewmodels.MainActivityViewModelFactory
import com.google.android.gms.ads.*


class MainActivity : AppCompatActivity() {

    private var mAdView: AdView? = null


    private fun loadBannerAd() {
        mAdView?.loadAd(AdRequest.Builder().build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdView = findViewById(R.id.my_banner)
        loadBannerAd()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val value = sharedPref.getBoolean(resources.getString(R.string.dark_theme_key), true)

        val factory =
            MainActivityViewModelFactory(
                value
            )
        val viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)

        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })

        viewModel.updateTheme.observe(this, Observer {
            if(it){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        })
    }

}
