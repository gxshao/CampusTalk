package com.mrsgx.campustalk.utils

/**
 * native层的工具
 * Created by Shao on 2017/10/10.
 */
class NativeUtils {
    init {
        System.loadLibrary("ct")
    }
    external fun encode(bytes:ByteArray):String
    external fun decodeBuffer(base:String):ByteArray
    external fun stringFromJNI():String
}