package com.example.lifetracker.routineSave

import android.R.integer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lifetracker.convertLongToDateString
import com.example.lifetracker.database.Activity
import com.example.lifetracker.database.Routine
import com.example.lifetracker.database.RoutineDatabaseDao
import kotlinx.coroutines.*


class RoutineSaveViewModel(
        private val routineId: Long = 0L,
        val database: RoutineDatabaseDao
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val currentRoutine = MutableLiveData<Routine>()

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>>
        get() = _activities

    private val _currentActivity = MutableLiveData<Activity>(Activity(id = -1))
    val currentActivity: LiveData<Activity>
        get() = _currentActivity

    private val _isActivityNew = MutableLiveData<Boolean>(false)
    val isActivityNew: LiveData<Boolean>
        get() = _isActivityNew

    private val _navigateToRoutineTracker = MutableLiveData<Routine>(null)
    val navigateToRoutineTracker: LiveData<Routine>
        get() = _navigateToRoutineTracker

    fun doneNavigating() {
        _navigateToRoutineTracker.value = null
    }

    init {
        getAllActivities()
        initializeCurrentRoutine()
    }

    private fun getAllActivities() {
        uiScope.launch {
            _activities.value = getAllActivitiesFromDatabase()
        }
    }

    private suspend fun getAllActivitiesFromDatabase(): List<Activity> {
        return withContext(Dispatchers.IO) {
            database.getAllActivities()
        }
    }

    private fun initializeCurrentRoutine() {
        uiScope.launch {
            currentRoutine.value = getLatestRoutineFromDatabase()
        }
    }

    private suspend fun getLatestRoutineFromDatabase(): Routine? {
        return withContext(Dispatchers.IO) {
            database.getLatestRoutine()
        }
    }

    val startTime = Transformations.map(currentRoutine) {
        convertLongToDateString(it?.startTimeMilli ?: 9999999999L)
    }

    val endTime = Transformations.map(currentRoutine) {
        convertLongToDateString(it?.endTimeMilli ?: 99999999999L)
    }

    fun updateCurrentActivityId(id : Long){
        _currentActivity.value?.id = id
    }

    private suspend fun updateRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            database.updateRoutine(routine)
        }
    }

    private suspend fun insertActivity(activity: Activity) : Long {
        return withContext(Dispatchers.IO) {
            database.insertActivity(activity)
        }
    }

    private suspend fun updateActivity(activity: Activity) {
        withContext(Dispatchers.IO) {
            database.updateActivity(activity)
        }
    }

    fun saveRoutine(){
        uiScope.launch {
            if(isActivityNew.value == true){
                _currentActivity.value?.let {
                    it.id = insertActivity(it)
                }
            }

            val updatedRoutine = currentRoutine.value ?: return@launch
            updatedRoutine.activityId = currentActivity.value?.id
            updateRoutine(updatedRoutine)

            _navigateToRoutineTracker.value = updatedRoutine
        }
    }

    fun evaluateActivity(activityName : String) {
        uiScope.launch {
            _currentActivity.value = getActivityFromDatabase(_currentActivity.value?.id ?: -1) ?: Activity(id = -1)
            _isActivityNew.value =  activityName != _currentActivity.value?.name
            if(_isActivityNew.value == true){
                 _currentActivity.value = Activity(name = activityName)
            }
        }
    }

    private suspend fun getActivityFromDatabase(id : Long): Activity? {
        return withContext(Dispatchers.IO) {
            database.getActivity(id)
        }
    }

    fun updateIsNewActivityValue(value : Boolean = false) {
        _isActivityNew.value = value
    }
}

