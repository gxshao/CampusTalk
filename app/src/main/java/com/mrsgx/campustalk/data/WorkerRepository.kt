package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.obj.CTUser
import io.reactivex.Observable

/**
 * Created by Shao on 2017/9/14.
 */
class WorkerRepository(private val workerRemoteDataSource: DataSource) : DataSource {
    override fun GetCode(): Observable<ResponseResult<Boolean>> {
      return workerRemoteDataSource.GetCode()
    }

    override fun RegAccount(user: String,code:String): Observable<ResponseResult<CTUser>> {
       return workerRemoteDataSource.RegAccount(user,code)
    }

    override fun CheckEmail(email: String?): Observable<ResponseResult<Boolean>> {
       return workerRemoteDataSource.CheckEmail(email)
    }

    override fun Login(email: String?, pwd: String?): Observable<ResponseResult<CTUser>> {
        return workerRemoteDataSource.Login(email, pwd)!!
    }

    companion object {
        private var INSTANCE: WorkerRepository? = null
        fun getInstance(remotedata: DataSource): WorkerRepository {
            if (INSTANCE == null) {
                INSTANCE = WorkerRepository(remotedata)
            }
            return INSTANCE!!
        }
    }

}