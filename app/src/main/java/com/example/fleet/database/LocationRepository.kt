package com.example.fleet.database

import com.example.fleet.application.MyApplication

class LocationRepository() {
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    /* val limitLocations : List<JobLocationsDetails> = jobLocationsDetailsDao.getLimitLocationRecords(1)
     val allLocations : List<JobLocationsDetails> = jobLocationsDetailsDao.getAllLocationRecords(1)
     */
    var jobLocationsDetailsDao : JobLocationsDetailsDao

    init {
        jobLocationsDetailsDao =
            LocationsDetailRoomDatabase.getDatabase(MyApplication.instance).jobLocationsDetailsDao()
    }

      fun insert(jobLocations : JobLocationsDetails) {
        jobLocationsDetailsDao.insert(jobLocations)
    }

    fun getLimitedRecords(id : String) : List<JobLocationsDetails> {
        return jobLocationsDetailsDao.getLimitLocationRecords(id)
    }

    fun update(status : String, id : Int) {
        jobLocationsDetailsDao.update(status, id)
    }

    fun getAllRecords(id : String) : List<JobLocationsDetails> {
        return jobLocationsDetailsDao.getAllLocationRecords(id)
    }

    suspend fun insertJob(jobLocations : JobDetails) {
        jobLocationsDetailsDao.insertJob(jobLocations)
    }

    fun getAllJob() : List<JobDetails> {
        return jobLocationsDetailsDao.getAllJobs()
    }

    fun getJob(id : String) : List<JobDetails> {
        return jobLocationsDetailsDao.getJob(id)
    }

    fun getAllJobs() : List<JobDetails> {
        return jobLocationsDetailsDao.getAllJobs()
    }

    fun updateJOb(status : String, id : String) {
        jobLocationsDetailsDao.updateJob(status, id)
    }

    fun updateJobSynchStatus(status : String, id : Int) {
        jobLocationsDetailsDao.updateJobSynchStatus(status, id)
    }

}