package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.retrofit.Api
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Query

/**
 * Created by Shao on 2017/9/14.
 */
interface DataSource {
    fun Login(email:String?,pwd:String?):Observable<ResponseResult<CTUser>>?
    fun CheckEmail(email: String?):Observable<ResponseResult<Boolean>>
    fun SendCode(email:String):Observable<ResponseResult<Boolean>>
    fun RegAccount(user:String,code:String):Observable<ResponseResult<CTUser>>
    fun GetSchoolData():Observable<ResponseResult<String>>
    fun UpdateUserProfile(user:String):Observable<ResponseResult<Boolean>>
    fun UploadFile(key:String,file: MultipartBody.Part, uid: String): Observable<ResponseResult<String>>
    fun GetUserInfoById(uid: String?):Observable<ResponseResult<CTUser>>
    fun StartMatch(uid: String?,schoolcode: String?):Observable<ResponseResult<Boolean>>
    fun StopMatch(uid: String?,schoolcode: String?):Observable<ResponseResult<Boolean>>


}