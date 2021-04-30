package com.wielabs.thewitness.data.repository

import androidx.lifecycle.LiveData
import com.wielabs.thewitness.data.dao.ProfileDao
import com.wielabs.thewitness.model.Profile
import com.wielabs.thewitness.network.TheWitnessService
import com.wielabs.thewitness.util.SharedPreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val theWitnessService: TheWitnessService,
    private val profileDao: ProfileDao,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    val profile: LiveData<Profile?> = profileDao.load()

    suspend fun getProfile(email: String, name: String?) = withContext(Dispatchers.IO) {
        try {
            val response = theWitnessService.getProfile(email = email, name = name).execute()
            response.run {
                if (isSuccessful)
                    body()?.let { profile ->
                        profileDao.run {
                            this@ProfileRepository.profile.value?.let {
                                delete(it)
                            }
                            save(profile)
                            sharedPreferenceUtils.setIsSignedIn(isSignedIn = true)
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteProfile() = withContext(Dispatchers.IO) {
        profile.value?.let { profile ->
            profileDao.delete(profile = profile)
        }
    }
}