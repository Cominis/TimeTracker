package com.dmt.timetracker.viewmodels

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.dmt.timetracker.database.Routine
import com.dmt.timetracker.repository.TimeRepository
import kotlinx.coroutines.*

class RoutineTrackerViewModel(private val repo: TimeRepository, private val context: Context) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val currentRoutine = MutableLiveData<Routine>(null)
    val isStart = Transformations.map(currentRoutine) { it == null }

    private val _navigateToRoutineSave = MutableLiveData<Routine>()
    val navigateToRoutineSave: LiveData<Routine>
        get() = _navigateToRoutineSave

    fun doneNavigating() { _navigateToRoutineSave.value = null }

    fun getCurrentDuration() : Long {
        currentRoutine.value?.let {
            return System.currentTimeMillis() - it.startTimeMilli
        }

        return 0L
    }

    val latestRoutine = repo.latestRoutine

    fun updateCurrentRoutine(value: Routine?) {
        if(currentRoutine.value != value)
            currentRoutine.value = value
    }

    private suspend fun insertRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            repo.insert(routine)
        }
    }

    fun onTracking(){
        if(isStart.value == true) onStartTracking()
        else if(isStart.value == false) onStopTracking()
    }

    private val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)

    private fun onStartTracking() {
        uiScope.launch {
            val newRoutine = Routine()
            insertRoutine(newRoutine)

//            val widgetIds = appWidgetManager.getAppWidgetIds(
//                ComponentName(context, MiniTracker::class.java)
//            )
//
//            if(widgetIds.isNotEmpty())
//                updateAppWidget(context, appWidgetManager, widgetIds[0], true)
        }
    }

    private fun onStopTracking() {
        uiScope.launch {
            _navigateToRoutineSave.value = currentRoutine.value ?: return@launch
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
