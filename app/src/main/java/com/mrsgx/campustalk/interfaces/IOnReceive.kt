package com.mrsgx.campustalk.interfaces

import com.mrsgx.campustalk.obj.CTPushMessage

/**
 * Created by mrsgx on 2018/3/10.
 */
interface IOnReceive {
    fun onReceivePushMessage(msg:CTPushMessage)
}