package com.mrsgx.campustalk.mvp.Main

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.service.CTConnection
import com.mrsgx.campustalk.service.NetStateListening
import com.zsoft.signala.SendCallback
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : Activity(), MainContract.View, NetStateListening.NetEvent{

    private var mNaviState = false
    override fun OnNetChanged(net: Int) {
        when (net) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
            }
        }
    }

    override fun OnSignalRChanged(state: Boolean) {
        if (state) {
        } else {
            // CTConnection.getInstance(this).Start()
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

    override fun setPresenter(presenter: MainContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mainpresenter: MainPresenter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_main)

        NetEventManager.getInstance().subscribe(this) //订阅网络消息
        mainpresenter = MainPresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
        gestureDetector= GestureDetector(this,listener)
        val con = CTConnection.getInstance(this)
        con.Start()
        btn_start.setOnClickListener {
            object : Thread() {
                override fun run() {
                    con.Send("xxx", object : SendCallback() {
                        override fun OnError(ex: Exception?) {
                            println("error:" + ex!!.message)
                        }

                        override fun OnSent(messageSent: CharSequence?) {

                        }
                    })
                    super.run()
                }
            }.start()
        }

        /**
         * 导航栏事件
         */
        btn_img_navi_switch.setOnClickListener {
            synchronized(this) {
                rotateSwitch(btn_img_navi_switch, mNaviState)
                moveNaviBar(frg_navibar, mNaviState)
                mNaviState = !mNaviState
            }
        }
        frg_navibar.setOnTouchListener(NaviTouchEvent)
        /**
         * 1.加载用户信息，学生认证校验和资料校验
         * 2.链接通讯服务器  监听网络状态
         * 3.加载导航
         * 4.子页业务逻辑
         */
    }

    //{动画区
   private var gestureDetector: GestureDetector?=null
    private val listener = object : GestureDetector.OnGestureListener {
        override fun onLongPress(p0: MotionEvent?) {

        }

        override fun onShowPress(p0: MotionEvent?) {

        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            return true
        }

        override fun onDown(p0: MotionEvent?): Boolean {
           return true
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return true
        }
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val x = e2!!.x - e1!!.x
            val y = e2.y - e1.y

            if (x > 0&&!mNaviState) {
                btn_img_navi_switch.performClick()
            } else if (x < 0&&mNaviState) {
                btn_img_navi_switch.performClick()
            }
            return true
        }
    }
    private val NaviTouchEvent = View.OnTouchListener { view, motionEvent ->
        kotlin.run {
            gestureDetector!!.onTouchEvent(motionEvent)
            true
        }
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mNaviState)
            btn_img_navi_switch.performClick()
        return super.onTouchEvent(event)
    }
    /**
     * 旋转开关动画
     */
    private fun rotateSwitch(btn: ImageView, state: Boolean) {
        val ani: Animation = if (state) {
            AnimationUtils.loadAnimation(this, R.anim.switch_out)
        } else {
            AnimationUtils.loadAnimation(this, R.anim.switch_in)
        }
        ani.fillAfter = true
        btn.startAnimation(ani)
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun moveNaviBar(frgbar: FrameLayout, state: Boolean) {
        val ani: TranslateAnimation = if (state) {
            AnimationUtils.loadAnimation(this, R.anim.navi_out) as TranslateAnimation
        } else
            AnimationUtils.loadAnimation(this, R.anim.navi_in) as TranslateAnimation
        ani.fillAfter = true

        val parm = frgbar.layoutParams as RelativeLayout.LayoutParams
        if (state) {
            parm.marginStart = this.resources.getDimension(R.dimen.hide_navibar_width).toInt()
            radio_navi.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.navi_sub_hide)
            radio_navi.startLayoutAnimation()
        } else {
            parm.marginStart = 0
            radio_navi.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.navi_sub_show)
            radio_navi.startLayoutAnimation()
        }
        frgbar.layoutParams = parm
            frgbar.startAnimation(ani)
    }
    //动画区}
    override fun onResume() {
        super.onResume()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }

        fun get() {

        }
    }
}
