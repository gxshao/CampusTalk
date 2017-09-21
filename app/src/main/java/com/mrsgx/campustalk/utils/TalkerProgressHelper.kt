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

        fun getInstance(context: Context): TalkerProgressDialog {
            synchronized(TalkerProgressDialog::class) {
                if(instance==null)
                instance = TalkerProgressDialog(context,R.style.BeanDialog)
            }
            return instance!!
        }

    }
}