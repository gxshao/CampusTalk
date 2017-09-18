package com.mrsgx.campustalk.mvp.Regesiter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.view.Window
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.RegMatchs
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_regesiter.*
import java.util.*

class RegesiterActivity : Activity(), RegesiterContract.View {
    override fun setEmailBoxState(b: Boolean) {
        if(b){
            ed_email.setBackgroundColor(this.resources.getColor(R.color.welcome_bg))
        }else
            ed_email.setBackgroundColor(this.resources.getColor(R.color.colorAccent))
    }

    override fun initViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this,target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        this.finish()
    }

    override fun setPresenter(presenter: RegesiterContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var presenter: RegesiterPensenter? = null
    var context: Context? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_regesiter)
        context = this
        presenter = RegesiterPensenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()!!), this)
        //邮件地址检测
        ed_email.setOnFocusChangeListener { view, b ->
            kotlin.run {
                if (!b) {
                    val email = view.ed_email.text.toString()
                    if (email.isNotEmpty() && RegMatchs.MatchEmail(email))
                        presenter!!.CheckEmail(email)
                }
            }
        }
        //获取验证码事件
        btn_getcode.setOnClickListener {
            //发起邮件请求
            if (!RegesiterPensenter.IS_EMAIL_AVILIABLE) {
                showMessage(context!!.getString(R.string.getcode_fail_wrong_email))
            } else {
                presenter!!.getCode()
                //改变按钮状态
                btn_getcode.isEnabled = false
                val msg = mHand.obtainMessage()
                var count = 60
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        msg.what = 1
                        if (count == 0) {
                            this.cancel()
                        }
                        count--
                        msg.obj = count
                        mHand.sendMessage(msg)
                    }
                }, 10, 1000)
            }
        }

        //提交注册
        btn_submit.setOnClickListener {
            presenter!!.RegAccount(CTUser(),"")
        }

    }

    val mHand = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun dispatchMessage(msg: Message) {
            if (msg.what == 1) {
                val value = msg.obj as Int
                if (value == 0) {
                    btn_getcode.isEnabled = true
                    btn_getcode.text = context!!.getString(R.string.getvalidatecode)
                } else {
                    btn_getcode.text = context!!.getString(R.string.getvalidatecode) + "(" + value + ")"
                }

            }
            super.dispatchMessage(msg)
        }
    }

}
