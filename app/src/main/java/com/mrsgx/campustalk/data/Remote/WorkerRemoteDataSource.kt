package com.mrsgx.campustalk.data.Remote

import com.mrsgx.campustalk.data.DataSource
import com.mrsgx.campustalk.data.ResponseResult
import io.reactivex.Observable

/**
 * Created by Shao on 2017/9/14.
 */
class WorkerRemoteDataSource: DataSource {
    override fun Login(email: String?, pwd: String?): Observable<ResponseResult<String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}