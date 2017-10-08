package com.mrsgx.campustalk.mvp.Login

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView

/**
 * Created by Shao on 2017/9/14.
 */
class LoginContract {
    interface View : BaseView<Presenter>{
        fun finishActivity()
        fun showMessage(msg:String,level: Int,time:Int)
        fun isAutoLogin():Boolean
    }
    interface Presenter:BasePresenter{
        fun initData()
        fun Login(email:String?,pwd:String?)
    }
}