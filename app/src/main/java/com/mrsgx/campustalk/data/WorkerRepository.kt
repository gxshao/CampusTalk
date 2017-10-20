package com.mrsgx.campustalk.data

import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.obj.CTLocation
import com.mrsgx.campustalk.obj.CTUser
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Shao on 2017/9/14.
 */
class WorkerRepository(private val workerRemoteDataSource: DataSource) : DataSource {
    override fun SignUp(uid: String?): Observable<ResponseResult<Boolean>> {
        return workerRemoteDataSource.SignUp(uid)
    }

    override fun CheckSign(uid: String?): Observable<ResponseResult<Boolean>> {
        return workerRemoteDataSource.CheckSign(uid)
    }

    override fun GetUserProperty(uid: String?): Observable<ResponseResult<String>> {
        return workerRemoteDataSource.GetUserProperty(uid)
    }

    override fun getLocationList(uid: String?, time: String): Observable<ResponseResult<ArrayList<CTLocation>>> {
        return workerRemoteDataSource.getLocationList(uid,time)
    }

    override fun getUserListByLoc(uid: String?, location: String): Observable<ResponseResult<ArrayList<CTUser>>> {
        return workerRemoteDataSource.getUserListByLoc(uid,location)
    }

    override fun uploadGpsInfo(location: String): Observable<ResponseResult<Boolean>> {
        return workerRemoteDataSource.uploadGpsInfo(location)
    }

    override fun GetFollowList( uid: String?): Observable<ResponseResult<ArrayList<CTUser>>> {
        return workerRemoteDataSource.GetFollowList(uid)
    }

    override fun FollowEvents(uid:String?,tid: String?, op: String): Observable<ResponseResult<Boolean>> {
        return workerRemoteDataSource.FollowEvents(uid,tid,op)
    }

    override fun StopMatch(uid: String?, schoolcode: String?): Observable<ResponseResult<Boolean>> {

        return workerRemoteDataSource.StopMatch(uid,schoolcode)
    }

    override fun StartMatch(uid: String?, schoolcode: String?): Observable<ResponseResult<Boolean>> {
        return workerRemoteDataSource.StartMatch(uid,schoolcode)
    }

    override fun GetUserInfoById(uid: String?): Observable<ResponseResult<CTUser>> {

        return workerRemoteDataSource.GetUserInfoById(uid)
    }

    override fun UploadFile(key:String,file: MultipartBody.Part, uid: String): Observable<ResponseResult<String>> {
        return workerRemoteDataSource.UploadFile(key,file,uid)
    }

    override fun UpdateUserProfile( user: String): Observable<ResponseResult<Boolean>> {

        return workerRemoteDataSource.UpdateUserProfile(user)
    }

    override fun GetSchoolData(): Observable<ResponseResult<String>> {
        return workerRemoteDataSource.GetSchoolData()
    }

    override fun SendCode(email:String): Observable<ResponseResult<Boolean>> {
      return workerRemoteDataSource.SendCode(email)
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
        private var INSTANCE: WorkerRepository?=null
        fun getInstance(remoteData: DataSource): WorkerRepository {
            if(INSTANCE==null)
            INSTANCE = WorkerRepository(remoteData)
            return INSTANCE!!
        }
    }

}