package com.example.mychat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.mychat.ImageList
import com.example.mychat.Message
import com.example.mychat.R
import com.example.mychat.databinding.ImageItemBinding

class ImageAdapter(context: Context): PagerAdapter() {

    private var messages: ArrayList<Message> = ArrayList()
    lateinit var binding:ImageItemBinding
    var layoutInflater=LayoutInflater.from(context)

    fun setUpdatedList(data:ImageList){
//        data.sortByDescending { it.time }
//        data.reverse()
        this.messages= data.msgList as ArrayList<Message>
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return messages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val message=messages[position]
        val view=layoutInflater.inflate(R.layout.image_item, container, false)
        binding=ImageItemBinding.bind(view)
        Glide.with(binding.root.context)
            .load(message.msg )
            .fitCenter()
            .placeholder(R.drawable.ic_baseline_image)
            .into(binding.image)
        container.addView(view)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}