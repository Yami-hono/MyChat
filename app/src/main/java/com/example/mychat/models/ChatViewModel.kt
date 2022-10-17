package com.example.mychat.models

import android.util.Log
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
    lateinit var me: User
    lateinit var replyMsg:Message

    var messageList=MutableLiveData<List<Message>>()

    fun putMessage(msg: Message){

        if(chatId!="") {
            myRef.child(chatId).push().setValue(msg)
        }
    }
//    var msgArr= arrayListOf<Message>()
    var msgArr= mutableListOf<Message>()
    var msgValue=HashMap<String?, HashMap<String, String>>()

    fun getMessageList() {
            myRef.child(chatId).addValueEventListener(object : ValueEventListener {
                val words = chatId.split("\\p{Space}".toRegex()).toTypedArray()

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value as HashMap<String?, HashMap<String, String>>?
                    if (value != null) {
                        msgValue=value
                    }
                    msgArr.clear()

                    if (value != null) {
                        for ((key, vl) in value) {
                            val msg = vl["msg"]?.let {
                                vl["id"]?.let { it1 ->
                                    vl["type"]?.let { it2 ->
                                        vl["time"]?.let { it3 ->
                                            vl["read"]?.let { it4 ->
                                                Message(
                                                    it, vl["name"],
                                                    it1, it2,
                                                    it3, it4
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Log.i("isRead", "onDataChange: $value")
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

    fun sendReadReceipt(message:Message){
        for ((key, vl) in msgValue) {
            if(vl["name"]==message.name && vl["time"]==message.time &&
                vl["type"]==message.type && vl["id"] ==message.id
            ){
                vl["read"]="TRUE"
            }

        }

//        msgArr[pos].isRead="TRUE"
        myRef.child(chatId).setValue(msgValue)

    }
}