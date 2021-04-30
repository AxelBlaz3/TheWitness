package com.wielabs.thewitness.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wielabs.thewitness.data.dao.CommentsDao
import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.Reply
import com.wielabs.thewitness.network.TheWitnessService
import com.wielabs.thewitness.util.SharedPreferenceUtils
import com.wielabs.thewitness.util.toInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val witnessService: TheWitnessService,
    private val commentsDao: CommentsDao
) {
    val comments: LiveData<List<Comment>> = commentsDao.load()
    val isCommentPosted: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isCommentLikeUpdated: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isReplyLikeUpdated: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isReplyPosted: MutableLiveData<Boolean?> = MutableLiveData(null)

    suspend fun getComments(userId: Int, forceRefresh: Boolean) =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    comments.value?.let { it ->
                        it.getOrNull(0)?.let { comment ->
                                if (it.isNotEmpty() && comment.lastFetched != -1L && Date().time - comment.lastFetched < TimeUnit.DAYS.toMillis(
                                        1
                                    )
                                )
                                    return@withContext
                            }
                    }
                }
                val response = witnessService.getCommentsForUser(userId = userId).execute()
                response.run {
                    if (isSuccessful)
                        body()?.let { comments ->
                            commentsDao.run {
                                val lastFetched = Date().time
                                if (comments.isNotEmpty())
                                    save(comments = comments.map { comment ->
                                        comment.lastFetched = lastFetched
                                        comment
                                    })
                                else
                                    this@CommentRepository.comments.value?.let { comments ->
                                        delete(comments)
                                    }
                            }
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun postComment(newsId: Int, userId: Int, name: String, message: String) =
        withContext(Dispatchers.IO) {
            try {
                val response = witnessService.postComment(newsId, userId, name, message).execute()
                response.run {
                    isCommentPosted.postValue(code() == 201)
                    return@withContext
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isCommentPosted.postValue(false)
        }

    suspend fun updateCommentLikes(comment: Comment, userId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = witnessService.updateCommentLikes(
                commentId = comment.id,
                userId = userId,
                isLiked = (!comment.isLiked).toInt()
            ).execute()
            response.run {
                isCommentLikeUpdated.postValue(code() == 204)
                return@withContext
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isCommentLikeUpdated.postValue(false)
    }

    suspend fun postReply(newsId: Int, userId: Int, name: String, message: String, commentId: Int) =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    witnessService.postReply(newsId, userId, name, message, commentId).execute()
                response.run {
                    isReplyPosted.postValue(code() == 201)
                    return@withContext
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isReplyPosted.postValue(false)
        }

    suspend fun updateReplyLikes(reply: Reply, userId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = witnessService.updateReplyLikes(
                replyId = reply.id,
                userId = userId,
                isLiked = (!reply.isLiked).toInt()
            ).execute()
            response.run {
                isReplyLikeUpdated.postValue(code() == 204)
                return@withContext
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isReplyLikeUpdated.postValue(false)
    }

    suspend fun deleteComments() = withContext(Dispatchers.IO){
        comments.value?.let { comments ->
            commentsDao.delete(comments = comments)
        }
    }
}