package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/9/14.
 */
class GlobalVar {
    companion object {
        val IP="192.168.123.1"
        val SERVER_URL="http://"+IP+":13614/MyConnection"
        var LOCAL_USER:CTUser?=null
        val USER_STATE_GOOD="0" //正常
        val USER_STATE_UNATH="1"//未认证
        val USER_STATE_STOPPED="2"//已停用
    }
}