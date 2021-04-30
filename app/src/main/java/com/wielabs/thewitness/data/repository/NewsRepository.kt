package com.wielabs.thewitness.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wielabs.thewitness.data.dao.NewsDao
import com.wielabs.thewitness.model.News
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
class NewsRepository @Inject constructor(
    private val theWitnessService: TheWitnessService,
    private val newsDao: NewsDao,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    val news: LiveData<List<News>> = newsDao.load()
    val isNewsLikeUpdated: MutableLiveData<Boolean?> = MutableLiveData(null)
    val lastFetched: MutableLiveData<Long> =
        MutableLiveData(sharedPreferenceUtils.getNewsLastFetched())

    suspend fun refreshNews(userId: Int, forceRefresh: Boolean) = withContext(Dispatchers.IO) {
        try {
            if (!forceRefresh) {
                news.value?.let { it ->
                    it.getOrNull(0)?.let { news ->
                        if (news.lastFetched != -1L && Date().time - news.lastFetched < TimeUnit.DAYS.toMillis(
                                1
                            )
                        )
                            return@withContext
                    }
                }
            }
            val response = theWitnessService.getNews(userId = userId).execute()
            response.run {
                if (isSuccessful)
                    body()?.let { news ->
                        newsDao.run {
                            val lastFetched = Date().time
                            if (news.isNotEmpty())
                                save(news.map { news ->
                                    news.lastFetched = lastFetched
                                    news
                                })
                            else
                                this@NewsRepository.news.value?.let { news ->
                                    delete(news = news)
                                }
                            sharedPreferenceUtils.setNewsLastFetched(time = lastFetched)
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateNewsLikes(news: News, userId: Int) = withContext(Dispatchers.IO) {
        try {
            val response =
                theWitnessService.updateNewsLikes(
                    newsId = news.id,
                    userId = userId,
                    isLiked = (!news.isLiked).toInt()
                )
                    .execute()
            response.run {
                isNewsLikeUpdated.postValue(code() == 204)
                return@withContext
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isNewsLikeUpdated.postValue(false)
    }

    suspend fun deleteNews() = withContext(Dispatchers.IO){
        news.value?.let { news ->
            newsDao.delete(news = news)
            lastFetched.postValue(-1)
        }
    }
}