package com.wielabs.thewitness.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.sql.Time

@Parcelize
@Entity
data class News(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("region") val region: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("video_url") val videoUrl: String?,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("comments_count") val commentsCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("comments") val comments: List<Comment>,
    @SerializedName("flag_url") val flagUrl: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("video_thumbnail") val videoThumbnail: String?,
    @SerializedName("article_link") val articleLink: String,
    var lastFetched: Long = -1
) : Parcelable