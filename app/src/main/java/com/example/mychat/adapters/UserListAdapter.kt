package com.example.mychat.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.R
import com.example.mychat.User
import com.example.mychat.databinding.LiUserItemBinding
import com.example.mychat.ui.main.Call


class UserListAdapter(var call: Call): RecyclerView.Adapter<UserListAdapter.UserViewHolder>(){

    var data=ArrayList<User>()

    fun setUpdatedList(data:ArrayList<User>){
        this.data= data
        notifyDataSetChanged()
    }


    inner class UserViewHolder(var binding: LiUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            with(binding) {
                binding.postTitle.text=item.name
                userCard.setOnClickListener {
                    item.let {
                        call.itemClick(it)
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = DataBindingUtil.inflate<LiUserItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.li_user_item,
            parent,
            false
        )
        return UserViewHolder(view)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val searchResult = data.get(position)
        searchResult.let {
                searchResult->
            holder.bind(searchResult)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

