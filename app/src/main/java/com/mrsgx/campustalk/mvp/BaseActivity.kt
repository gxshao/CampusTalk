package com.mrsgx.campustalk.mvp

import android.app.Activity
import android.view.KeyEvent
import com.mrsgx.campustalk.R

/**
 * Created by mrsgx on 2018/3/3.
 */
open class BaseActivity : Activity() {

    private var exitTime: Long = 0// 退出时间
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            // 判断间隔时间 大于2秒就退出应用
            return if (System.currentTimeMillis() - exitTime > 2000) {
                exitTime = System.currentTimeMillis()
                false
            } else {
                true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}