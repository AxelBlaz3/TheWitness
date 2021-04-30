package com.wielabs.thewitness.network

import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.News
import com.wielabs.thewitness.model.Profile
import retrofit2.Call
import retrofit2.http.*

interface TheWitnessService {

    @GET("news")
    fun getNews(@Query("user_id") userId: Int): Call<List<News>>

    @GET("comments/")
    fun getCommentsForUser(@Query("user_id") userId: Int): Call<List<Comment>>

    @GET("profile")
    fun getProfile(@Query("email") email: String, @Query("name") name: String?): Call<Profile>

    @POST("comment/new")
    @FormUrlEncoded
    fun postComment(
        @Field("news_id") newsId: Int,
        @Field("user_id") userId: Int,
        @Field("name") name: String,
        @Field("message") message: String
    ): Call<Any>

    @POST("reply/new")
    @FormUrlEncoded
    fun postReply(
        @Field("news_id") newsId: Int,
        @Field("user_id") userId: Int,
        @Field("name") name: String,
        @Field("message") message: String,
        @Field("comment_id") commentId: Int
    ): Call<Any>

    @PUT("comment/update/likes")
    @FormUrlEncoded
    fun updateCommentLikes(
        @Field("comment_id") commentId: Int,
        @Field("user_id") userId: Int,
        @Field("is_liked") isLiked: Int
    ): Call<Void>

    @PUT("reply/update/likes")
    @FormUrlEncoded
    fun updateReplyLikes(
        @Field("reply_id") replyId: Int,
        @Field("user_id") userId: Int,
        @Field("is_liked") isLiked: Int
    ): Call<Void>

    @PUT("news/update/likes")
    @FormUrlEncoded
    fun updateNewsLikes(
        @Field("news_id") newsId: Int,
        @Field("user_id") userId: Int,
        @Field("is_liked") isLiked: Int
    ): Call<Void>


}