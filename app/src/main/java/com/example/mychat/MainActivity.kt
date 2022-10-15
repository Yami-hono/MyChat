package com.example.mychat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.mychat.ui.main.MainViewModel
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
//        setContentView(R.layout.activity_main)

        val sharedPref=this.getSharedPreferences("USER_STATE", Context.MODE_PRIVATE)
        val editor=sharedPref.edit()

        val isUser=sharedPref.getBoolean("ALREADY_USER", false)

        if(!isUser){
            editor.putBoolean("ALREADY_USER",true)
            //other
            editor.putString("name", "Yami 8")
            editor.putString("id","8")
            val user=User("Yami 8","8")
            //me
//            editor.putString("name", "Sigma")
//            editor.putString("id","Test@1")
//            val user=User("Sigma","Test@1")
            viewModel.putData(user)
        }
        editor.apply()

        CoroutineScope(Dispatchers.IO).launch{
            delay(500)
            startActivity(Intent(this@MainActivity, ChatActivity::class.java))
            finish()
        }
    }
}