package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.obj.CTUser
import io.reactivex.Observable

/**
 * Created by Shao on 2017/9/14.
 */
interface DataSource {
    fun Login(email:String?,pwd:String?):Observable<ResponseResult<CTUser>>?
    fun CheckEmail(email: String?):Observable<ResponseResult<Boolean>>
    fun SendCode(email:String):Observable<ResponseResult<Boolean>>
    fun RegAccount(user:String,code:String):Observable<ResponseResult<CTUser>>
}