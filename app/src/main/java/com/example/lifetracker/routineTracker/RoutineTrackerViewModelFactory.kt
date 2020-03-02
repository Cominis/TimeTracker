package com.example.lifetracker.routineTracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.database.RoutineDatabaseDao

class RoutineTrackerViewModelFactory(private val dataSource: RoutineDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineTrackerViewModel::class.java)) {
            return RoutineTrackerViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
