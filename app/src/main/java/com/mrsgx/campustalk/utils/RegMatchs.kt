package com.mrsgx.campustalk.utils

import java.util.regex.Pattern

/**
 * Created by Shao on 2017/9/18.
 */
class RegMatchs {

    companion object {
        private val REGEX_EMAIL="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        fun MatchEmail(email:String):Boolean{
            val regex = Pattern.compile(REGEX_EMAIL)
            val matcher = regex.matcher(email)
            return matcher.matches()
        }
    }
}