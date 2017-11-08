package com.mrsgx.campustalk.mvp.Login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Regesiter.RegisterActivity
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.ref.WeakReference
import java.util.*

class LoginActivity : Activity(), LoginContract.View {
    override fun isAutoLogin(): Boolean {
        return cb_autologin.isChecked
    }

    var STOP_TEXT_ANIM = false
    var loginpresenter: LoginPresenter? = null
    var rootView: View? = null
    lateinit var mHandler: LoginHandler

    companion object {
        var LOGIN_INSTANCE: WeakReference<LoginActivity>? = null
    }

    override fun initViews() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_login, null)
        var i = 0
        val timer = Timer()
        anim_title.postDelayed({
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (STOP_TEXT_ANIM)
                        this.cancel()
                    val msg = mHandler.obtainMessage()
                    msg.obj = i++
                    mHandler.sendMessage(msg)
                    if (i > 1)
                        i = 0
                }

            }, 100, 5000)
        }, 1000)
        val animLoginBox = AnimationUtils.loadAnimation(this, R.anim.login_box_enter)
        login_sub_box.post {
            login_sub_box.startAnimation(animLoginBox)
        }
        btn_login.setOnClickListener {
            loginpresenter!!.Login(ed_email.text.toString(), ed_pass.text.toString())
        }
        btn_reg.setOnClickListener {
            startNewPage(RegisterActivity::class.java)
        }

    }

    class LoginHandler(activity: LoginActivity) : Handler() {
        private val loginHandler: WeakReference<LoginActivity> by lazy {
            WeakReference<LoginActivity>(activity)
        }

        override fun handleMessage(msg: Message?) {
            val activity = loginHandler.get() ?: return
            activity.anim_title.animateText(activity.text!![msg!!.obj as Int])
            super.handleMessage(msg)
        }
    }

    override fun Close() {
    }

    override fun finishActivity() {
        TalkerProgressHelper.getInstance(this).hideDialog()
        this.finish()
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun startNewPage(target: Class<*>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(Intent(this, target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(Intent(this, target))
        }
    }

    //自定义提示
    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this, rootView!!).show(msg, level, time)
    }

    override fun setPresenter(presenter: LoginContract.Presenter?) {

    }

    override fun onStop() {
        CTNote.getInstance(this, rootView!!).hide()
        super.onStop()
    }

    override fun onDestroy() {
        STOP_TEXT_ANIM = true
        loginpresenter = null
        System.gc()
        super.onDestroy()
    }

    var text: Array<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_login)
        mHandler = LoginHandler(this)
        initViews()
        text = arrayOf(this.resources.getString(R.string.app_name), this.resources.getString(R.string.sign_in) + "☺")
        LOGIN_INSTANCE = WeakReference(this)
        loginpresenter = LoginPresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)

    }
}
