package com.mrsgx.campustalk.obj

/**
 * Created by Shao on 2017/9/20.
 */
class CTMessage {
    companion object {
         const val MESSAGE_TYPE_TEXT = "0"
         const val MESSAGE_TYPE_EMOJI = "1"
         const val MESSAGE_TYPE_AUDIO = "2"
         const val MESSAGE_TYPE_PHOTO = "3"
    }
    var From: String? = null
    var To: String? = null
    var Type: String? = null // 文字 0，表情 1，语音 2， 图片 3
    var Body: String? = null
    var Time: String? = null
}