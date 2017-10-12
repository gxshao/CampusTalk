package com.mrsgx.campustalk.obj

/**
 * Created by Shao on 2017/9/8.
 */
 class CTUser{
    var Sex: String = ""  //性别
    var Uid: String = ""  //UUID+0 || +1
    var School: CTSchool? = CTSchool()//学校
    var Age:String?="0"
    var Email:String=""
    var Nickname:String?=null
    var Headpic:String?=null
    var Userexplain:String?=null
    var State:String?=null
    var Password:String?=null
    var Stucard:String?=null
}

