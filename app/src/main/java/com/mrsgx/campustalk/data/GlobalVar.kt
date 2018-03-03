package com.mrsgx.campustalk.data

import android.Manifest
import android.graphics.Typeface
import com.mrsgx.campustalk.obj.CTUser
import java.lang.reflect.Type

/**
 * Created by Shao on 2017/9/14.
 * 全局变量
 */
class GlobalVar {
    companion object {
        const val IP = "192.168.2.222:13614"
        // const val IP="mrsgx.cn"
        val SERVER_URL = "http://$IP/MyConnection"
        var LOCAL_USER: CTUser? = CTUser()
        val LOCAL_DIRECTORY = "/campustalk/"
        val USER_STATE_GOOD = "0" //正常
        val USER_STATE_WAITING = "2"//等待服务端认证
        val USER_STATE_UNATH = "1"//未认证
        val USER_STATE_STOPPED = "3"//已停用
        val SIGNAL_STATE = "signalstate" //获取推送服务器状态
        val ALLOW_FIND_DAY = 604800000L  //允许选择时间周期
        val SEX_MAN = "0"
        val SEX_FEMALE = "1"
        val SPILTER = "$" //数据分隔符
        val AUTOLOGIN = "AUTOLOGIN" //自动登录关键字
        val RECONNECT_INTERVAL = 5000L //发起重连间隔
        val UPLOAD_SPAN = 180000L //半小时上传一次
        val ACTION_FOLLOW = "1"//关注
        val ACTION_UNFOLLOW = "0" //取消关注

        //REQUESTCODE
        val CHOOSE_PHOTO: Int = 1
        val CHANGE_SETTING: Int = 2
        val SELECT_TIME_RANGE: String = "select_time"
        var typeface: Typeface? = null

        //权限集合
        val permissionList = arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.GET_ACCOUNTS)
    }
}