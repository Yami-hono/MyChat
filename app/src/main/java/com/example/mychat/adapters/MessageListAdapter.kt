package com.example.mychat.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychat.ImageList
import com.example.mychat.Message
import com.example.mychat.R
import com.example.mychat.databinding.MyImageMessageBinding
import com.example.mychat.databinding.MyMessageBinding
import com.example.mychat.databinding.OtherImageMessageBinding
import com.example.mychat.databinding.OtherMessageBinding
import com.example.mychat.ui.main.Call


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2
private const val VIEW_IMAGE_OTHER_MESSAGE = 3
private const val VIEW_IMAGE_MY_MESSAGE = 4

//class MessageListAdapter(val context: Context) : RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>(){
class MessageListAdapter(val context: Context,val call:Call) : ListAdapter<Message,MessageListAdapter.MessageViewHolder>(DiffCallback()){

    private var data=ArrayList<Message>()
    private var messages: ArrayList<Message> = ArrayList()
    val sharedPref=context?.getSharedPreferences("USER_STATE", Context.MODE_PRIVATE)
    val myName=sharedPref?.getString("name","")
    val myId=sharedPref?.getString("id","")
    var msgList= mutableListOf<Message>()
     var  imageList= ImageList(null)

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if(myId == message.id) {
            if(message.type=="TEXT")
                VIEW_TYPE_MY_MESSAGE
            else
                VIEW_IMAGE_MY_MESSAGE
        } else {
            if(message.type=="IMAGE")
                VIEW_IMAGE_OTHER_MESSAGE
            else
                VIEW_TYPE_OTHER_MESSAGE
        }
    }

    fun setUpdatedList(data:ArrayList<Message>){
//        data.sortByDescending { it.time }
//        data.reverse()
        this.messages= data
        notifyDataSetChanged()
        setImageList()
    }

    fun addMessage(message: Message){
//        messages.clear()
        messages.add(message)
        notifyDataSetChanged()
    }


    fun setImageList(){
        msgList.clear()
        for( i in messages){
            if(i.type=="IMAGE")
            msgList.add(i)
        }
    }




    open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(message:Message,position: Int) {}
    }

    inner class MyMessageViewHolder(var view:View) :
        MessageViewHolder(view) {
        val binding: MyMessageBinding = MyMessageBinding.bind(itemView)
        override fun bind(message: Message,position: Int) {
            with(binding) {
                txtMyMessage.text=message.msg
                txtMyMessageTime.text= message.time.toString()
                }
            }
        }

    inner class MyImageViewHolder(var view:View) :
        MessageViewHolder(view) {
        val binding: MyImageMessageBinding= MyImageMessageBinding.bind(itemView)
        override fun bind(message: Message,position: Int) {
            with(binding) {
                Glide.with(root.context)
                    .load(message.msg )
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_image)
                    .into(imageViewMessageImage)
            }
            binding.messageRoot.setOnClickListener {

                imageList.msgList=msgList
                imageList.let { it1 -> call.messageClick(it1,position) }
            }
        }
    }

    inner class OtherImageViewHolder(var view:View) :
        MessageViewHolder(view) {
        val binding: OtherImageMessageBinding = OtherImageMessageBinding.bind(itemView)
        override fun bind(message: Message,position: Int) {
            with(binding) {
                Glide.with(root.context)
                    .load(message.msg)
                    .placeholder(R.drawable.ic_baseline_image)
                    .centerCrop()
                    .into(imageViewMessageImage)
            }
            binding.messageRoot.setOnClickListener {
                imageList?.msgList=msgList
                imageList?.let { it1 -> call.messageClick(it1, pos = position) }
            }
        }
    }

    inner class OtherMessageViewHolder(var view: View) :
        MessageViewHolder(view) {
        val binding: OtherMessageBinding = OtherMessageBinding.bind(itemView)
        override fun bind(message: Message,position: Int) {
            with(binding) {
                txtOtherMessage.text=message.msg
                txtOtherUser.text=message.name
                txtOtherMessageTime.text= message.time.toString()

            }
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        return if(viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.my_message, parent, false)
            )
        } else if(viewType == VIEW_TYPE_OTHER_MESSAGE) {
            OtherMessageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.other_message, parent, false)
            )
        }
        else if(viewType == VIEW_IMAGE_MY_MESSAGE) {
            MyImageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.my_image_message, parent, false)
            )
        }
        else {
            OtherImageViewHolder(
                LayoutInflater.from(context).inflate(R.layout.other_image_message, parent, false)
            )
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Message>() {

        //2
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {

            return oldItem == newItem
        }

        //3
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.time == newItem.time
        }
    }
}

//1




