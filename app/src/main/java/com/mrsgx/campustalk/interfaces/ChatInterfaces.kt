package com.mrsgx.campustalk.interfaces

import com.mrsgx.campustalk.obj.CTMessage

/**
 * Created by Shao on 2017/10/7.
 */
interface ChatInterfaces {
     fun onMatched(uid:String)
     fun onMessage(msg:CTMessage)
     fun onNextMatch()
}