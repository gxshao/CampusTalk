package com.mrsgx.campustalk.app

import android.app.Application
import android.graphics.Typeface
import com.baidu.mapapi.SDKInitializer
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Local.DB

/**
 * Created by Shao on 2017/9/29.
 * Application
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(applicationContext)
        //GPS位置信息上传  LeakCanary.install(this)
        //权限请求
        GlobalVar.typeface = Typeface.createFromAsset(applicationContext!!.assets, "fonts/myfonts.ttf")
    }
    override fun onTerminate() {
        DB.getInstance(this).Close()
        println("CampusTalk is already exit")
        super.onTerminate()
    }

}