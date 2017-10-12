package com.mrsgx.campustalk.retrofit

import com.mrsgx.campustalk.data.GlobalVar

/**
 * Created by Shao on 2017/9/4.
 */
class   Api{
    companion object {
        const val KEY="key"
        const val API_BASE:String="http://"+ GlobalVar.IP+":13614/subsite/CampusTalk/"
        const val API_HEADPIC_BASE= API_BASE+"/images/headpic/"
        const val API_STUCARD_BASE= API_BASE+"/images/stucard/"
        const val API_SERVER_VALIEDATE = API_BASE+"/events/ctValidate.ashx"
        const val API_SERVER_COMMON = API_BASE+"/events/ctCommon.ashx"
        const val API_SERVER_MATCH= API_BASE+"/events/ctMatch.ashx"

        const val API_EMAIL="email"
        const val API_CTUSER="user"
        const val API_PWD="pass"
        const val API_CODE="code" //验证码
        const val API_UID="uid"
        const val API_MATCH="match"
        const val API_QUIT="quit"
        const val API_SCHOOLCODE="schoolcode"

        const val API_TID="tid" //被关注目标ID
        const val API_OP="op" //操作 0 或 1
        const val API_GET_DATA="schoolinfo"
        const val API_UPDATE_PROFILE="updateprofile"
    }
}