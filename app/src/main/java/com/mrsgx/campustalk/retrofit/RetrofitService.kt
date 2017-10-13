package com.mrsgx.campustalk.retrofit

import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.obj.CTUser
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


/**
 * Created by Shao on 2017/9/4.
 */
interface RetrofitService {
    @POST(Api.API_SERVER_VALIEDATE)
     fun Login(@Query(Api.KEY)key:String?, @Query(Api.API_EMAIL) email: String,@Query(Api.API_PWD) pwd: String): Observable<ResponseResult<CTUser>>

    @GET(Api.API_SERVER_VALIEDATE)
    fun CheckEmail(@Query(Api.KEY)key:String?,@Query(Api.API_EMAIL)email:String):Observable<ResponseResult<Boolean>>

    @GET(Api.API_SERVER_VALIEDATE)
    fun SendCode(@Query(Api.KEY)key:String?,@Query(Api.API_EMAIL)email:String):Observable<ResponseResult<Boolean>>

    @POST(Api.API_SERVER_VALIEDATE)
    fun RegAccount(@Query(Api.KEY)key:String?,@Query(Api.API_CTUSER)user:String,@Query(Api.API_CODE)code:String):Observable<ResponseResult<CTUser>>

    @GET(Api.API_SERVER_COMMON)
    fun GetSchoolData(@Query(Api.KEY)key:String?):Observable<ResponseResult<String>>

    @POST(Api.API_SERVER_COMMON)
    fun UpdateUserProfile(@Query(Api.KEY)key:String?,@Query(Api.API_CTUSER)user:String):Observable<ResponseResult<Boolean>>

    @POST(Api.API_SERVER_COMMON)
    @Multipart
    fun UploadFile(@Query(Api.KEY)key:String?,@Part file: MultipartBody.Part, @Query(Api.API_UID)uid:String?): Observable<ResponseResult<String>>

    @POST(Api.API_SERVER_COMMON)
    fun GetUserInfoById(@Query(Api.KEY)key: String?,@Query(Api.API_UID)uid: String?):Observable<ResponseResult<CTUser>>

    @POST(Api.API_SERVER_MATCH)
    fun StartMatch(@Query(Api.KEY)key: String?,@Query(Api.API_UID)uid: String?,@Query(Api.API_SCHOOLCODE)schoolcode: String?):Observable<ResponseResult<Boolean>>

    @POST(Api.API_SERVER_MATCH)
    fun StopMatch(@Query(Api.KEY)key: String?,@Query(Api.API_UID)uid: String?,@Query(Api.API_SCHOOLCODE)schoolcode: String?):Observable<ResponseResult<Boolean>>

    @POST(Api.API_SERVER_COMMON)
    fun FollowEvents(@Query(Api.KEY)key:String?,@Query(Api.API_UID)uid:String?,@Query(Api.API_TID)tid:String?,@Query(Api.API_OP)op:String):Observable<ResponseResult<Boolean>>

    @POST(Api.API_SERVER_COMMON)
    fun GetFollowList(@Query(Api.KEY)key:String?,@Query(Api.API_UID)uid:String?):Observable<ResponseResult<ArrayList<CTUser>>>

    @POST(Api.API_SERVER_GPSINFO)
    fun uploadGpsInfo(@Query(Api.KEY)key:String?,@Query(Api.API_GPS)location:String):Observable<ResponseResult<Boolean>>
}