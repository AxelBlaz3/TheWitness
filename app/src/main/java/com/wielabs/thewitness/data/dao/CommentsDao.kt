package com.wielabs.thewitness.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wielabs.thewitness.model.Comment

@Dao
interface CommentsDao {

    @Query("SELECT * FROM Comment")
    fun load(): LiveData<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(comments: List<Comment>)

    @Delete
    fun delete(comments: List<Comment>)
}