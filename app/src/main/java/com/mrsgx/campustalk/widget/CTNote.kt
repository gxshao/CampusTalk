package com.mrsgx.campustalk.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.mrsgx.campustalk.R
import java.lang.ref.WeakReference
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
        const val TIME_LONG = 20

        private var INSTANCE: WeakReference<CTNote>? = null
        private var rootview: WeakReference<View>? = null

        fun getInstance(context: Context, r: View): CTNote {
            if (INSTANCE == null || INSTANCE!!.get() == null) {
                INSTANCE = WeakReference(CTNote(context))
            }
            rootview = WeakReference(r)
            return INSTANCE?.get()!!
        }
    }

    private var view: View? = null
    private var mTitle: TextView? = null
    private var mContent: TextView? = null
    private var mBtnClose: ImageView? = null
    private val MILLISECOND: Int = 1000 //时间系数
    private var mTimer: Timer = Timer()
    private var isRunning = false
    private var Title_Tips = ""
    private var Title_Warning = ""
    private var Title_Error = ""
    private var Title_Notify = ""
    private lateinit var mHandler: CtnoteHandler

    init {
        try {
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
            mHandler = CtnoteHandler(this)
        } catch (e: Exception) {
            println(e)
        }
        Title_Tips = context.resources.getString(R.string.tips)
        Title_Error = context.resources.getString(R.string.error)
        Title_Notify = context.resources.getString(R.string.notify)
        Title_Warning = context.resources.getString(R.string.warning)
    }

    /***
     * 显示通知
     */
    fun show(content: String, level: Int, len: Int) {
        mTitle!!.text = Title_Tips
        mContent!!.text = content
        //判断级别并设置背景和时间

        var background: Drawable = this.context.resources.getDrawable(R.drawable.ctnote_bg_green)
        when (level) {
            LEVEL_TIPS -> {
            }
            LEVEL_ERROR -> {
                mTitle!!.text = Title_Error
                background = this.context.resources.getDrawable(R.drawable.ctnote_bg_red)
            }
            LEVEL_WARNING -> {
                mTitle!!.text = Title_Warning
                background = this.context.resources.getDrawable(R.drawable.ctnote_bg_orange)
            }
            LEVEL_NOTIFY -> {
                mTitle!!.text = Title_Notify
                background = this.context.resources.getDrawable(R.drawable.ctnote_bg_blue)
            }
        }
        background.alpha = 180
        view!!.setBackgroundDrawable(background)
        if (this.isShowing) {
            this.update()
        } else {
            //从上面弹出
            try {
                if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    !(rootview?.get()!!.context as Activity).isDestroyed && !(rootview?.get()!!.context as Activity).isFinishing
                } else {
                    true
                }) {
                    this.showAtLocation(rootview?.get(), android.view.Gravity.TOP, 0, 0)
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        if (!isRunning) {
            mTimer.purge()
            mTimer = Timer()
            mTimer.schedule(getDismisTask(), (MILLISECOND * len).toLong())
            isRunning = true
        } else {
            mTimer.cancel()
            mTimer.purge()
            mTimer = Timer()
            mTimer.schedule(getDismisTask(), (MILLISECOND * len).toLong())
            isRunning = true
        }
    }

    /**
     * 自动退出线程
     */
    private fun getDismisTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                mHandler.sendMessage(mHandler.obtainMessage())
                isRunning = false
                cancel()
            }
        }
    }

    class CtnoteHandler(note: CTNote) : Handler() {
        private val mHand: WeakReference<CTNote> by lazy {
            WeakReference<CTNote>(note)
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        override fun handleMessage(msg: Message?) {
            val ct = mHand.get()
            if (ct != null) {
                ct.hide()
            }
            super.handleMessage(msg)
        }
    }

    /**
     * 手动退出方法
     */
    fun hide() {
        try {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                !(context as Activity).isDestroyed && !(context).isFinishing
            } else {
                true
            }) {
                this.dismiss()
                rootview = null
            }
        } catch (e: IllegalArgumentException) {
            // Handle or log or ignore
        } catch (e: Exception) {
            // Handle or log or ignore
        } finally {
            INSTANCE = null
        }
    }

}