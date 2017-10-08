package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/9/14.
 */
class GlobalVar {
    companion object {
        const val IP="192.168.2.131"
        val SERVER_URL= "http://$IP:13614/MyConnection"
        var LOCAL_USER:CTUser?=null
        val USER_STATE_GOOD="0" //正常
        val USER_STATE_WAITING="2"//等待服务端认证
        val USER_STATE_UNATH="1"//未认证
        val USER_STATE_STOPPED="3"//已停用
        val SIGNAL_STATE="signalstate" //获取推送服务器状态
        val ALLOW_FIND_DAY=604800000L
        val SEX_MAN="0"
        val SPILTER="$"
        val SEX_FEMALE="1"
        val AUTOLOGIN="AUTOLOGIN"

    }
}