package com.wielabs.thewitness.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val WITNESS_SHARED_PREF = "com.wielabs.thewitness.SHARED_PREF"
private const val KEY_LAST_FETCHED = "com.wielabs.thewitness.KEY_LAST_FETCHED"
private const val KEY_SIGNED_IN = "com.wielabs.thewitness.KEY_SIGNED_IN"

@Singleton
class SharedPreferenceUtils @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(WITNESS_SHARED_PREF, Context.MODE_PRIVATE)

    fun getNewsLastFetched(): Long =
        sharedPreferences.getLong(KEY_LAST_FETCHED, -1)

    fun setNewsLastFetched(time: Long) {
        sharedPreferences.edit().putLong(KEY_LAST_FETCHED, time).apply()
    }

    fun getIsSignedIn() =
        sharedPreferences.getBoolean(KEY_SIGNED_IN, false)

    fun setIsSignedIn(isSignedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SIGNED_IN, isSignedIn).apply()
    }
}