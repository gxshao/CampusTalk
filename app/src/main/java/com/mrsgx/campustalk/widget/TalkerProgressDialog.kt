package com.mrsgx.campustalk.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.mrsgx.campustalk.R


/**
 * Created by Shao on 2017/9/15.
 */
class TalkerProgressDialog(context: Context?, theme:Int) : Dialog(context,theme) {
    var mText: TextView? = null
    var mImg :ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.bean_progress_layout)
        super.onCreate(savedInstanceState)
        mText = this.findViewById(R.id.prg_txt)
        mImg=this.findViewById(R.id.prg_img)
       // this.setCancelable(false)
        this.setOnShowListener({
            mImg!!.postDelayed({
                val an:AnimationDrawable= this.mImg!!.drawable as AnimationDrawable
                an.start()
            },10)
        })
        this.setOnDismissListener {
            mImg!!.post {
                val an:AnimationDrawable= this.mImg!!.drawable as AnimationDrawable
                an.stop()
            }
        }

    }

    fun show(txt:String) {
        if(mText!=null)
        {
            mText!!.text=txt
        }
        if(!isShowing)
            show()
    }

    override fun hide(){
        if(isShowing){
            val an:AnimationDrawable= this.mImg!!.drawable as AnimationDrawable
            an.stop()
            hide()
        }
    }

}