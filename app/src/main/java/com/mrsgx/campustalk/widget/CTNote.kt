package com.mrsgx.campustalk.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.mrsgx.campustalk.R

/**
 * Created by Shao on 2017/9/24.
 */
class CTNote(private val context: Context) : PopupWindow(context) {
    companion object {
        //背景颜色分级
        val LEVEL_TIPS = 0
        val LEVEL_WARNING = 1
        val LEVEL_ERROR = 2
        val LEVEL_NOTIFY = 3
        //时间
        val TIME_SHORT = 4
        val TIME_LENGTH = 8

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CTNote? = null
        @SuppressLint("StaticFieldLeak")
        private var rootview: View?=null

        fun getInstance(context: Context, r: View): CTNote {
            if (INSTANCE == null) {
                INSTANCE = CTNote(context)
            }
            rootview = r
            return INSTANCE!!
        }
    }

    private var view: View? = null
    private var mTitle: TextView? = null
    private var mContent: TextView? = null
    private var mBtnClose: ImageView? = null
    private val MILLISECOND: Int = 1000 //时间系数

    init {
        view = LayoutInflater.from(context).inflate(R.layout.ctnote, null)
        this.contentView = view
        this.background.alpha = 0
        this.isTouchable = true
        this.isOutsideTouchable = false
        this.animationStyle = R.style.anim_ctnote
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        if (view != null) {
            mTitle = view!!.findViewById(R.id.txt_title_cnote)
            mContent = view!!.findViewById(R.id.txt_note_cnote)
            mBtnClose = view!!.findViewById(R.id.btn_close_cnote)
            mBtnClose!!.setOnClickListener {
                this.dismiss()
            }
        }

    }


    fun show(title: String, content: String, levle: Int, len: Int) {
        mTitle!!.text = title
        mContent!!.text = content
        //判断级别并设置背景和时间
        if(this.isShowing)
        {
            this.update()
        }else {
            this.showAtLocation(rootview, android.view.Gravity.TOP, 0, 0)
        }
        }
    fun hide(){
        this.dismiss()
        INSTANCE=null
    }

}