package com.mrsgx.campustalk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.widget.TalkerProgressDialog

/**
 * Created by Shao on 2017/9/15.
 */
class TalkerProgressHelper {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TalkerProgressDialog?=null
        @SuppressLint("StaticFieldLeak")
        private var O_context:Context?=null
        fun getInstance(context: Context): TalkerProgressDialog {
            synchronized(this) {
                if(O_context!=context)
                     instance = TalkerProgressDialog(context, R.style.BeanDialog)
                O_context=context
                return instance!!
            }
        }
    }
}