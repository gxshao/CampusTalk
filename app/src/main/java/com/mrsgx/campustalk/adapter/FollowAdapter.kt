package com.mrsgx.campustalk.adapter

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.snowdream.android.widget.SmartImageView
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.interfaces.RecyclerViewClickListener
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.retrofit.Api
import kotlinx.android.synthetic.main.item_follow_list.view.*
import java.util.*

/**
 * Created by Shao on 2017/9/28.
 */
class FollowAdapter(private val mListFollows: ArrayList<CTUser>) : RecyclerView.Adapter<FollowAdapter.ViewHodler>() {
    private var mContext: Context? = null
    lateinit var mUnfollowListener: View.OnClickListener
        set

    var mRecyclerViewListener:RecyclerViewClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHodler {
        mContext = parent!!.context

        return ViewHodler(LayoutInflater.from(parent.context).inflate(R.layout.item_follow_list, parent, false),mRecyclerViewListener)
    }
    fun addToFollowList(usr: CTUser) {
        synchronized(this) {
            mListFollows.add(usr)
            this.notifyItemInserted(mListFollows.size - 1)
        }
    }
    fun getChildItemByPos(pos:Int):CTUser?{
        if(pos>mListFollows.size||pos<0)
            return null
        return mListFollows[pos]
    }

    fun deleteFromFollowList(pos: Int) {
        synchronized(this) {
            if ( pos>=0) {
                mListFollows.removeAt(pos)
                this.notifyItemRemoved(pos)
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHodler?, position: Int) {
        if (holder != null) {
            holder.txt_nickname!!.text = mListFollows[position].Nickname
            holder.txt_explan!!.text = mListFollows[position].Userexplain
            holder.img_headpic!!.setImageUrl(Api.API_HEADPIC_BASE+mListFollows[position].Headpic, Rect())
            holder.btn_unfollow!!.setTag(R.id.btn_unfollow, position)
            holder.btn_unfollow!!.setOnClickListener(mUnfollowListener)
        }
    }

    override fun getItemCount(): Int {
        return mListFollows.size
    }


    class ViewHodler(view: View,listener: RecyclerViewClickListener?) : RecyclerView.ViewHolder(view) {

        var txt_nickname: TextView? = null
        var txt_explan: TextView? = null
        var img_headpic: SmartImageView? = null
        var btn_unfollow: Button? = null

        init {
            txt_nickname = view.findViewById(R.id.txt_item_nickname)
            txt_explan = view.findViewById(R.id.txt_item_explan)
            img_headpic = view.findViewById(R.id.img_headpic)
            btn_unfollow = view.findViewById(R.id.btn_unfollow)
            btn_unfollow!!.tag=view
            view.isClickable=true
            if(listener!=null){
                view.setOnClickListener {
                    listener.onItemClick(view,this.position)
                }
            }
        }
    }

}

