package com.mrsgx.campustalk.app

import android.graphics.Typeface
import android.support.multidex.MultiDexApplication
import com.baidu.mapapi.SDKInitializer
import com.mrsgx.campustalk.data.GlobalVar.Companion.TYPEFACE_HUAKANG
import com.mrsgx.campustalk.data.Local.DB
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Shao on 2017/9/29.
 */
class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(applicationContext)
        //GPS位置信息上传
        LeakCanary.install(this)
        TYPEFACE_HUAKANG = Typeface.createFromAsset(this.assets, "fonts/myfonts.ttf")
    }
    override fun onTerminate() {
        DB.getInstance(this).Close()
        super.onTerminate()
    }
}