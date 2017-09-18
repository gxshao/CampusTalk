package com.mrsgx.campustalk.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.widget.TalkerProgressDialog

/**
 * Created by Shao on 2017/9/18.
 */
class SharedHelper {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance:SharedPreferences? = null

        fun getInstance(context: Context): SharedPreferences {
            if (instance == null) {
                synchronized(TalkerProgressDialog::class) {
                    if (instance == null) {
                        instance = context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE)
                    }
                }
            }
            return instance!!
        }
        val SHARED_NAME="config"
        val KEY_EMAIL="email"
        val KEY_PWD="pass"
}}