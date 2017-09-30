package com.mrsgx.campustalk.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.mrsgx.campustalk.R
import java.util.*

/**
 * Created by Shao on 2017/9/24.
 */
class CTNote(private val context: Context) : PopupWindow(context) {
    companion object {
        //背景颜色分级
        const val LEVEL_TIPS = 0
        const val LEVEL_WARNING = 1
        const val LEVEL_ERROR = 2
        const val LEVEL_NOTIFY = 3
        //时间
        const val TIME_SHORT = 4
        const val TIME_LENGTH = 8

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CTNote? = null
        @SuppressLint("StaticFieldLeak")
        private var rootview: View? = null

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
    private var mTimer: Timer = Timer()
    private var isRunning = false
    private var Title_Tips=""
    private var Title_Warning=""
    private var Title_Error=""
    private var Title_Notify=""

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
        Title_Tips=context.resources.getString(R.string.tips)
        Title_Error=context.resources.getString(R.string.error)
        Title_Notify=context.resources.getString(R.string.notify)
        Title_Warning=context.resources.getString(R.string.warning)
    }

    @SuppressLint("ResourceType")
            /***
     * 显示通知
     */
    fun show(content: String, level: Int, len: Int) {
        mTitle!!.text=Title_Tips
        mContent!!.text = content
        //判断级别并设置背景和时间

        var background: Drawable = this.context.resources.getDrawable(R.drawable.ctnote_bg_green)
        when (level) {
            LEVEL_TIPS -> {
            }
            LEVEL_ERROR -> {
                mTitle!!.text=Title_Error
                background = this.context.resources.getDrawable(R.drawable.ctnote_bg_red)
            }
            LEVEL_WARNING -> {
                mTitle!!.text=Title_Warning
                background = this.context.resources.getDrawable(R.drawable.ctnote_bg_orange)
            }
            LEVEL_NOTIFY -> {
                mTitle!!.text=Title_Notify
                background = this.context.resources.getDrawable(R.drawable.ctnote_bg_blue)
            }
        }
        background.alpha=255
        view!!.setBackgroundDrawable(background)
        if (this.isShowing) {
            this.update()
        } else {
            //从上面弹出
            this.showAtLocation(rootview, android.view.Gravity.TOP, 0, 0)

        }
        if (!isRunning) {
            mTimer.purge()
            mTimer=Timer()
            mTimer.schedule(getDismisTask(), (MILLISECOND * len).toLong())
            isRunning = true
        } else {
            mTimer.cancel()
            mTimer.purge()
            mTimer=Timer()
            mTimer.schedule(getDismisTask(), (MILLISECOND * len).toLong())
            isRunning = true
        }
    }

    /**
     * 自动退出线程
     */
    fun getDismisTask():TimerTask{
         val mTask = object : TimerTask() {
            override fun run() {
                mHandler.sendMessage(mHandler.obtainMessage())
                isRunning = false
                this.cancel()
            }
        }
        return mTask
    }
    val mHandler= @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun dispatchMessage(msg: Message?) {
            if(INSTANCE!=null)
                INSTANCE!!.dismiss()
            super.dispatchMessage(msg)
        }

    }
    /**
     * 手动退出方法
     */
    fun hide() {
        this.dismiss()
        INSTANCE = null
    }

}