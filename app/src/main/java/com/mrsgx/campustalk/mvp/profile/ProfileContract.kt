package com.mrsgx.campustalk.mvp.profile

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView
import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/10/3.
 */
class ProfileContract {
    interface View:BaseView<Presenter>{
        fun showMessage(msg:String,level:Int,time:Int)
        fun onStucardUpload(path: String)
    }
    interface Presenter:BasePresenter{
        fun submitProfile(user:CTUser)
        fun uplpadstucard(path:String,uid:String)
    }
}