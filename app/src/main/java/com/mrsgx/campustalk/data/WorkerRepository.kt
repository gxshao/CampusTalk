package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import io.reactivex.Observable

/**
 * Created by Shao on 2017/9/14.
 */
class WorkerRepository(workerRemoteDataSource: DataSource):DataSource {

    override fun Login(email: String?, pwd: String?): Observable<ResponseResult<String>>? {
        return  null
    }
    companion object {
        var INSTANCE:WorkerRepository?=null
        fun getInstance(remotedata:DataSource):WorkerRepository{
            if(INSTANCE==null){
                INSTANCE= WorkerRepository(remotedata)
            }
            return INSTANCE!!
        }
    }

}