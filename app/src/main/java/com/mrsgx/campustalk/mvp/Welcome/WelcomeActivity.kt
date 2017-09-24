package com.mrsgx.campustalk.mvp.Welcome

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.view.KeyEvent
import android.view.Window
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Login.LoginActivity
import com.mrsgx.campustalk.utils.SharedHelper
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.*

class WelcomeActivity : Activity(), WelcomeContract.View {
    override fun setPresenter(presenter: WelcomeContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this,target),ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        this.finish()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_welcome)

        val anim=AnimatorInflater.loadAnimator(this,R.animator.anim_appname)
        anim.setTarget(txt_apptitle)
        anim.start()
        val timer = Timer()
        var count = 5
        val context = this
        val welpresenter = WelcomePresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
        timer.schedule(object : TimerTask() {
            override fun run() {
                val msg = mHand.obtainMessage()
                if (count == 0) {
                    //检测是否已经登录
                    val shared = SharedHelper.getInstance(context)
                    val email = shared.getString(SharedHelper.KEY_EMAIL, null)
                    val pwd = shared.getString(SharedHelper.KEY_PWD, null)
                    if (email == null || email == "" || pwd == null || pwd == "") {
                        //初次登录或账号注销
                        msg.what=2
                        mHand.sendMessage(msg)
                    } else {
                        //登录校验
                        welpresenter.Login(email, pwd)
                    }
                    timer.cancel()
                } else {
                    count--
                    msg.what = 1
                    mHand.sendMessage(msg)
                }
            }

        }, 1000, 1000)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event!!.repeatCount==0)
            return false
        return super.onKeyDown(keyCode, event)
    }
   private var mHand = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun dispatchMessage(msg: Message) {
            if (msg.what == 1) {
                txt_count.append(".")
            }
            if(msg.what==2){
                startNewPage(LoginActivity::class.java)
            }
            super.dispatchMessage(msg)
        }
    }

/*
        var con = CTConnection.getInstance(this)
        send.setOnClickListener {
            object : Thread() {
                override fun run() {
                  /*  var json = Gson()
                    var school = CTSchool()
                    school.sCode = "111"
                    school.sName = "123456"


                    var user = CTUser()
                    user.school = school
                    user.sex = "0"
                    user.uid = "1230"


                    var text = CTData<CTUser>()
                    text.DataType = "0"
                    text.Body = user
                    var t = json.toJson(text).toString()
                    println(t)
                    var da=json.fromJson(t, CTData::class.java)
                    println(da.Body)

                    con.Send(json.toJson(text).toString().trim(), object : SendCallback() {
                        override fun OnError(ex: Exception?) {
                        }

                        override fun OnSent(messageSent: CharSequence?) {

                        }
                    })*/
                    super.run()
                }
            }.start()
        }
        disconnect.setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                       con.Stop()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    super.run()
                }
            }.start()
        }
        connect.setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                        con.Start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    super.run()
                }
            }.start()
        }*/

}
