package com.example.fleet.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationsViewModel(application : Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository : LocationRepository
    // LiveData gives us updated words when they change.
    /*   val allLocations : List<JobLocationsDetails>
       val limitLocations : List<JobLocationsDetails>*/

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val jobLocationsDetailsDao =
            LocationsDetailRoomDatabase.getDatabase(application).jobLocationsDetailsDao()
        repository = LocationRepository()

    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(locationDetail : JobLocationsDetails) = viewModelScope.launch {
        repository.insert(locationDetail)
    }

    fun update(status : String, id : Int) = viewModelScope.launch {
        repository.update(status, id)
    }

    fun getAllRecords(id : String) : List<JobLocationsDetails> {
        return repository.getAllRecords(id)
    }

    fun getLimitRecords(id : String) : List<JobLocationsDetails> {
        return repository.getLimitedRecords(id)
    }

    fun insertJob(locationDetail : JobDetails) = viewModelScope.launch {
        repository.insertJob(locationDetail)
    }

    fun getJob(id : String) : List<JobDetails> {
        return repository.getJob(id)
    }

    fun getAllJobs() : List<JobDetails> {
        return repository.getAllJob()
    }

    fun updateJobStatus(completeStatus : String, id : String) = viewModelScope.launch {
        repository.updateJOb(completeStatus, id)
    }

    fun updateJobSynchStatus(completeStatus : String, id : Int) = viewModelScope.launch {
        repository.updateJobSynchStatus(completeStatus, id)
    }
}