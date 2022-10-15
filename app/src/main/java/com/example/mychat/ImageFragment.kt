package com.example.mychat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.example.mychat.databinding.FragmentChatBinding
import com.example.mychat.databinding.FragmentImageBinding


class ImageFragment : Fragment() {

    private lateinit var binding: FragmentImageBinding
    lateinit var messageImage:Message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        arguments?.let {
            messageImage= it.getParcelable("MESSAGE")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentImageBinding.inflate(inflater, container, false)
//        return inflater.inflate(R.layout.fragment_image, container, false)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.run {
                        supportFragmentManager.beginTransaction().remove(this@ImageFragment)
                            .commitAllowingStateLoss()
                    }
                }
            }
            )

        Glide.with(binding.root.context)
            .load(messageImage.msg)
            .placeholder(R.drawable.ic_baseline_image)
            .fitCenter()
            .into(binding.msgImage)
        return binding.root


    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}