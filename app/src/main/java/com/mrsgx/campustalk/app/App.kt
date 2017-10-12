package com.mrsgx.campustalk.app

import android.app.Application
import android.support.multidex.MultiDexApplication
import com.baidu.mapapi.SDKInitializer
import com.mrsgx.campustalk.data.Local.DB
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * Created by Shao on 2017/9/29.
 */
class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(applicationContext)
        Watcher=LeakCanary.install(this)
        //GPS位置信息上传
    }
    companion object {
        lateinit var Watcher:RefWatcher
    }
    override fun onTerminate() {
        DB.getInstance(this).Close()
        super.onTerminate()
    }
}