package com.mrsgx.campustalk.mvp.Welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Local.DB
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Login.LoginActivity
import com.mrsgx.campustalk.mvp.Main.MainActivity
import com.mrsgx.campustalk.obj.CTArea
import com.mrsgx.campustalk.obj.CTSchool
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.SharedHelper
import com.mrsgx.campustalk.widget.CTNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shao on 2017/9/4.
 */
class WelcomePresenter(private val view: WelcomeContract.View, private val workerRepository: WorkerRepository, private val context: Context) : WelcomeContract.Presenter {

    override fun loadingResources() {
        val disposiabl = workerRepository.GetSchoolData().observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<ResponseResult<String>>() {
                    override fun onComplete() {
                        view.loadingPost()
                    }

                    @SuppressLint("CommitPrefEdits")
                    override fun onNext(value: ResponseResult<String>?) {
                        if (value != null && !value.Body.isNullOrEmpty()) {
                            val arr = value.Body!!.split(GlobalVar.SPILTER)
                            if (arr.size == 2) {
                                val aType = object : TypeToken<ArrayList<CTArea>>() {}.type
                                DB.getInstance(context).insertArea(Gson().fromJson(arr[0], aType))
                                val sType = object : TypeToken<ArrayList<CTSchool>>() {}.type
                                DB.getInstance(context).insertSchool(Gson().fromJson(arr[1], sType))
                                val edit = SharedHelper.getInstance(context).edit()
                                edit.putBoolean(SharedHelper.FIRST_LOAD, false)
                                edit.apply()
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
                        println(e)
                        view.showMessage(context.getString(R.string.fail_load_resources), CTNote.LEVEL_WARNING, CTNote.TIME_SHORT)
                        view.loadingPost()
                    }
                })
        compositeDisposable.add(disposiabl)
    }

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
        val disposable = workerRepository.Login(email, pwd).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<CTUser>>() {
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable?) {
                        view.startNewPage(LoginActivity::class.java)
                    }

                    @SuppressLint("CommitPrefEdits")
                    override fun onNext(value: ResponseResult<CTUser>) {
                        val user = value.Body
                        if (user != null) {
                            if (user.Email == null || user.Email.equals("")) {
                                view.showMessage(context.getString(R.string.login_failed_problem_email))
                                view.startNewPage(LoginActivity::class.java)
                                return
                            }
                            if (user.Uid == null || user.Uid.equals("")) {
                                view.showMessage(context.getString(R.string.login_failed_problem_password))
                                view.startNewPage(LoginActivity::class.java)
                                return
                            }
                            if (user.State == GlobalVar.USER_STATE_STOPPED) {
                                view.showMessage(context.getString(R.string.login_failed_problem_stopped))
                                view.startNewPage(LoginActivity::class.java)
                                return
                            }
                            GlobalVar.LOCAL_USER = user
                            //存储到本地方便快速登
                            val edit: SharedPreferences.Editor = sharedpref!!.edit()
                            edit.putString("email", user.Email)
                            edit.putString("pass", pwd)
                            edit.apply()
                            view.startNewPage(MainActivity::class.java)
                        } else {
                            view.showMessage(context.getString(R.string.login_failed_problem_network))
                            view.startNewPage(LoginActivity::class.java)
                        }
                    }
                })
        compositeDisposable.add(disposable)

    }

    var sharedpref: SharedPreferences? = SharedHelper.getInstance(context)
    var compositeDisposable = CompositeDisposable()
}