package com.mrsgx.campustalk.mvp.Login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Main.MainActivity
import com.mrsgx.campustalk.mvp.Regesiter.RegisterActivity
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : Activity(), LoginContract.View {
    override fun finishActivity() {
        this.finish()
    }

    var STOP_TEXT_ANIM = false
    var loginpresenter: LoginPresenter? = null

    companion object {
        var LOGIN_INSTANCE: LoginActivity? = null
    }

    override fun initViews() {

        var i=0
        val timer=Timer()
        anim_title.postDelayed({
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if(STOP_TEXT_ANIM)
                        this.cancel()
                    val msg=mHandler.obtainMessage()
                    msg.obj=i++
                    mHandler.sendMessage(msg)
                    if(i>1)
                        i=0
                }

            },100,5000)
        }, 1000)
        val anim_login_sub_box=AnimationUtils.loadAnimation(this,R.anim.login_box_enter)
        login_sub_box.post{
            login_sub_box.startAnimation(anim_login_sub_box)
        }
    }
    val mHandler= @SuppressLint("HandlerLeak")
    object : Handler(){

        override fun dispatchMessage(msg: Message?) {
            anim_title.animateText(text!![msg!!.obj as Int])
            super.dispatchMessage(msg)
        }
    }
    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this, target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    override fun setPresenter(presenter: LoginContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        STOP_TEXT_ANIM=true
        super.onDestroy()
    }
    var text:Array<String>?=null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_login)
        initViews()
        text= arrayOf(this.resources.getString(R.string.app_name),this.resources.getString(R.string.sign_in)+"☺")
        LOGIN_INSTANCE = this
        loginpresenter = LoginPresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
        btn_login.setOnClickListener {
          //  TalkerProgressHelper.getInstance(this).show(this.resources.getString(R.string.login))
           startNewPage(MainActivity::class.java)
        }
        btn_reg.setOnClickListener {
            startNewPage(RegisterActivity::class.java)
        }

    }
}
