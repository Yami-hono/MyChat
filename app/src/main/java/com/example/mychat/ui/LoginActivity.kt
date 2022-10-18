package com.example.mychat.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.mychat.R
import com.example.mychat.User
import com.example.mychat.databinding.ActivityLoginBinding
import com.example.mychat.ui.main.MainViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy{
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)
        val sharedPref=this.getSharedPreferences("USER_STATE", Context.MODE_PRIVATE)
        val editor=sharedPref.edit()
        viewModel.getUserList()
        binding.apply {


            submitBtn.setOnClickListener {
                val mail=emailId.text.toString()
                val pass=password.text.toString()
                if(mail=="") showToast("Please Enter an email address")
                else if(pass=="") showToast("Please Enter the password")

                else if(mail==pass){
                    viewModel.me= mail?.let {
                        pass?.let { User(mail,pass) }
                    }!!
                    viewModel.putData(User(mail,pass))
                    editor?.putString("name", mail)
                    editor?.putString("id",mail)
                    editor?.putBoolean("ALREADY_USER", true)
                    editor?.apply()
                    startActivity(Intent(this@LoginActivity, ChatActivity::class.java))
                    finish()

                }
                else
                    showToast("Please Enter Valid data")
            }
        }

        editor?.apply()

    }

    private fun showToast(str:String){
        Toast.makeText(this,str, Toast.LENGTH_LONG).show()
    }
}
