package com.mrsgx.campustalk.utils

import java.util.regex.Pattern

/**
 * 正则表达式校验工具
 * Created by Shao on 2017/9/18.
 */
class RegMatchs {

    companion object {
        private val REGEX_EMAIL="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"
        fun matchEmail(email:String):Boolean{
            val regex = Pattern.compile(REGEX_EMAIL)
            val matcher = regex.matcher(email)
            return matcher.matches()
        }
    }
}