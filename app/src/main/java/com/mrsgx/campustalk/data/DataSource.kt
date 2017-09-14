package com.mrsgx.campustalk.data

import io.reactivex.Observable

/**
 * Created by Shao on 2017/9/14.
 */
interface DataSource {
    fun Login(email:String?,pwd:String?):Observable<ResponseResult<String>>?
}