package com.mrsgx.campustalk.mvp.Welcome

import android.os.Handler
import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView

/**
 * Created by Shao on 2017/9/4.
 */
interface WelcomeContract {
    interface View : BaseView<Presenter>{
        fun loadingPost()
        fun showMessage(msg:String,level: Int,time:Int)
    }
    interface Presenter: BasePresenter {
        fun init()
        fun loadingResources()
        fun Login(email:String?,pwd:String?)
    }
}