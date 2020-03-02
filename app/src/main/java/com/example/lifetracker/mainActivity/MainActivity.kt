package com.example.lifetracker.mainActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })
    }
}
