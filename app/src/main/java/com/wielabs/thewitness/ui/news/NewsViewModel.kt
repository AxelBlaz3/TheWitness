package com.wielabs.thewitness.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wielabs.thewitness.data.repository.CommentRepository
import com.wielabs.thewitness.data.repository.NewsRepository
import com.wielabs.thewitness.data.repository.ProfileRepository
import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.News
import com.wielabs.thewitness.model.Reply
import com.wielabs.thewitness.util.SharedPreferenceUtils
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val commentRepository: CommentRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val news: LiveData<List<News>> = newsRepository.news
    private val comments: LiveData<List<Comment>> = commentRepository.comments
    private val isCommentPosted: MutableLiveData<Boolean?> = commentRepository.isCommentPosted
    private val isNewsLikeUpdated: MutableLiveData<Boolean?> = newsRepository.isNewsLikeUpdated
    private val isCommentLikeUpdated: MutableLiveData<Boolean?> =
        commentRepository.isCommentLikeUpdated
    private val isReplyLikeUpdated: MutableLiveData<Boolean?> = commentRepository.isReplyLikeUpdated
    private val isReplyPosted: MutableLiveData<Boolean?> = commentRepository.isReplyPosted
    private val suggestReload: MutableLiveData<Boolean> = MutableLiveData(false)
    private val lastFetched: MutableLiveData<Long> = newsRepository.lastFetched
    private val isCommentEmpty: MutableLiveData<Boolean?> = MutableLiveData(null)

    init {
        profileRepository.profile.value?.let { profile ->
            refreshNews(userId = profile.id, forceRefresh = false)
        }
    }

    fun getSuggestReload(): LiveData<Boolean> = suggestReload
    fun getNews(): LiveData<List<News>> = news
    fun getLastFetched(): LiveData<Long> = lastFetched
    fun getIsCommentEmpty(): LiveData<Boolean?> = isCommentEmpty

    fun refreshNews(userId: Int, forceRefresh: Boolean) {
        viewModelScope.launch {
            newsRepository.refreshNews(userId = userId, forceRefresh = forceRefresh)
        }
    }

    fun refreshComments(userId: Int, forceRefresh: Boolean) {
        viewModelScope.launch {
            commentRepository.getComments(
                userId = userId,
                forceRefresh = forceRefresh
            )
        }
    }

    fun getComments(): LiveData<List<Comment>> = comments
    fun getIsNewsLikeUpdated(): LiveData<Boolean?> = isNewsLikeUpdated
    fun getIsCommentLikeUpdated(): LiveData<Boolean?> = isCommentLikeUpdated
    fun getIsCommentPosted(): LiveData<Boolean?> = isCommentPosted
    fun getIsReplyPosted(): LiveData<Boolean?> = isReplyPosted
    fun getIsReplyLikesUpdated(): LiveData<Boolean?> = isReplyLikeUpdated

    fun setIsCommentEmpty(newValue: Boolean?) {
        isCommentEmpty.value = newValue
    }

    fun setSuggestReload(newValue: Boolean) {
        suggestReload.value = newValue
    }

    fun setIsNewsLikeUpdated(newValue: Boolean?) {
        isNewsLikeUpdated.postValue(newValue)
    }

    fun setIsCommentPosted(newValue: Boolean?) {
        isCommentPosted.value = newValue
    }

    fun setIsReplyPosted(newValue: Boolean?) {
        isReplyPosted.value = newValue
    }

    fun setIsReplyLikeUpdated(newValue: Boolean?) {
        isReplyLikeUpdated.value = newValue
    }

    fun setIsCommentLikeUpdated(newValue: Boolean?) {
        isCommentLikeUpdated.value = newValue
    }

    fun updateNewsLikes(news: News, userId: Int) {
        viewModelScope.launch {
            newsRepository.updateNewsLikes(news = news, userId = userId)
        }
    }

    fun updateCommentLikes(comment: Comment, userId: Int) {
        viewModelScope.launch {
            commentRepository.updateCommentLikes(comment = comment, userId = userId)
        }
    }

    fun updateReplyLikes(reply: Reply, userId: Int) {
        viewModelScope.launch {
            commentRepository.updateReplyLikes(reply = reply, userId = userId)
        }
    }

    fun postComment(userId: Int, newsId: Int, name: String, message: String) {
        viewModelScope.launch {
            isCommentEmpty.postValue(message.isEmpty())
            if (message.isEmpty())
                return@launch
            commentRepository.postComment(newsId, userId, name, message)
        }
    }

    fun postReply(userId: Int, comment: Comment?, reply: Reply?, name: String, message: String) {
        viewModelScope.launch {
            isCommentEmpty.postValue(message.isEmpty())
            if (message.isEmpty())
                return@launch
            comment?.let { comment ->
                commentRepository.postReply(
                    newsId = comment.newsId,
                    userId = userId,
                    name = name,
                    message = message,
                    commentId = comment.id
                )
            }
            reply?.let { comment ->
                commentRepository.postReply(
                    newsId = reply.newsId,
                    userId = userId,
                    name = name,
                    message = message,
                    commentId = reply.commentId
                )
            }
        }
    }
}