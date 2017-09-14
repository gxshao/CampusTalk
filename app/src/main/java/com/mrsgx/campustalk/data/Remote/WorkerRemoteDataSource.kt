package com.mrsgx.campustalk.data.Remote

import com.mrsgx.campustalk.data.DataSource
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.retrofit.RetrofitClient
import com.mrsgx.campustalk.retrofit.RetrofitService
import io.reactivex.Observable

/**
 * Created by Shao on 2017/9/14.
 */
class WorkerRemoteDataSource : DataSource {
    override fun Login(email: String?, pwd: String?): Observable<ResponseResult<CTUser>> {
        return RetrofitClient.getInstance()!!.create(RetrofitService::class.java).Login("login", email!!, pwd!!)
    }

    companion object {
        fun getInstance(): WorkerRemoteDataSource? {
            var INSTANCE: WorkerRemoteDataSource? = null
            if (INSTANCE == null) {
                INSTANCE = WorkerRemoteDataSource()
            }
            return INSTANCE
        }
    }
}