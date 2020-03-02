package com.example.lifetracker.statisticsSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.database.RoutineDatabaseDao

class StatisticsSettingsViewModelFactory(
        private val routineId: Long,
        private val dataSource: RoutineDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsSettingsViewModel::class.java)) {
            return StatisticsSettingsViewModel(routineId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
