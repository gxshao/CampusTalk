package com.mrsgx.campustalk.mvp.Main

import android.content.Context
import com.google.gson.Gson
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Local.DB
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTLocation
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
 * Created by Shao on 2017/9/4.
 */
class MainPresenter(private val view: MainContract.View, private val workerRepository: WorkerRepository, private val context: Context) : MainContract.Presenter {


    override fun initUserProperty(uid: String) {
        val disposable=workerRepository.GetUserProperty(uid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<String>>(){
                    override fun onNext(value: ResponseResult<String>?) {
                        view.setCoin(Integer.parseInt(value!!.Body))
                    }

                    override fun onComplete() {

                   }

                    override fun onError(e: Throwable?) {

                    }
                })
        compositeDisposable.addAll(disposable)
    }

    override fun signUp(uid: String) {
        val disposable=workerRepository.SignUp(uid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<Boolean>>(){
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body!!) {
                            view.showMessage("签到成功！金币+10")
                            view.setSignBtnSate(true)
                            view.setCoin(10)
                        }else
                        {
                            view.showMessage("签到失败，请稍候重试")
                        }
                    }
                })
        compositeDisposable.add(disposable)
    }


    private val compositeDisposable = CompositeDisposable()


    override fun updateFollowList() {
        val disposable=workerRepository.GetFollowList(GlobalVar.LOCAL_USER!!.Uid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<ArrayList<CTUser>>>(){
                    override fun onError(e: Throwable?) {
                        view.initFollowData(ArrayList())
                        view.showMessage("更新关注列表失败,请稍候重试！",CTNote.LEVEL_WARNING,CTNote.TIME_SHORT)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(value: ResponseResult<ArrayList<CTUser>>?) {
                        if(value?.Body != null){
                            view.initFollowData(value.Body!!)
                        }
                    }
                })
        compositeDisposable.add(disposable)

    }


    override fun cancelFollow(uid: String) {
        val disposable = workerRepository.FollowEvents(GlobalVar.LOCAL_USER!!.Uid, uid, GlobalVar.ACTION_UNFOLLOW).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if (value!!.Body as Boolean) {
                        } else {
                            view.showMessage(context.getString(R.string.unfollow_fail), CTNote.LEVEL_WARNING, CTNote.TIME_SHORT)
                        }

                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.unfollow_fail), CTNote.LEVEL_WARNING, CTNote.TIME_SHORT)
                    }

                    override fun onComplete() {
                    }
                })

        compositeDisposable.add(disposable)

    }
    ///废弃的方法
    override fun uploadLocationInfo(list: ArrayList<CTLocation>) {
        val disposiable=workerRepository.uploadGpsInfo(Gson().toJson(list)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<Boolean>>(){
                    override fun onComplete() {

                    }

                    override fun onNext(value: ResponseResult<Boolean>?) {

                    }

                    override fun onError(e: Throwable?) {

                    }
                })
        compositeDisposable.add(disposiable)

    }
    override fun uploadHeadpic(path: String, uid: String) {
        TalkerProgressHelper.getInstance(context).show("正在上传头像...")
        val file = File(path)
        if (file.exists() && file.isFile) {
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val image = MultipartBody.Part.createFormData(file.path, file.name, requestBody)
            workerRepository.UploadFile("uploadheadpic", image, uid).observeOn(AndroidSchedulers.mainThread()!!)
                    .subscribeOn(Schedulers.io()!!)
                    .subscribeWith(object : DisposableObserver<ResponseResult<String>>() {
                        override fun onComplete() {
                            TalkerProgressHelper.getInstance(context).hideDialog()
                        }

                        override fun onError(e: Throwable?) {
                            view.showMessage("头像上传失败", CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
                            TalkerProgressHelper.getInstance(context).hideDialog()
                        }

                        override fun onNext(value: ResponseResult<String>?) {
                            if (GlobalVar.LOCAL_USER != null) {
                                GlobalVar.LOCAL_USER!!.Headpic = value!!.Body
                                DB.getInstance(context).insertOrUpdateUser(GlobalVar.LOCAL_USER!!)
                                view.showMessage("头像上传成功", CTNote.LEVEL_TIPS, CTNote.TIME_SHORT)
                            }
                        }
                    })
        }

    }

    init {

    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}