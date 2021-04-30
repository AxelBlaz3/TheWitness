package com.wielabs.thewitness.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wielabs.thewitness.data.repository.CommentRepository
import com.wielabs.thewitness.data.repository.NewsRepository
import com.wielabs.thewitness.data.repository.ProfileRepository
import com.wielabs.thewitness.model.Profile
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MainActivityViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val newsRepository: NewsRepository,
    private val commentRepository: CommentRepository
) :
    ViewModel() {
    private val profile = profileRepository.profile
    var videoUri: String = ""
    var currentPosition = 0L

    fun getProfile(): LiveData<Profile?> = profile

    fun refreshProfile(email: String, name: String?) {
        viewModelScope.launch {
            profileRepository.getProfile(email = email, name = name)
        }
    }

    // Logout user.
    fun deleteSession() {
        viewModelScope.launch {
            profileRepository.deleteProfile()
            newsRepository.deleteNews()
            commentRepository.deleteComments()
        }
    }
}