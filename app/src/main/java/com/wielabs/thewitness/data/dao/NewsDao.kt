package com.wielabs.thewitness.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wielabs.thewitness.model.News

@Dao
interface NewsDao {

    @Query("SELECT * FROM News")
    fun load(): LiveData<List<News>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(news: List<News>)

    @Delete
    fun delete(news: List<News>)
}