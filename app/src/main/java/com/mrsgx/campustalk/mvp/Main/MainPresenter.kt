package com.mrsgx.campustalk.mvp.Main

import android.content.ComponentName
import android.content.Context
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Local.DB
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import io.reactivex.android.schedulers.AndroidSchedulers
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