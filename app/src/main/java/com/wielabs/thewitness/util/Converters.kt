package com.wielabs.thewitness.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.Reply

object Converters {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromComments(comments: List<Comment>): String =
        if (comments.isNullOrEmpty())
            ""
        else
            gson.toJson(comments)

    @TypeConverter
    @JvmStatic
    fun toComments(commentsJson: String) =
        if (commentsJson.isEmpty())
            emptyList<Comment>()
        else
            gson.fromJson(commentsJson, object : TypeToken<List<Comment>>() {}.type)

    @TypeConverter
    @JvmStatic
    fun fromReplies(replies: List<Reply>): String =
        if (replies.isNullOrEmpty())
            ""
        else
            gson.toJson(replies)

    @TypeConverter
    @JvmStatic
    fun toReplies(repliesJson: String) =
        if (repliesJson.isEmpty())
            emptyList<Reply>()
        else
            gson.fromJson(repliesJson, object : TypeToken<List<Reply>>() {}.type)

    @TypeConverter
    @JvmStatic
    fun fromString(tags: List<String>): String =
        if (tags.isNullOrEmpty())
            ""
        else
            gson.toJson(tags)

    @TypeConverter
    @JvmStatic
    fun toString(tagsJson: String) =
        if (tagsJson.isEmpty())
            emptyList<String>()
        else
            gson.fromJson(tagsJson, object : TypeToken<List<String>>() {}.type)
}