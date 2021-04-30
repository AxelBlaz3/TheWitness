package com.wielabs.thewitness.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Comment(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int,
    @SerializedName("news_id") val newsId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("reply_id") val replyId: Int,
    @SerializedName("name") val author: String,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("is_liked") val isLiked: Boolean,
    @SerializedName("replies") val replies: List<Reply>,
    var lastFetched: Long = -1
) : Parcelable