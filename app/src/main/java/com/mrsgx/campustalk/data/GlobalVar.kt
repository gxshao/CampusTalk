package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/9/14.
 */
class GlobalVar {
    companion object {
        const val IP="192.168.2.131"
        val SERVER_URL= "http://$IP:13614/MyConnection"
        lateinit var LOCAL_USER:CTUser
        val USER_STATE_GOOD="0" //正常
        val USER_STATE_UNATH="1"//未认证
        val USER_STATE_STOPPED="2"//已停用
        val SIGNAL_STATE="signalstate" //获取推送服务器状态
    }
}