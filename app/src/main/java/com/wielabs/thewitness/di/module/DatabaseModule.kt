package com.wielabs.thewitness.di.module

import android.content.Context
import androidx.room.Room
import com.wielabs.thewitness.data.TheWitnessDatabase
import com.wielabs.thewitness.data.dao.CommentsDao
import com.wielabs.thewitness.data.dao.NewsDao
import com.wielabs.thewitness.data.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    /**
     * Provides single TheWitnessDatabase instance throughout the app's lifecycle.
     * @return [TheWitnessDatabase]
     */
    @Singleton
    @Provides
    fun provideTheWitnessDatabase(@ApplicationContext context: Context): TheWitnessDatabase {
        return Room.databaseBuilder(
            context,
            TheWitnessDatabase::class.java,
            "the_witness.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideNewsDao(theWitnessDatabase: TheWitnessDatabase): NewsDao =
        theWitnessDatabase.newsDao()

    @Singleton
    @Provides
    fun provideCommentsDao(theWitnessDatabase: TheWitnessDatabase): CommentsDao =
        theWitnessDatabase.commentsDao()

    @Singleton
    @Provides
    fun provideProfileDao(theWitnessDatabase: TheWitnessDatabase): ProfileDao =
        theWitnessDatabase.profileDao()
}