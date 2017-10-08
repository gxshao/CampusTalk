package com.mrsgx.campustalk.widget

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import com.mrsgx.campustalk.R
import kotlinx.android.synthetic.main.talker_progress_layout.*

/**
 * Created by Shao on 2017/9/15.
 */
class TalkerProgressDialog(private  var content: Context?, theme: Int) : Dialog(content, theme) {
     var mText: TextView
     var mImg: ImageView

    init {
        setContentView(R.layout.talker_progress_layout)
        mText = this.prg_txt
        mImg = this.prg_img
         this.setCancelable(false)
        mImg.postDelayed({
            val an: AnimationDrawable = mImg.drawable as AnimationDrawable
            an.start()
        }, 10)
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun show(txt: String) {
        mText.text = txt
        mImg.postDelayed({
            val an: AnimationDrawable = mImg.drawable as AnimationDrawable
            an.start()
        }, 10)
        if (!isShowing&&!(content as Activity).isDestroyed)
            this.show()
    }

     fun hideDialog() {
        if (isShowing) {
            val an: AnimationDrawable = this.mImg.drawable as AnimationDrawable
            an.stop()
            this.cancel()
        }
    }

}