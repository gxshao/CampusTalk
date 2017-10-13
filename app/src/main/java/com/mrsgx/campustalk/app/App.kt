package com.mrsgx.campustalk.app

import android.support.multidex.MultiDexApplication
import com.baidu.mapapi.SDKInitializer
import com.mrsgx.campustalk.data.Local.DB

/**
 * Created by Shao on 2017/9/29.
 */
class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(applicationContext)
        //GPS位置信息上传
    }
    override fun onTerminate() {
        DB.getInstance(this).Close()
        super.onTerminate()
    }
}