package com.example.mychat.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.mychat.R
import com.example.mychat.User
import com.example.mychat.ui.main.MainViewModel
import com.smartlook.android.core.api.Smartlook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by lazy{
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref=this.getSharedPreferences("USER_STATE", Context.MODE_PRIVATE)
        val editor=sharedPref.edit()


        //smartLook Implementation
        val smartlook = Smartlook.instance
        smartlook.preferences.projectKey = "8a008c6b6fb32fb2a22f195b8fad519fccecdef0"
        smartlook.start()
        val smartlookInstance = Smartlook.instance
        Smartlook.instance.preferences.projectKey = "8a008c6b6fb32fb2a22f195b8fad519fccecdef0"
        val isUser=sharedPref.getBoolean("ALREADY_USER", false)
//        Smartlook.instance.start()


        if(isUser){
            CoroutineScope(Dispatchers.IO).launch{
                delay(500)
                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
                finish()
            }

//            editor.putBoolean("ALREADY_USER",true)
            //other
//            editor.putString("name", "Yami 8")
//            editor.putString("id","8")
//            val user= User("Yami 8","8")
            //me
//            editor.putString("name", "Sigma")
//            editor.putString("id","Test@1")
//            val user=User("Sigma","Test@1")
//            viewModel.putData(user)
        }
        else{
            CoroutineScope(Dispatchers.IO).launch{
                delay(500)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        editor.apply()

    }
}