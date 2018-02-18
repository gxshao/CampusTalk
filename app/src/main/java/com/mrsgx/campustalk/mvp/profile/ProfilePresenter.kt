package com.mrsgx.campustalk.mvp.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import com.google.gson.Gson
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.local.DB
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Shao on 2017/10/3.
 */
class ProfilePresenter(private val view: ProfileContract.View, private val workerRepository: WorkerRepository, private val context: Context):ProfileContract.Presenter {
    override fun uplpadstucard(path: String, uid: String) {
        IS_STUCARD_UPLOADED=1
        val file=File(path)
        if(file.exists()&&file.isFile){
            val requestBody=RequestBody.create(MediaType.parse("multipart/form-data"),file)
            val image=MultipartBody.Part.createFormData(file.path,file.name,requestBody)
            workerRepository.UploadFile("uploadstucard",image,uid).observeOn(AndroidSchedulers.mainThread()!!)
                    .subscribeOn(Schedulers.io()!!)
                    .subscribeWith(object : DisposableObserver<ResponseResult<String>>(){
                        override fun onComplete() {

                        }

                        override fun onError(e: Throwable?) {
                            IS_STUCARD_UPLOADED=1
                            view.showMessage("学生卡上传失败",CTNote.LEVEL_ERROR,CTNote.TIME_SHORT)
                        }

                        override fun onNext(value: ResponseResult<String>?) {
                            IS_STUCARD_UPLOADED=2

                            if(GlobalVar.LOCAL_USER!=null)
                            {
                                GlobalVar.LOCAL_USER!!.Stucard =value!!.Body
                                view.onStucardUpload(value.Body!!)

                            }
                            mHand.sendEmptyMessage(1)
                        }
                    })
        }

    }

    private var compositeDisposable= CompositeDisposable()
    init {

    }
    override fun submitProfile(user: CTUser) {
        val jsonUser=Gson().toJson(user)
        val disposiable=workerRepository.UpdateUserProfile(jsonUser).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>(){
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if (value!!.Body as Boolean){
                            user.State=GlobalVar.USER_STATE_WAITING
//                            if(!GlobalVar.LOCAL_USER!!.Stucard.isNullOrEmpty())
//                                user.Stucard=GlobalVar.LOCAL_USER!!.Stucard
                            GlobalVar.LOCAL_USER=user
                            DB.getInstance(context).insertOrUpdateUser(user)
                            view.showMessage(context.getString(R.string.upload_success),CTNote.LEVEL_TIPS,CTNote.TIME_SHORT)
                            mHand.sendEmptyMessage(0)
                            IS_PROFILE_UPLOADED=true
                        }else
                        {
                            IS_PROFILE_UPLOADED=false
                        }
                    }
                    override fun onComplete() {
                        TalkerProgressHelper.getInstance(context).hideDialog()
                    }

                    override fun onError(e: Throwable?) {
                        TalkerProgressHelper.getInstance(context).hideDialog()
                        IS_PROFILE_UPLOADED=false
                        view.showMessage(context.getString(R.string.upload_fail),CTNote.LEVEL_ERROR,CTNote.TIME_SHORT)
                    }
                })
        compositeDisposable.add(disposiable)
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
    }
    var IS_PROFILE_UPLOADED=false
    var IS_STUCARD_UPLOADED=0 //0未激活 1未传成功 2 传成功
    val mHand= @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun dispatchMessage(msg: Message?) {
            if(IS_PROFILE_UPLOADED&&IS_STUCARD_UPLOADED==2||IS_STUCARD_UPLOADED==0){
                view.Close()
            }
            super.dispatchMessage(msg)
        }
    }
}