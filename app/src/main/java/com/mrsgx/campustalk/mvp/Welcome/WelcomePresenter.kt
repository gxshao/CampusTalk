package com.mrsgx.campustalk.mvp.Welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Login.LoginActivity
import com.mrsgx.campustalk.mvp.Main.MainActivity
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.SharedHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shao on 2017/9/4.
 */
class WelcomePresenter(private val view: WelcomeContract.View, private val workerRepository: WorkerRepository, private val context: Context):WelcomeContract.Presenter {
    init {

    }
    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun init() {

    }

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun Login(email: String?, pwd: String?) {
        val disposable=workerRepository.Login(email,pwd).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<CTUser>>(){
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable?) {
                        view.startNewPage(LoginActivity::class.java)
                    }
                    @SuppressLint("CommitPrefEdits")
                    override fun onNext(value: ResponseResult<CTUser>) {
                        val user=value.Body
                        if(user!=null){
                            if(user.Email==null||user.Email.equals(""))
                            {
                                view.showMessage(context.getString(R.string.login_failed_problem_email))
                                view.startNewPage(LoginActivity::class.java)
                                return
                            }
                            if(user.Uid==null||user.Uid.equals("")){
                                view.showMessage(context.getString(R.string.login_failed_problem_password))
                                view.startNewPage(LoginActivity::class.java)
                                return
                            }
                            if(user.State==GlobalVar.USER_STATE_STOPPED){
                                view.showMessage(context.getString(R.string.login_failed_problem_stopped))
                                view.startNewPage(LoginActivity::class.java)
                                return
                            }
                            GlobalVar.LOCAL_USER=user
                            //存储到本地方便快速登
                            val edit: SharedPreferences.Editor =sharedpref!!.edit()
                            edit.putString("email",user.Email)
                            edit.putString("pass",pwd)
                            edit.apply()
                            view.startNewPage(MainActivity::class.java)
                        }else
                        {
                            view.showMessage(context.getString(R.string.login_failed_problem_network))
                            view.startNewPage(LoginActivity::class.java)
                        }
                    }
                })
        compositeDisposable.add(disposable)

    }
    var sharedpref:SharedPreferences?= SharedHelper.getInstance(context)
    var compositeDisposable= CompositeDisposable()
}