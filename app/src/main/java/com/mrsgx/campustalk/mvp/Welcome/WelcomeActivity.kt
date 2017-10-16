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
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.R.id.txt_count
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Login.LoginActivity
import com.mrsgx.campustalk.utils.SharedHelper
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_welcome.*
import java.lang.ref.WeakReference
import java.util.*

class WelcomeActivity : Activity(), WelcomeContract.View {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this,rootView!!).show(msg,level,time)
    }

    override fun loadingPost() {
        runOnUiThread {
            initViews()
        }
    }

    override fun setPresenter(presenter: WelcomeContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initViews() {
        val timer = Timer()
        var count = 3  //等待秒数
        val context = this
        txt_count.text=getString(R.string.loading)
        timer.schedule(object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun run() {
                val msg = mHand.obtainMessage()
                if (count == 0) {
                    //检测是否已经登录
                    val shared = SharedHelper.getInstance(context)
                    val email = shared.getString(SharedHelper.KEY_EMAIL, null)
                    val pwd = shared.getString(SharedHelper.KEY_PWD, null)
                    if (email == null || email == "" || pwd == null || pwd == ""&&!shared.getBoolean(GlobalVar.AUTOLOGIN,false)) {
                        //初次登录或账号注销
                        runOnUiThread { startNewPage(LoginActivity::class.java) }
                    } else {
                        //登录校验
                        welpresenter!!.Login(email, pwd)
                    }
                    timer.cancel()
                } else {
                    count--
                    runOnUiThread { txt_count.append(".") }
                }
            }

        }, 1000, 1000)    }

    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this, target))
        mHand.postDelayed({this.finish()},200)

    }

    private var welpresenter:WelcomePresenter?=null
    private var rootView: View?=null
    private lateinit var mHand:MyHandler
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_welcome)
        mHand= MyHandler(this)
        rootView=LayoutInflater.from(this).inflate(R.layout.activity_welcome,null)
        welpresenter = WelcomePresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
        val anim = AnimatorInflater.loadAnimator(this, R.animator.anim_appname)
        anim.setTarget(txt_apptitle)
        anim.start()
        if (SharedHelper.getInstance(this).getBoolean(SharedHelper.FIRST_LOAD, true)) {
            txt_count.text="首次加载资源,请稍候..."
            //后台加载，失败则跳过提示重启加载
            welpresenter!!.loadingResources()
        } else {
            initViews()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.repeatCount == 0)
            return false
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        welpresenter=null
        System.gc()
        super.onDestroy()
    }
    class MyHandler(activity: WelcomeActivity): Handler() {
        private val mActivty: WeakReference<WelcomeActivity> by lazy {
            WeakReference<WelcomeActivity>(activity)
        }

    }
}
