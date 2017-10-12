package com.mrsgx.campustalk.mvp.Chat

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView
import com.mrsgx.campustalk.obj.CTMessage
import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/9/25.
 */
class ChatContract {
    interface View : BaseView<Prensenter> {
        fun showMessage(msg: String, level: Int, time: Int)
        fun setPartner(p: CTUser)
        fun getPartner(): CTUser
        fun getChatFolder():String
        fun reset()
        fun onReceiveMsg(msg: String)
        fun onReceiveAudio(path: String)
        fun onReceiveImage(path: String)
        fun setCurrentState(state: Int)
        fun getCurrentState(): Int
        fun setFollowState(state:Boolean)
    }

    interface Prensenter : BasePresenter {
        fun startMatch() //开始匹配
        fun stopMatch()  //结束匹配
        fun getPartnerInfo(uid: String) //获取对方资料
        fun sendTextMsg(msg: String)  //发送文本信息
        fun sendImageMsg(picBase: String)//发送图片信息
        fun sendAudioMsg(audioBase: String)//发送音频信息
        fun followPartner(uid: String) //关注
        fun unfollowPartner(uid: String) //取消关注
        fun unregsiter()  //注销业务
    }
}