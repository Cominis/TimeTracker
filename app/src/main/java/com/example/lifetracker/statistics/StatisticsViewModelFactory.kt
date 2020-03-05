package com.example.lifetracker.statistics

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.database.RoutineDatabaseDao

class StatisticsViewModelFactory(
        private val dataSource: RoutineDatabaseDao,
        private val sharedPref: SharedPreferences
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(dataSource, sharedPref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
