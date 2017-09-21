package com.mrsgx.campustalk.utils

/**
 * Created by Shao on 2017/9/18.
 */
class RegMatchs {

    companion object {
        private val REGEX_EMAIL="/^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$/"
        fun MatchEmail(email:String):Boolean{
            return Regex(REGEX_EMAIL).matches(email)
        }
    }
}