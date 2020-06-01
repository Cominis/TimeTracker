package com.dmt.timetracker.viewmodels

import androidx.lifecycle.*
import com.dmt.timetracker.database.Activity
import com.dmt.timetracker.database.Routine
import com.dmt.timetracker.repository.TimeRepository
import com.dmt.timetracker.utils.millisToString
import kotlinx.coroutines.*
import java.time.Instant


class RoutineSaveViewModel(
        val repo: TimeRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val currentRoutine = MutableLiveData<Routine>()

    private val _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>>
        get() = _activities

    private var _currentActivity = Activity()
    val currentActivity: Activity
        get() = _currentActivity

    private val _currentName = MutableLiveData("")
    private val currentName: LiveData<String>
        get() = _currentName

    private val _iconChanged = MutableLiveData(false)
    val iconChanged: LiveData<Boolean>
        get() = _iconChanged

    fun doneChangingIcon() {
        _iconChanged.value = false
    }

    private val _isKeyboardOpen = MutableLiveData(false)
    val isKeyboardOpen: LiveData<Boolean>
        get() = _isKeyboardOpen

    private val _isNewEntry = MutableLiveData(false)
    val isNewEntry: LiveData<Boolean>
        get() = _isNewEntry

    private val _navigateToRoutineTracker = MutableLiveData<Routine>(null)
    val navigateToRoutineTracker: LiveData<Routine>
        get() = _navigateToRoutineTracker

    fun doneNavigating() {
        _navigateToRoutineTracker.value = null
    }

    val saveEnabled = MediatorLiveData<Boolean>()

    init {
        saveEnabled.addSource(currentName) { text ->
            saveEnabled.value = combineLatestData(text, isKeyboardOpen)
        }
        saveEnabled.addSource(isKeyboardOpen) { isOpen ->
            saveEnabled.value = combineLatestData(currentName, isOpen)
        }

        getAllActivities()
        initializeCurrentRoutine()
    }

    private fun combineLatestData(
        activityName: String,
        isKeyboardOpen: LiveData<Boolean>
    ): Boolean {

        val isOpen = isKeyboardOpen.value ?: false
        return !activityName.isBlank() && !isOpen
    }

    private fun combineLatestData(
        activityName: LiveData<String>,
        isKeyboardOpen: Boolean
    ): Boolean {

        val name = activityName.value ?: ""
        return !name.isBlank() && !isKeyboardOpen
    }

    private fun getAllActivities() {
        uiScope.launch {
            _activities.value = repo.getAllActivities()
        }
    }

    private fun initializeCurrentRoutine() {
        uiScope.launch {
            currentRoutine.value = repo.getLatestRoutine()
        }
    }

    val startTime = Transformations.map(currentRoutine) {
        if(currentRoutine.value != null)
            millisToString(
                it.startTimeMilli,
                "yyyy-MM-dd\n HH:mm:ss"
            )
        else
            "-"
    }

    val endTime = Transformations.map(currentRoutine) {
        if(currentRoutine.value != null)
            millisToString(
                Instant.now().toEpochMilli(), "yyyy-MM-dd\n HH:mm:ss"
            )
        else
            "-"
    }

    private suspend fun updateRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            repo.update(routine)
        }
    }

    private suspend fun insertActivity(activity: Activity) : Long {
        return withContext(Dispatchers.IO) {
            repo.insert(activity)[0]
        }
    }

    private suspend fun updateActivity(activity: Activity) {
        withContext(Dispatchers.IO) {
            repo.update(activity)
        }
    }

    fun updateIcon(name: String) {
        currentActivity.imageName = name
    }

    fun saveRoutine(){  //todo
        uiScope.launch {
            if(isNewEntry.value == true){
                currentActivity.id = 0
                currentActivity.id = insertActivity(currentActivity)
            }

            else if(isNewEntry.value == false)
                updateActivity(currentActivity)

            val updatedRoutine = currentRoutine.value ?: return@launch

            updatedRoutine.activityId = currentActivity.id
            updatedRoutine.endTimeMilli = System.currentTimeMillis()

            updateRoutine(updatedRoutine)

            _navigateToRoutineTracker.value = updatedRoutine
        }
    }

    fun evaluateActivity() {
        uiScope.launch {
            _currentName.value?.let { currentName ->
                when {
                    currentName.isBlank() -> {
                        _currentActivity.id = -1
                        _currentActivity.name = currentName
                        _currentActivity.imageName = ""

                        _isNewEntry.value =  false
                        _iconChanged.value = true
                    }
                    currentName == _currentActivity.name -> { }
                    else -> {
                        val activity = getActivityByNameFromDatabase(currentName)
                        if(activity == null){
                            _currentActivity.id = -1
                            _currentActivity.name = currentName
                            _currentActivity.imageName = ""
                            _isNewEntry.value = true
                        } else {
                            _currentActivity = activity
                            _isNewEntry.value = false
                        }
                        _iconChanged.value = true
                    }
                }
            }
        }
    }

    private suspend fun getActivityByNameFromDatabase(name : String): Activity? {
        return withContext(Dispatchers.IO) {
            repo.getActivityByName(name)
        }
    }
    fun updateCurrentActivity(activity: Activity){
        _currentActivity = activity.copy()
        updateCurrentName(activity.name)
        _iconChanged.value = true
        _isNewEntry.value = false
    }

    fun updateCurrentName(name: String){
        _currentName.value = name
    }

    fun updateKeyboardStatus(isOpen: Boolean){
        _isKeyboardOpen.value = isOpen
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

