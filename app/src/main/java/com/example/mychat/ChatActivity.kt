package com.example.mychat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.mychat.databinding.ActivityChatBinding
import com.example.mychat.ui.main.Call
import com.example.mychat.ui.main.ChatFragment
import com.example.mychat.ui.main.MainViewModel
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity(), Call {
    private val viewModel by lazy{
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var userListAdapter: UserListAdapter
    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chat)
        viewModel.database= Room.databaseBuilder(applicationContext,
            PostDatabase::class.java,
            "PostTable")
            .fallbackToDestructiveMigration()
            .build()
        viewModel.getUserList()
        val sharedPref=this.getSharedPreferences("USER_STATE", Context.MODE_PRIVATE)
        val name=sharedPref?.getString("name","")
        val id=sharedPref?.getString("id","")
        viewModel.me= name?.let {
            id?.let { User(name,id) }
        }!!

        Log.i("myUser", "onCreate: ${viewModel.me}")

//        for(i in 0..10){
//            val user=User("Yami $i","$i")
//            viewModel.putData(user)
//        }
//        viewModel.deleteAll()
        userListAdapter= UserListAdapter(this)
        binding.recyclerView.adapter=userListAdapter
        addObserver()
//        viewModel.deleteAll()


    }

    private fun addObserver(){
        viewModel.database.contactDAO().getdata().observe(this){
            viewModel.userList= it as MutableList<User>
            it.sortBy { it.id }
            userListAdapter.setUpdatedList(it as ArrayList<User> )
//            if(it.isEmpty()) {
//                for (i in 2..10) {
//                    viewModel.putData(User("Yami $i", "$i"))
//                }
//            }

        }
    }

    override fun itemClick(user: User) {
        val fragment = ChatFragment()
        val bundle = Bundle()
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragment?.apply {
            exitTransition = MaterialSharedAxis(
                MaterialSharedAxis.Z,
                /* forward= */ false
            ).apply {
                duration = 500
            }
        }
        bundle.putParcelable("SEND_TO", user)
        fragment.arguments = bundle
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }
}