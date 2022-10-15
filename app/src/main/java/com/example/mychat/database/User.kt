package com.example.mychat

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "PostTable")
data class User (
    val name:String,
    @PrimaryKey(autoGenerate = false)
    val id:String,
    val profilePic:String?=null
) : Parcelable

@Parcelize
data class Message(
    val msg:String,
    val name:String?,
    val id:String,
    val type:String,
    val time:String
) : Parcelable

@Parcelize
data class ImageList(
    var msgList:MutableList<Message>?
):Parcelable
//data class ImageMessage(
//
//)