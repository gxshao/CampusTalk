package com.mrsgx.campustalk.mvp.main

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView
import com.mrsgx.campustalk.obj.CTLocation
import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/9/4.
 */
class MainContract{
    interface View:BaseView<Presenter>
    {
        fun setNavigator(state:Int)
        fun showMessage(msg:String,level: Int,time:Int)
        fun uploadImg(path:String,uid:String)
        fun updateFollowList()
        fun cancelFollow(uid:String)
        fun initFollowData(list:ArrayList<CTUser>)
        fun showUserProfile(user:CTUser)
        fun setCoin(num:Int)
        fun signUp(uid: String)
        fun initUserProperty(uid: String)
        fun setSignBtnSate(b:Boolean)
        fun checkSign(uid: String)
        fun logout()
    }
    interface Presenter:BasePresenter{
        fun initData()
        fun uploadHeadpic(path:String,uid:String)
        fun updateFollowList()
        fun cancelFollow(uid:String)
        fun uploadLocationInfo(list:ArrayList<CTLocation>)
        fun signUp(uid: String)
        fun initUserProperty(uid: String)
        fun checkSign(uid: String)
    }
}