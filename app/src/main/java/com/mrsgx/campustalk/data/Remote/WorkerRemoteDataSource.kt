package com.mrsgx.campustalk.data.Remote

import com.mrsgx.campustalk.data.DataSource
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.retrofit.Api
import com.mrsgx.campustalk.retrofit.RetrofitClient
import com.mrsgx.campustalk.retrofit.RetrofitService
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Shao on 2017/9/14.
 */
class WorkerRemoteDataSource : DataSource {
    override fun StopMatch(uid: String?, schoolcode: String?): Observable<ResponseResult<Boolean>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).StopMatch(Api.API_QUIT,uid,schoolcode)
    }

    override fun StartMatch(uid: String?, schoolcode: String?): Observable<ResponseResult<Boolean>> {

        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).StartMatch(Api.API_MATCH,uid,schoolcode)
    }

    override fun GetUserInfoById(uid: String?): Observable<ResponseResult<CTUser>> {

        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).GetUserInfoById("getUserProfile",uid)
    }


    override fun UploadFile(key:String,file: MultipartBody.Part, uid: String): Observable<ResponseResult<String>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).UploadFile(key,file,uid)
    }

    override fun UpdateUserProfile(user: String): Observable<ResponseResult<Boolean>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).UpdateUserProfile("updateprofile",user)
    }

    override fun GetSchoolData(): Observable<ResponseResult<String>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).GetSchoolData("schoolinfo")
    }

    override fun RegAccount(user: String, code: String): Observable<ResponseResult<CTUser>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).RegAccount("regesiter", user, code)
    }


    override fun SendCode(email: String): Observable<ResponseResult<Boolean>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).SendCode("sendcode", email)
    }

    override fun CheckEmail(email: String?): Observable<ResponseResult<Boolean>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).CheckEmail("ckemail", email!!)
    }

    override fun Login(email: String?, pwd: String?): Observable<ResponseResult<CTUser>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).Login("login", email!!, pwd!!)
    }

    companion object {
        private var INSTANCE: WorkerRemoteDataSource? = null
        fun getInstance(): WorkerRemoteDataSource {
            if (INSTANCE == null)
                INSTANCE = WorkerRemoteDataSource()
            return INSTANCE!!
        }
    }
}