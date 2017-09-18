package com.mrsgx.campustalk.mvp.Regesiter

import android.content.Context
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
 * Created by Shao on 2017/9/18.
 */
class RegesiterPensenter(private val view: RegesiterContract.View, private val workerRepository: WorkerRepository, private val context: Context) : RegesiterContract.Presenter {
    override fun getCode() {
        //发出获取邮件请求
        val disposiable = workerRepository.GetCode().observeOn(AndroidSchedulers.mainThread())
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
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
        compositeDisposable.add(disposiable)
    }

    override fun RegAccount(user: CTUser,code:String) {
        TalkerProgressHelper.getInstance(context).show(context.getString(R.string.reging_please_wait))
        var json=""
        val disposiable=workerRepository.RegAccount(json,code).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<CTUser>>(){
                    override fun onComplete() {
                       TalkerProgressHelper.getInstance(context).hide()
                    }

                    override fun onError(e: Throwable?) {
                       view.showMessage(context.getString(R.string.reg_fail_unknow))
                    }

                    override fun onNext(value: ResponseResult<CTUser>?) {
                        if(value!=null){
                            val usr=value.Body
                            if(usr==null)
                            {
                                view.showMessage(context.getString(R.string.reg_fail_wrong_code))
                                return
                            }else
                            {
                                GlobalVar.LOCAL_USER=usr
                               val edit= SharedHelper.getInstance(context).edit()
                                edit.putString(SharedHelper.KEY_EMAIL,user.Email)
                                edit.putString(SharedHelper.KEY_PWD,user.Password)
                                edit.apply()
                                view.startNewPage(MainActivity::class.java)
                            }
                        }else
                        {
                            view.showMessage(context.getString(R.string.reg_fail_unknow))
                        }
                    }
                })
        compositeDisposable.add(disposiable)
    }

    companion object {
        var IS_EMAIL_AVILIABLE = false
    }

    var compositeDisposable = CompositeDisposable()
    override fun CheckEmail(email: String) {
        val disposiable = workerRepository.CheckEmail(email).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onError(e: Throwable?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onComplete() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if (value != null) {
                            val b = value.Body as Boolean
                            RegesiterPensenter.IS_EMAIL_AVILIABLE = b
                            //根据判断结果修改UI状态
                            view.setEmailBoxState(b)
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