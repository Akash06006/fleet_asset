package com.example.fleet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JobLocationsDetailsDao {
    @Query("SELECT * from job_loccations WHERE status==0 and  job_id=:jobId LIMIT 20")// Running Job
    fun getLimitLocationRecords(jobId : String) : List<JobLocationsDetails>

    @Query("SELECT * from job_loccations WHERE status==0 and job_id=:jobId")// Complete Job
    fun getAllLocationRecords(jobId : String) : List<JobLocationsDetails>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
      fun insert(word : JobLocationsDetails)

    @Query("DELETE FROM job_loccations")
    suspend fun deleteAll()

    @Query("UPDATE job_loccations SET status = :amount WHERE id =:id")
    fun update(amount : String?, id : Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJob(jobDetails : JobDetails)

    @Query("SELECT * from job_details WHERE status==0 and job_id=:jobId")// Complete Job
    fun getJob(jobId : String) : List<JobDetails>

    @Query("SELECT * from job_details WHERE status==0 LIMIT 1")// Complete Job
    fun getAllJobs() : List<JobDetails>

    @Query("UPDATE job_details SET complete_status = :jobStatus WHERE job_id =:id")
    fun updateJob(jobStatus : String?, id : String)

    @Query("UPDATE job_details SET status = :jobSynchStatus WHERE job_id =:id")
    fun updateJobSynchStatus(jobSynchStatus : String?, id : Int)

}