package com.mrsgx.campustalk.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.github.snowdream.android.widget.SmartImageView
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.retrofit.Api

/**
 * Created by Shao on 2017/9/26.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatHolder>() {
    private val mListChat=ArrayList<Any>()
    private val mListRoles = ArrayList<Int>()
    private val mListChatType = ArrayList<Int>()

    companion object {
        const val MSG_TYPE_TEXT = 0
        const val MSG_TYPE_IMGAE = 1
        const val MSG_TYPE_AUDIO = 2
        const val MSG_TYPE_EMOJI = 3
    }

    val TYPE_MY = 1
    val TYPE_OTHER = 0
    var Partner: CTUser = CTUser()
    lateinit var OnAudioPlayerListener:View.OnClickListener
    lateinit var onImageCheckListener:View.OnClickListener
    lateinit var OnHeadPicClickListener:View.OnClickListener
    override fun onBindViewHolder(holder: ChatHolder?, position: Int) {
        if (holder != null) {
            if (mListRoles[position] == TYPE_MY) {
                //设置我的头像
                if (!GlobalVar.LOCAL_USER!!.Headpic.isNullOrEmpty())
                    holder.headpic!!.setImageUrl(Api.API_HEADPIC_BASE + GlobalVar.LOCAL_USER!!.Headpic, Rect())
            } else {
                if (!Partner.Headpic.isNullOrEmpty()) {
                    holder.headpic!!.setImageUrl(Api.API_HEADPIC_BASE + Partner.Headpic, Rect())
                }
            }
            holder.headpic!!.setOnClickListener(OnHeadPicClickListener)
            when (mListChatType[position]) {
                ChatAdapter.MSG_TYPE_TEXT -> {
                    holder.txt_layer!!.visibility=View.VISIBLE
                    holder.img_layer!!.visibility=View.INVISIBLE
                    holder.text!!.text = mListChat[position].toString()
                    holder.text!!.setOnClickListener(null)
                    val lp=holder.image!!.layoutParams
                    lp.width = 0
                    lp.height =0
                    holder.image!!.layoutParams=lp
                    holder.image!!.setImagePath("", Rect())
                    holder.text!!.setTextColor(holder.context!!.resources.getColor(android.R.color.black))
                }
                ChatAdapter.MSG_TYPE_AUDIO -> {
                    val lp=holder.image!!.layoutParams
                    holder.txt_layer!!.visibility=View.VISIBLE
                    holder.img_layer!!.visibility=View.INVISIBLE
                    lp.width = 0
                    lp.height =0
                    holder.image!!.layoutParams=lp
                    holder.image!!.setImagePath("",Rect())
                    holder.text!!.setTextColor(holder.context!!.resources.getColor(R.color.unpressed_blue))
                    holder.text!!.text=String.format("[语音消息] %ds",(com.mrsgx.campustalk.utils.Utils.getfileDuration(mListChat[position].toString())/1000))
                    holder.text!!.tag=mListChat[position]  //语音文件

                    holder.text!!.setOnClickListener(OnAudioPlayerListener)
                }
                ChatAdapter.MSG_TYPE_IMGAE->{
                    holder.txt_layer!!.visibility=View.INVISIBLE
                    holder.img_layer!!.visibility=View.VISIBLE
                    holder.text!!.text=""
                    val lp = holder.image!!.layoutParams
                    val filepath=mListChat[position].toString()
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    lp.height =ViewGroup.LayoutParams.WRAP_CONTENT
                    holder.image!!.layoutParams=lp
                    ///加载图片
                    holder.image!!.setImageBitmap(BitmapFactory.decodeFile(filepath))
                    holder.image!!.tag=mListChat[position]
                    holder.image!!.setOnClickListener(onImageCheckListener)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun addMyChat(xx: String) {
        mListRoles.add(TYPE_MY)
        mListChatType.add(MSG_TYPE_TEXT)
        mListChat.add(xx)
        this.notifyItemInserted(mListChat.size)
    }

    fun addOtherChat(xx: String) {
        mListRoles.add(TYPE_OTHER)
        mListChatType.add(MSG_TYPE_TEXT)
        mListChat.add(xx)
        this.notifyItemInserted(mListChat.size)
    }

    fun addMyMsg(str: String, type: Int) {
        mListRoles.add(TYPE_MY)
        synchronized(this) {
            when (type) {
                ChatAdapter.MSG_TYPE_IMGAE -> {
                    mListChat.add(str)
                }
                ChatAdapter.MSG_TYPE_EMOJI -> {

                }
                ChatAdapter.MSG_TYPE_AUDIO -> {
                    mListChat.add(str)
                }
            }
            mListChatType.add(type)
        }
        this.notifyItemInserted(mListChatType.size)
    }
        //对方消息
    fun addOtherMsg(str: String, type: Int) {
        mListRoles.add(TYPE_OTHER)
        when (type) {
            ChatAdapter.MSG_TYPE_IMGAE -> {
                mListChat.add(str)
            }
            ChatAdapter.MSG_TYPE_EMOJI -> {

            }
            ChatAdapter.MSG_TYPE_AUDIO -> {
                mListChat.add(str)
            }
        }
        mListChatType.add(type)
        this.notifyItemInserted(mListChatType.size)
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

    //重新会话
    fun clearAll() {
        mListChat.clear()
        mListRoles.clear()
        mListChatType.clear()
        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return mListRoles[position]
    }

    class ChatHolder(view: View, role: Int) : RecyclerView.ViewHolder(view) {
        var text: TextView? = null
        var headpic: SmartImageView? = null
        var image:SmartImageView?=null
        var txt_layer:FrameLayout?=null
        var img_layer:FrameLayout?=null
        var context: Context?=null
        init {
            context=view.context
            image= SmartImageView(view.context)
            image!!.maxHeight=context!!.resources.getDimension(R.dimen.chat_image_maxHeight).toInt()
            image!!.maxWidth=context!!.resources.getDimension(R.dimen.chat_image_maxWidth).toInt()
            image!!.adjustViewBounds=true
            image!!.scaleType=ImageView.ScaleType.CENTER_INSIDE
            if (role == 1) {
                text = view.findViewById(R.id.txt_my_chat)
                headpic = view.findViewById(R.id.txt_my_headpic)
                headpic!!.tag=1
                txt_layer=view.findViewById(R.id.txt_my_layer)
                img_layer=view.findViewById(R.id.img_my_layer)
                img_layer!!.addView(image)
            } else {
                text = view.findViewById(R.id.txt_other_chat)
                headpic = view.findViewById(R.id.txt_other_headpic)
                headpic!!.tag=0
                txt_layer=view.findViewById(R.id.txt_other_layer)
                img_layer=view.findViewById(R.id.img_other_layer)
                img_layer!!.addView(image)
            }
            text!!.isClickable = true
        }
    }
}