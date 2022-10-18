package com.example.mychat.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychat.ImageList
import com.example.mychat.Message
import com.example.mychat.PostDatabase
import com.example.mychat.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    lateinit var database: PostDatabase
    private val firebaseDatabase = Firebase.database
    private val myRef = firebaseDatabase.reference.child("users")
     var me:User?=null

    fun putData(user:User){
        Log.i("anderchalo", "putData: $user")
        if(!userList.contains(user) && user!=me &&userList.isNotEmpty()) {
            Log.i("anderchalo", "putData: checked $user")
            userList.add(user)
            lastmyRef = myRef.push()
            lastmyRef.setValue(user)
        }
        if(user==me && !ogUserList.contains(user)){
            Log.i("anderchalo", "putData: checked 3  ${ogUserList}")
            lastmyRef = myRef.push()
            lastmyRef.setValue(user)
        }
    }
     var userList= mutableListOf<User>()
     var ogUserList= mutableListOf<User>()
    lateinit var lastmyRef:DatabaseReference
//    lateinit var list:MutableList<User>


    fun getUserList(){
            myRef.addValueEventListener(object: ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
                val value = snapshot.value as HashMap<String?, HashMap<String,String>>?
                Log.i("anderchalo", "putData: checked 3  ${value}")
                if (value != null) {
                    for ((key, vl) in value) {
                        val user= vl["name"]?.let { vl["id"]?.let { it1 -> User(it, it1) } }
                        if (user != null) {
                            ogUserList.add(user)
                        }
                        if(!userList.contains(user) && user != null && user!=me){
                            userList.add(user)
                            insertIntoDB(user)

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    fun insertIntoDB(user: User){
        viewModelScope.launch {
            try {

                database.contactDAO().insert(user)
            }catch (ex:Exception){
            }
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            database.contactDAO().deleteAll()
        }
    }

}

interface Call {
    fun itemClick(user: User)
    fun messageClick(msg: ImageList, pos:Int)
    fun msgData(message: Message)
    fun readReceipt(message: Message)

}