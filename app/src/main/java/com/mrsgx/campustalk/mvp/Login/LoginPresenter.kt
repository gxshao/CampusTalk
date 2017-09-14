package com.mrsgx.campustalk.mvp.Login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shao on 2017/9/14.
 */
class LoginPresenter(private val view:LoginContract.View, private val workerRepository:WorkerRepository, context: Context):LoginContract.Presenter{
    /**
     * 登录事件
     */
    override fun Login(email: String?, pwd: String?) {
        var disposable=workerRepository.Login(email,pwd).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object :DisposableObserver<ResponseResult<CTUser>>(){
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable?) {
                        println(e!!.message)
                    }

                    override fun onNext(value: ResponseResult<CTUser>) {
                        var  user=value.Body
                        view.showMessage(user!!.Email)
                    }
                })

        compositeDisposable.add(disposable)

    }

    var sharedpref:SharedPreferences?=context.getSharedPreferences("config",Activity.MODE_PRIVATE)
    var compositeDisposable=CompositeDisposable()
    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {

    }

}


