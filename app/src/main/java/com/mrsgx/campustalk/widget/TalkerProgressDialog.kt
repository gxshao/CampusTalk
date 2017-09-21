package com.mrsgx.campustalk.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import android.widget.TextView
import com.mrsgx.campustalk.R
import kotlinx.android.synthetic.main.talker_progress_layout.*

/**
 * Created by Shao on 2017/9/15.
 */
class TalkerProgressDialog(context: Context?, theme: Int) : Dialog(context, theme) {
     var mText: TextView
     var mImg: ImageView

    init {
        setContentView(R.layout.talker_progress_layout)
        mText = this.prg_txt
        mImg = this.prg_img
        // this.setCancelable(false)
        mImg.postDelayed({
            val an: AnimationDrawable = mImg.drawable as AnimationDrawable
            an.start()
        }, 10)
    }
    fun show(txt: String) {
        mText.text = txt
        if (!isShowing)
            show()
    }

    override fun hide() {
        if (isShowing) {
            val an: AnimationDrawable = this.mImg.drawable as AnimationDrawable
            an.stop()
            hide()
        }
    }

}