package com.mrsgx.campustalk.mvp.Chat

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView

/**
 * Created by Shao on 2017/9/25.
 */
class ChatContract {
    interface  View: BaseView<Prensenter> {
        fun showMessage(title:String,msg: String,level:Int)
    }
    interface Prensenter: BasePresenter {

    }
}