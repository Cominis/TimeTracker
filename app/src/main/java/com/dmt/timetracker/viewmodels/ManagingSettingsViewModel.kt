package com.dmt.timetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmt.timetracker.repository.TimeRepository
import kotlinx.coroutines.*

class ManagingSettingsViewModel(private val repo: TimeRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _showSnackBarEvent = MutableLiveData(false)
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackBarEvent

    fun doneShowingSnackBar() {
        _showSnackBarEvent.value = false
    }

    fun deleteCurrentRoutine() {
        uiScope.launch {
            var routine = repo.getLatestRoutine()
            if (routine?.endTimeMilli != routine?.startTimeMilli) {
                routine = null
            }
            if(routine != null) {
                repo.delete(routine)
                _showSnackBarEvent.value = true
            } else {
                _showSnackBarEvent.value = false
            }
        }
    }

    fun deleteAll() {
        uiScope.launch {
            repo.eraseAll()
            _showSnackBarEvent.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
