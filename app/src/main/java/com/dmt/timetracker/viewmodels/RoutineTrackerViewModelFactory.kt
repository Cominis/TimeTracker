package com.dmt.timetracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmt.timetracker.repository.TimeRepository

class RoutineTrackerViewModelFactory(
    private val repo: TimeRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineTrackerViewModel::class.java)) {
            return RoutineTrackerViewModel(repo, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
