package com.example.mychat.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        if(!userList.contains(user) && user!=me &&userList.isNotEmpty()) {
            userList.add(user)
            lastmyRef = myRef.push()
            lastmyRef.setValue(user)
        }
    }
     var userList= mutableListOf<User>()
    lateinit var lastmyRef:DatabaseReference
//    lateinit var list:MutableList<User>


    fun getUserList(){
            myRef.addValueEventListener(object: ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
                val value = snapshot.value as HashMap<String?, HashMap<String,String>>?
                if (value != null) {
                    for ((key, vl) in value) {
                        val user= vl["name"]?.let { vl["id"]?.let { it1 -> User(it, it1) } }
                        Log.i("myUser", "onDataChange: $user---$me")
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

}