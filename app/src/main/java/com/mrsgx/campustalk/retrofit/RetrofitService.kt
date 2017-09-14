package com.mrsgx.campustalk.retrofit

import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.obj.CTUser
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Shao on 2017/9/4.
 */
interface RetrofitService {
    @GET(Api.API_VALIDATE)
     fun Login(@Query(Api.KEY)key:String?, @Query(Api.API_EMAIL) email: String,@Query(Api.API_PWD) pwd: String): Observable<ResponseResult<CTUser>>
}