package com.example.lifetracker.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.database.RoutineDatabaseDao

class StatisticsViewModelFactory(
        private val dataSource: RoutineDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(database = dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
