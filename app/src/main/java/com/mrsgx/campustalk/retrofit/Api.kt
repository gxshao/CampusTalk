package com.mrsgx.campustalk.retrofit

import com.mrsgx.campustalk.data.GlobalVar

/**
 * Created by Shao on 2017/9/4.
 */
class   Api{
    companion object {
        const val KEY="key"
        const val API_BASE:String="http://"+ GlobalVar.IP+"13614/subsite/CampusTalk/"
        const val API_VALIDATE= API_BASE+"/events/ctValidate.ashx"
        const val API_EMAIL="email"
        const val API_CTUSER="user"
        const val API_PWD="pass"
        const val API_CODE="code"
    }
}