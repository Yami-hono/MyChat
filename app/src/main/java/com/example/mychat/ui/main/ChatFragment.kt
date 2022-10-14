package com.example.mychat.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.mychat.Message
import com.example.mychat.MessageListAdapter
import com.example.mychat.StorageUtil
import com.example.mychat.User
import com.example.mychat.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object DateUtils {
    fun fromMillisToTimeString(millis: Long) : String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(millis)
    }
}

private const val RC_SELECT_IMAGE = 20
class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var msgListAdapter: MessageListAdapter
    private var receiver: User?=null
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        val sharedPref=context?.getSharedPreferences("USER_STATE", Context.MODE_PRIVATE)
        val bundle = this.arguments
        receiver= bundle?.getParcelable("SEND_TO")

        val name=sharedPref?.getString("name","")
        val id=sharedPref?.getString("id","")
        viewModel.me= name?.let {
            id?.let { User(name,id) }
        }!!

        Log.i("messageList", "onCreate: ${viewModel.me}")
//        viewModel.chatId="${receiver?.id}+$id"


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        addObserver()
//        val editor=sharedPref?.edit()
        msgListAdapter= MessageListAdapter(requireActivity())
        viewModel.chatId=" ${viewModel.me.id} ${receiver?.id}"            //others
//        viewModel.chatId=" ${receiver?.id} ${viewModel.me.id}"              //mine
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.receiverName.text=receiver?.name
        viewModel.getMessageList()
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)
        binding.sendBtn.setOnClickListener {
            val text= binding.msgTxt.text.toString()
            val message=Message(text,viewModel.me.name,viewModel.me.id,"TEXT",System.currentTimeMillis().toString())

            if(text.isNotEmpty()) viewModel.putMessage(message )
            binding.msgTxt.setText("")

        }

        binding.sendCam.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
        }
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.run {
                        supportFragmentManager.beginTransaction().remove(this@ChatFragment)
                            .commitAllowingStateLoss()
                    }
                }
            }
            )

        binding.recyclerView.adapter=msgListAdapter

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes,viewModel.chatId) { imagePath ->
                val message=Message(imagePath,viewModel.me.name,viewModel.me.id,"IMAGE",System.currentTimeMillis().toString())
                viewModel.putMessage(message )
            }
        }
    }


    fun addObserver(){
        viewModel.messageList.observe(viewLifecycleOwner){ it ->
            if(it.isNotEmpty()) {
                Log.i("messageItems", "addObserver: $it")
//                for( i in it)
//                    msgListAdapter.addMessage(i)
                msgListAdapter.setUpdatedList(it as ArrayList<Message>)
            }

        }
    }

}