package com.wielabs.thewitness.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Reply(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int,
    @SerializedName("news_id") val newsId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("comment_id") val commentId: Int,
    @SerializedName("name") val author: String,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("is_liked") val isLiked: Boolean,
) : Parcelable