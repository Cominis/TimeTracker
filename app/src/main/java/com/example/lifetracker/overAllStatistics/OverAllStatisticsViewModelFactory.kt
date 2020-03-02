package com.example.lifetracker.overAllStatistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.database.RoutineDatabaseDao

class OverAllStatisticsViewModelFactory(
        private val dataSource: RoutineDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverAllStatisticsViewModel::class.java)) {
            return OverAllStatisticsViewModel(database = dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
