package com.wielabs.thewitness.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Profile(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
)