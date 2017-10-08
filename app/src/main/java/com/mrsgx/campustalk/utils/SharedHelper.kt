package com.mrsgx.campustalk.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Shao on 2017/9/18.
 */
class SharedHelper {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPreferences?=null

        fun getInstance(context: Context): SharedPreferences {
            synchronized(this) {
                if(instance==null)
                instance = context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE)
            }
            return instance!!
        }


        val SHARED_NAME = "config"
        val KEY_EMAIL = "email"
        val KEY_PWD = "pass"
        val FIRST_LOAD="0"
    }
}