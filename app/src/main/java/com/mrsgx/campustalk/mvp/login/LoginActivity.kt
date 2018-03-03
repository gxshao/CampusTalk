package com.mrsgx.campustalk.mvp.login

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.BaseActivity
import com.mrsgx.campustalk.mvp.register.RegisterActivity
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.ref.WeakReference
import java.util.*

class LoginActivity : BaseActivity(), LoginContract.View {
    override fun isAutoLogin(): Boolean {
        return cb_autologin.isChecked
    }

    var stopTextAnimation = false
    private var loginpresenter: LoginPresenter? = null
    private var rootView: View? = null
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
                    if (stopTextAnimation)
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
        requestItem=0
        requestAllPermissions()
    }

    private var requestItem = 0
    /**
     * 请求所有的权限
     */
    private fun requestAllPermissions() {
        //请求所有权限
        val msg = mHandler.obtainMessage()
        msg.what = LoginHandler.MSGTYPE_REQUEST_PERMISSIONS
        msg.obj = GlobalVar.permissionList[requestItem++]
        mHandler.sendMessage(msg)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (!grantResults.isNotEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showMessage("权限:" + permissions[0] + "获取失败,请手动尝试获取！")
            }
        }
        if (requestItem < GlobalVar.permissionList.size)
            requestAllPermissions()
    }

    class LoginHandler(activity: LoginActivity) : Handler() {
        private val loginHandler: WeakReference<LoginActivity> by lazy {
            WeakReference<LoginActivity>(activity)
        }

        companion object {
            const val MSGTYPE_STOP_ANIMATION = 1
            const val MSGTYPE_REQUEST_PERMISSIONS = 2
        }

        override fun handleMessage(msg: Message?) {
            val activity = loginHandler.get() ?: return
            when (msg!!.what) {
                MSGTYPE_STOP_ANIMATION -> {
                    activity.anim_title?.animateText(activity.text!![msg.obj as Int])
                }
            //获取相应权限
                MSGTYPE_REQUEST_PERMISSIONS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(activity, msg.obj.toString()) != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(kotlin.arrayOf(msg.obj.toString()), 1)
                    }else{
                        if (activity.requestItem < GlobalVar.permissionList.size)
                             activity.requestAllPermissions()
                    }
                }
            }

            super.handleMessage(msg)
        }
    }

    override fun Close() {
        stopTextAnimation = true
        if (loginpresenter != null)
            loginpresenter!!.compositeDisposable.dispose()
        loginpresenter = null

    }

    override fun finishActivity() {
        TalkerProgressHelper.getInstance(this).hideDialog()
        this.finish()
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun startNewPage(target: Class<*>?) {

        startActivity(Intent(this, target))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
        Close()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
       if(keyCode == KeyEvent.KEYCODE_BACK){
          if(super.onKeyDown(keyCode, event))
              this.finish()
           else{
              val applicationName = resources.getString(
                      R.string.app_name)
              val msg = "再按一次返回键退出" + applicationName
              //String msg1 = "再按一次返回键回到桌面";
              showMessage(msg)
          }
           return true
       }
        return super.onKeyDown(keyCode, event)
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
