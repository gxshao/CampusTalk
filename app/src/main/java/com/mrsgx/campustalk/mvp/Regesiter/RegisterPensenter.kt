package com.mrsgx.campustalk.mvp.Regesiter

import android.content.Context
import com.google.gson.Gson
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Login.LoginActivity
import com.mrsgx.campustalk.mvp.Main.MainActivity
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.SharedHelper
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shao on 2017/9/18.
 */
class RegisterPensenter(private val view: RegisterContract.View, private val workerRepository: WorkerRepository, private val context: Context) : RegisterContract.Presenter {
    override fun SendCode(email:String) {
        //发出获取邮件请求
        val disposiable = workerRepository.SendCode(email).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.email_sent_error))
                    }

                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if (value != null) {
                            if (value.Body as Boolean)
                                view.showMessage(context.getString(R.string.email_sent))
                            else
                                view.showMessage(context.getString(R.string.email_sent_error))
                        }
                    }

                    override fun onComplete() {

                    }

                })
        compositeDisposable.add(disposiable)
    }

    override fun RegAccount(user: CTUser, code:String) {
        TalkerProgressHelper.getInstance(context).show(context.getString(R.string.reging_please_wait))
        val json=Gson().toJson(user)
        val disposiable=workerRepository.RegAccount(json,code).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<CTUser>>(){
                    override fun onComplete() {
                       TalkerProgressHelper.getInstance(context).hideDialog()
                    }

                    override fun onError(e: Throwable?) {
                       view.showMessage(context.getString(R.string.reg_fail_unknow))
                        TalkerProgressHelper.getInstance(context).hideDialog()
                    }

                    override fun onNext(value: ResponseResult<CTUser>?) {
                        if(value!=null){
                            val usr=value.Body
                            if(usr!!.Email.isNullOrEmpty())
                            {
                                view.showMessage(context.getString(R.string.reg_fail_wrong_code))
                                return
                            }else
                            {
                                GlobalVar.LOCAL_USER=usr
                                val edit= SharedHelper.getInstance(context).edit()
                                edit.putString(SharedHelper.KEY_EMAIL,user.Email)
                                edit.putString(SharedHelper.KEY_PWD,user.Password)
                                edit.putBoolean(GlobalVar.AUTOLOGIN,false)
                                edit.apply()
                                view.startNewPage(MainActivity::class.java)
                                if(LoginActivity.LOGIN_INSTANCE!=null){
                                    val activity=LoginActivity.LOGIN_INSTANCE!!.get()
                                    activity?.finish()
                                }

                                view.Close()
                            }
                        }else
                        {
                            view.showMessage(context.getString(R.string.reg_fail_unknow))
                        }
                    }
                })
        compositeDisposable.add(disposiable)
    }

    var IS_EMAIL_AVILIABLE = false
    var compositeDisposable = CompositeDisposable()
    override fun CheckEmail(email: String) {
        val disposiable = workerRepository.CheckEmail(email).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.error_check_email), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if (value != null) {
                            val b = value.Body as Boolean
                            IS_EMAIL_AVILIABLE = b
                            //根据判断结果修改UI状态
                            if(!b)
                                view.showMessage(context.getString(R.string.error_reged_email), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
                        }
                    }
                })
        compositeDisposable.add(disposiable)
    }

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}