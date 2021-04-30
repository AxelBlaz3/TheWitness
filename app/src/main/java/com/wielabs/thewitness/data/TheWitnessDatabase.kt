package com.wielabs.thewitness.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wielabs.thewitness.data.dao.CommentsDao
import com.wielabs.thewitness.data.dao.NewsDao
import com.wielabs.thewitness.data.dao.ProfileDao
import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.News
import com.wielabs.thewitness.model.Profile
import com.wielabs.thewitness.model.Reply
import com.wielabs.thewitness.util.Converters

@Database(entities = [News::class, Comment::class, Profile::class, Reply::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TheWitnessDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun commentsDao(): CommentsDao
    abstract fun profileDao(): ProfileDao
}