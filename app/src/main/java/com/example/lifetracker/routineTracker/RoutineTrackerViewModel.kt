package com.example.lifetracker.routineTracker

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lifetracker.database.Prepopulation
import com.example.lifetracker.database.Routine
import com.example.lifetracker.database.RoutineDatabaseDao
import kotlinx.coroutines.*

class RoutineTrackerViewModel(val database: RoutineDatabaseDao) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentRoutine = MutableLiveData<Routine?>()

    private val _navigateToRoutineSave = MutableLiveData<Routine>()
    val navigateToRoutineSave: LiveData<Routine>
        get() = _navigateToRoutineSave

    fun doneNavigating() {
        _navigateToRoutineSave.value = null
    }

    val isStartButton = Transformations.map(currentRoutine) {
        it == null
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    init {
        initializeCurrentRoutine()
        _showSnackbarEvent.value = false
        //prepopulateDatabase()
    }

    private fun prepopulateDatabase() {
        uiScope.launch {
            _showSnackbarEvent.value = prepopulate()
        }
    }

    private suspend fun prepopulate(): Boolean {
        return withContext(Dispatchers.IO) {
            Prepopulation.populateInitialData(database)
            true
        }
    }

    private fun initializeCurrentRoutine() {
        uiScope.launch {
            currentRoutine.value = getLatestRoutineFromDatabase()
        }
    }

    private suspend fun getLatestRoutineFromDatabase(): Routine? {
        return withContext(Dispatchers.IO) {
            var routine = database.getLatestRoutine()
            if (routine?.endTimeMilli != routine?.startTimeMilli) {
                routine = null
            }
            routine
        }
    }



    private suspend fun updateRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            database.updateRoutine(routine)
        }
    }

    private suspend fun insertRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            database.insertRoutine(routine)
        }
    }

    fun onTracking(){
        if(isStartButton.value == true)
            onStartTracking()
        else
            onStopTracking()
    }

    private fun onStartTracking() {
        uiScope.launch {
            val newRoutine = Routine()
            insertRoutine(newRoutine)
            currentRoutine.value = getLatestRoutineFromDatabase()
        }
    }

    private fun onStopTracking() {
        uiScope.launch {
            //we are specifying to return from launch(),
            // not the lambda.
            val oldRoutine = currentRoutine.value ?: return@launch

            oldRoutine.endTimeMilli = System.currentTimeMillis()
            updateRoutine(oldRoutine)
            _navigateToRoutineSave.value = oldRoutine
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
