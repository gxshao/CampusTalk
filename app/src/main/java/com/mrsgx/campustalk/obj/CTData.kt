package com.mrsgx.campustalk.obj

/**
 * Created by Shao on 2017/9/8.
 */
class CTData<T>{
    var DataType:String?=""
    var Body:T?=null
    companion object {
        const val DATATYPE_CONNECTED = "0"//链接信息
        const val DATATYPE_MESSAGE = "1" //普通消息
        const val DATATYPE_REPLY = "2"//服务器响应消息
        const val DATATYPE_PUSH = "3"//服务器响应消息
    }
}