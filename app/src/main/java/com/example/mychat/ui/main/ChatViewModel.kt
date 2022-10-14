package com.example.mychat.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.Message
import com.example.mychat.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel:ViewModel() {

    private val firebaseDatabase = Firebase.database
    private val myRef = firebaseDatabase.reference.child("messages")
    private lateinit var lastmyRef: DatabaseReference
    var chatId=""
    lateinit var me:User

    var messageList=MutableLiveData<List<Message>>()

    fun putMessage(msg: Message){

        if(chatId!="") {
            myRef.child(chatId).push().setValue(msg)
        }
    }
//    var msgArr= arrayListOf<Message>()
    var msgArr= mutableListOf<Message>()

    fun getMessageList() {
            myRef.child(chatId).addValueEventListener(object : ValueEventListener {
                val words = chatId.split("\\p{Space}".toRegex()).toTypedArray()

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value as HashMap<String?, HashMap<String, String>>?
                    Log.i("messageItems", "onDataChange: $value")

                    msgArr.clear()

                    if (value != null) {
                        for ((key, vl) in value) {
                            val msg = vl["msg"]?.let {
                                vl["id"]?.let { it1 ->
                                    vl["type"]?.let { it2 ->
                                        vl["time"]?.let { it3 ->
                                            Message(
                                                it, vl["name"],
                                                it1, it2,
                                                it3
                                            )
                                        }
                                    }
                                }
                            }
                            if (msg != null) {
                                msgArr.add(msg)
                            }
                        }
                    }
                    msgArr.sortByDescending { it.time }
                    msgArr.reverse()
                    messageList.value = msgArr
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}