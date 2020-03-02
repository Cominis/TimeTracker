package com.example.lifetracker.routineSave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.database.RoutineDatabaseDao

class RoutineSaveViewModelFactory(
        private val routineId: Long,
        private val dataSource: RoutineDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineSaveViewModel::class.java)) {
            return RoutineSaveViewModel(routineId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
