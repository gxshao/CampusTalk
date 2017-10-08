package com.mrsgx.campustalk.mvp.Main

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView

/**
 * Created by Shao on 2017/9/4.
 */
class MainContract{
    interface View:BaseView<Presenter>
    {
        fun showMessage(msg:String,level: Int,time:Int)
        fun uploadImg(path:String,uid:String)
    }
    interface Presenter:BasePresenter{
        fun initData()
        fun uploadHeadpic(path:String,uid:String)
    }
}