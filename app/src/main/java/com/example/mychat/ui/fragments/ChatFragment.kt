package com.example.mychat.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.*
import com.example.mychat.adapters.MessageListAdapter
import com.example.mychat.databinding.FragmentChatBinding
import com.example.mychat.models.ChatViewModel
import com.example.mychat.ui.fragments.ImageFragment
import com.google.android.material.transition.MaterialSharedAxis
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


object DateUtils {
    fun fromMillisToTimeString(millis: Long) : String {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(millis)
    }
}

private const val RC_SELECT_IMAGE = 20
class ChatFragment : Fragment(),Call {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var msgListAdapter: MessageListAdapter
    private var receiver: User?=null
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding
    private lateinit var itemTouchHelper: ItemTouchHelper
    var isReply=MutableLiveData(false)
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        addObserver()
//        val editor=sharedPref?.edit()
        msgListAdapter= MessageListAdapter(requireActivity(), this)
        if(viewModel.me.id< receiver?.id.toString()){
            viewModel.chatId=" ${viewModel.me.id} ${receiver?.id}"
        }
        else{
            viewModel.chatId=" ${receiver?.id} ${viewModel.me.id}"
        }
//        viewModel.chatId=" ${viewModel.me.id} ${receiver?.id}"            //others
//        viewModel.chatId=" ${receiver?.id} ${viewModel.me.id}"              //mine
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.receiverName.text=receiver?.name
        viewModel.getMessageList()
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)
        binding.sendBtn.setOnClickListener {
            val text= binding.msgTxt.text.toString()
            val message=Message(text,viewModel.me.name,viewModel.me.id,"TEXT",System.currentTimeMillis().toString(),"FALSE")

            if(text.isNotEmpty()) viewModel.putMessage(message )
            binding.msgTxt.setText("")

        }


        binding.sendCam.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
//                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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

        binding.replyClose.setOnClickListener { binding.replyView.visibility=View.GONE }


        val simpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.RIGHT,
                 ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }


            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                // If you want to add a background, a text, an icon
                //  as the user swipes, this is where to start decorating
                //  I will link you to a library I created for that below
                val isDraggingRight = dX > 300
                val isDraggingIntoUndraggableArea =
                    (isDraggingRight)
                val newDx = if (isDraggingIntoUndraggableArea) {
                    300f  // Clamp
                } else {
                    dX
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    newDx,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                if(dX>400){
                    Log.i("messageDrag", "onChildDraw: ")
                    isReply.value=true
                    binding.replyView.visibility=View.VISIBLE
                }

                //actionState should be 2 to enable the drag function
                //dX should be 300

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.RIGHT -> {

                        // Do something when a user swept right
                    }
                }
            }
        }
//        itemTouchHelper.startDrag()
        itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMessageList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("multimedia", "onActivityResult: ${data?.clipData?.itemCount}")
        if(data?.clipData?.itemCount==null) {
            if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null
            ) {
                val count = data.clipData?.itemCount
                val selectedImagePath = data.data

                val selectedImageBmp =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)

                val outputStream = ByteArrayOutputStream()

                selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                val selectedImageBytes = outputStream.toByteArray()

                StorageUtil.uploadMessageImage(selectedImageBytes, viewModel.chatId) { imagePath ->
                    val message = Message(
                        imagePath,
                        viewModel.me.name,
                        viewModel.me.id,
                        "IMAGE",
                        System.currentTimeMillis().toString(),
                        "FALSE"
                    )
                    viewModel.putMessage(message)
                }
            }
        }else{
            var count = data.clipData?.itemCount

            Log.i("multimedia", "onActivityResult: ${data.data}")
            Log.i("multimedia", "onActivityResult: ${data.clipData}")

                for (i in 0 until count!!) {
                    val selectedImagePath = data.clipData?.getItemAt(i)?.uri
                    val selectedImageBmp =
                        MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)

                    val outputStream = ByteArrayOutputStream()

                    selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    val selectedImageBytes = outputStream.toByteArray()

                    StorageUtil.uploadMessageImage(selectedImageBytes, viewModel.chatId) { imagePath ->
                        val message = Message(
                            imagePath,
                            viewModel.me.name,
                            viewModel.me.id,
                            "IMAGE",
                            System.currentTimeMillis().toString(),
                            "FALSE"
                        )
                        viewModel.putMessage(message)
                }
            }
        }
    }


    fun addObserver(){
        viewModel.messageList.observe(viewLifecycleOwner){ it ->
            if(it.isNotEmpty()) {
//                for( i in it)
//                    msgListAdapter.addMessage(i)
                msgListAdapter.setUpdatedList(it as ArrayList<Message>)
                binding.recyclerView.scrollToPosition(it.size-1)
            }
        }
        isReply.observe(viewLifecycleOwner){
            if(it) {
                Log.i("replied", "addObserver: ${viewModel.replyMsg}")
                isReply.value=false
                binding.replyTxt.text=viewModel.replyMsg.msg
            }
        }
    }

    override fun itemClick(user: User) {
        TODO("Not yet implemented")
    }

    override fun messageClick(msg: ImageList,pos: Int) {
        val fragment = ImageFragment()
        val bundle = Bundle()
        val fragmentTransaction: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        fragment?.apply {
            exitTransition = MaterialSharedAxis(
                MaterialSharedAxis.Z,
                /* forward= */ false
            ).apply {
                duration = 500
            }
        }
        Log.i("imageClick", "messageClick: ")
        bundle.putParcelable("IMAGE_LIST", msg)
        bundle.putInt("POSITION",pos)
        fragment.arguments = bundle
        fragmentTransaction?.replace(R.id.chat, fragment)
        fragmentTransaction?.commit()

    }

    override fun msgData(message: Message) {
        viewModel.replyMsg=message
    }

    override fun readReceipt(message: Message) {
        viewModel.sendReadReceipt(message)
    }

}