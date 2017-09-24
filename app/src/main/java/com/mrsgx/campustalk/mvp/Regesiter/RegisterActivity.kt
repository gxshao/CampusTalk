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
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.RegMatchs
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_regesiter.*
import java.util.*

class RegisterActivity : Activity(), RegisterContract.View {
    override fun setEmailBoxState(b: Boolean) {
        if (b) {
            ed_email.setBackgroundColor(this.resources.getColor(R.color.welcome_bg))
        } else
            ed_email.setBackgroundColor(this.resources.getColor(R.color.colorAccent))
    }

    override fun initViews() {
        //加载入场动画
        val reg_box_anim=AnimationUtils.loadAnimation(this,R.anim.reg_box_enter)
        reg_box.post {
            reg_box.startAnimation(reg_box_anim)
        }
    }

    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this, target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        this.finish()
    }

    override fun setPresenter(presenter: RegisterContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var presenter: RegisterPensenter? = null
    var context: Context? = null
    var rootview: View?=null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_regesiter)
        initViews()
        context = this
        presenter = RegisterPensenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
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
            println(ed_email.text.toString())
            //发起邮件请求
            if (RegisterPensenter.IS_EMAIL_AVILIABLE) {
                showMessage(context!!.getString(R.string.getcode_fail_wrong_email))
            } else {
                presenter!!.SendCode(ed_email.text.toString())
                //改变按钮状态
                btn_getcode.isEnabled = false

                var count = 60
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        synchronized(this) {
                            val msg = mHand.obtainMessage()
                            msg.what = 1
                            if (count == 0) {
                                this.cancel()
                                return
                            }
                            count--
                            msg.obj = count
                            mHand.sendMessage(msg)
                        }
                    }
                }, 10, 1000)
            }
        }
        rootview=LayoutInflater.from(this).inflate(R.layout.activity_regesiter,null)
        //提交注册
        btn_submit.setOnClickListener {

            CTNote.getInstance(this,rootview!!).show("xxx","xxx",0,CTNote.LEVEL_TIPS)
            //获取信息并判断然后提交
           // presenter!!.RegAccount(CTUser(), "123456")
        }

    }

    override fun onDestroy() {
        CTNote.getInstance(this,rootview!!).hide()
        super.onDestroy()
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
