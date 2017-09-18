package com.mrsgx.campustalk.mvp.Welcome

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView

/**
 * Created by Shao on 2017/9/4.
 */
interface WelcomeContract {
    interface View : BaseView<Presenter>
    interface Presenter: BasePresenter {
        fun init()
        fun Login(email:String?,pwd:String?)
    }
}