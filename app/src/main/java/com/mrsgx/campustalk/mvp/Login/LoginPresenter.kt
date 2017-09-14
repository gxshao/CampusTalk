package com.mrsgx.campustalk.mvp.Login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.mrsgx.campustalk.data.WorkerRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Shao on 2017/9/14.
 */
class LoginPresenter(view:LoginContract.View,workerRepository:WorkerRepository,compositeDisposable: CompositeDisposable,context: Context):LoginContract.Presenter{
    override fun Login(email: String?, pwd: String?) {

    }

    var sharedpref:SharedPreferences?=context.getSharedPreferences("config",Activity.MODE_PRIVATE)
    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {

    }

}