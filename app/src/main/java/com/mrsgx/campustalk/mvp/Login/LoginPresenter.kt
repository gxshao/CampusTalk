package com.mrsgx.campustalk.mvp.Login

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Main.MainActivity
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.SharedHelper
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shao on 2017/9/14.
 */
class LoginPresenter(private val view:LoginContract.View, private val workerRepository:WorkerRepository, private val context: Context):LoginContract.Presenter {
    /**
     * 登录事件
     */
    override fun Login(email: String?, pwd: String?) {
        TalkerProgressHelper.getInstance(context).show(context.getString(R.string.login))
        val disposable=workerRepository.Login(email,pwd).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object :DisposableObserver<ResponseResult<CTUser>>(){
                    override fun onComplete() {
                        TalkerProgressHelper.getInstance(context).hide()
                    }

                    override fun onError(e: Throwable?) {
                       view.showMessage(context.getString(R.string.login_failed_problem_network))
                    }
                    @SuppressLint("CommitPrefEdits")
                    override fun onNext(value: ResponseResult<CTUser>) {
                        val user=value.Body
                        if(user!=null){
                            if(user.Email==null||user.Email.equals(""))
                            {
                                view.showMessage(context.getString(R.string.login_failed_problem_email))
                                return
                            }
                            if(user.Uid==null||user.Uid.equals("")){
                                view.showMessage(context.getString(R.string.login_failed_problem_password))
                                return
                            }
                            if(user.State=="2"){
                                view.showMessage(context.getString(R.string.login_failed_problem_stopped))
                                return
                            }
                            GlobalVar.LOCAL_USER=user
                            //存储到本地方便快速登
                            val edit:Editor=sharedprefs!!.edit()
                            edit.putString("email",user.Email)
                            edit.putString("pass",pwd)
                            edit.apply()
                            view.startNewPage(MainActivity::class.java)
                        }else
                        {
                            view.showMessage(context.getString(R.string.login_failed_problem_network))
                        }
                    }
                })
        compositeDisposable.add(disposable)

    }
    private var sharedprefs:SharedPreferences?=SharedHelper.getInstance(context)
    private var compositeDisposable=CompositeDisposable()

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {

    }

}


