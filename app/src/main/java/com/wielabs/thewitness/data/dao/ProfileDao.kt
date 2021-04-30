package com.wielabs.thewitness.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wielabs.thewitness.model.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM Profile LIMIT 1")
    fun load(): LiveData<Profile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(profile: Profile)

    @Delete
    fun delete(profile: Profile)
}