package com.mrsgx.campustalk.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.snowdream.android.widget.SmartImageView
import com.mrsgx.campustalk.R

/**
 * Created by Shao on 2017/9/26.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatHolder>() {
    private val mListChat = ArrayList<String>()
    private val mListRoles = ArrayList<Int>()
    val TYPE_MY = 1
    val TYPE_OTHER = 0
    override fun onBindViewHolder(holder: ChatHolder?, position: Int) {
        if (holder != null) {
            holder.text!!.text = mListChat[position]
            if(mListRoles[position]==TYPE_MY)
            {
                //设置我的头像
                holder.headpic!!.setImageUrl("http://www.mrsgx.cn/images/smallbutton/usr.png", Rect())
            }else
            {
                holder.headpic!!.setImageUrl("http://k2.jsqq.net/uploads/allimg/1703/7_170312181624_2.jpg", Rect())
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun addMyChat(xx: String) {
        mListChat.add(xx)
        mListRoles.add(TYPE_MY)
        this.notifyDataSetChanged()
    }

    fun addOtherChat(xx: String) {
        mListChat.add(xx)
        mListRoles.add(TYPE_OTHER)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatHolder {
        return if (viewType == 1)
            ChatHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_chat_my_layout, parent, false), viewType)
        else
            ChatHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_chat_other_layout, parent, false), viewType)
    }

    override fun getItemCount(): Int {
        return mListChat.size
    }

    override fun getItemViewType(position: Int): Int {
        return mListRoles[position]
    }

    class ChatHolder(view: View, role: Int) : RecyclerView.ViewHolder(view) {
        var text: TextView? = null
        var headpic: SmartImageView? = null

        init {
            if (role == 1) {
                text = view.findViewById(R.id.txt_my_chat)
                headpic=view.findViewById(R.id.txt_my_headpic)
            } else {
                text = view.findViewById(R.id.txt_other_chat)
                headpic=view.findViewById(R.id.txt_other_headpic)
            }
        }
    }
}