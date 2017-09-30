package com.mrsgx.campustalk.mvp.Main

import android.app.ActivityOptions
import android.support.v4.app.Fragment
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.*
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.adapter.FragAdapter
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.service.CTConnection
import com.mrsgx.campustalk.service.NetStateListening
import com.mrsgx.campustalk.widget.CTNote
import com.mrsgx.campustalk.widget.MainViewPagerTransform
import com.zsoft.signala.SendCallback
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : FragmentActivity(), MainContract.View, NetStateListening.NetEvent, MatchFragment.OnFragmentInteractionListener
        , FollowFragment.OnFragmentInteractionListener, FindFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener {
    override fun showMessage(msg: String, level: Int, time: Int) {
      CTNote.getInstance(this,mView).show(msg,level,time)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

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

    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this, target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    override fun setPresenter(presenter: MainContract.Presenter?) {

    }

    var viewpagerAdapter: FragAdapter? = null
    override fun initViews() {
        //viewpager
        val fm = this.supportFragmentManager
        mView=LayoutInflater.from(this).inflate(R.layout.activity_main,null)
        val mFragments = ArrayList<Fragment>()
        mFragments.add(MatchFragment())
        val find=FindFragment()
        find.rootview=this
        find.parentContext=this
        mFragments.add(find)
        mFragments.add(FollowFragment())
        mFragments.add(SettingFragment())
        viewpagerAdapter = FragAdapter(fm, mFragments)
        viewpager.adapter = viewpagerAdapter
        viewpager.setOnTouchListener { view, motionEvent ->
            kotlin.run {
                if (mNaviState) {
                    btn_img_navi_switch.performClick()
                }
                false
            }
        }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }
        })
        viewpager.setPageTransformer(true, MainViewPagerTransform())
        colorAnimationView.setmViewPager(viewpager, 4, 0xffB1D3EC.toInt(), 0xff6CAAD9.toInt(), 0xff0066B2.toInt(), 0xff285577.toInt())
        radio_navi.setOnCheckedChangeListener(mRadioChanged)
        val mWidth = this.resources.getDimension(R.dimen.radio_width).toInt()
        val icon_match = this.resources.getDrawable(R.mipmap.icon_match)
        icon_match.setBounds(0, 0, mWidth, mWidth)
        val icon_find = this.resources.getDrawable(R.mipmap.icon_find)
        icon_find.setBounds(0, 0, mWidth, mWidth)
        val icon_follow = this.resources.getDrawable(R.mipmap.icon_follow)
        icon_follow.setBounds(0, 0, mWidth, mWidth)
        val icon_my = this.resources.getDrawable(R.mipmap.icon_my)
        icon_my.setBounds(0, 0, mWidth, mWidth)


        radio_match.setCompoundDrawables(null, icon_match, null, null)
        radio_find.setCompoundDrawables(null, icon_find, null, null)
        radio_follow.setCompoundDrawables(null, icon_follow, null, null)
        radio_setting.setCompoundDrawables(null, icon_my, null, null)
    }

    private val mRadioChanged = RadioGroup.OnCheckedChangeListener { p0, who ->
        run {
            when (who) {
                radio_match.id -> {
                    viewpager.setCurrentItem(0, true)
                }
                radio_find.id -> {
                    viewpager.setCurrentItem(1, true)
                }
                radio_follow.id -> {
                    viewpager.setCurrentItem(2, true)
                }
                radio_setting.id -> {
                    viewpager.setCurrentItem(3, true)
                }
            }
        }

    }
    private lateinit var mainpresenter: MainPresenter
    private lateinit var mView:View
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_main)
        initViews()
        NetEventManager.getInstance().subscribe(this) //订阅网络消息
        mainpresenter = MainPresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
        gestureDetector = GestureDetector(this, listener)
        val con = CTConnection.getInstance(this)
        frg_navibar.setOnTouchListener(NaviTouchEvent)
        /**
         * 导航栏事件
         */
        btn_img_navi_switch.setOnClickListener {
            synchronized(this) {
                moveNaviBar(frg_navibar, mNaviState)  //滑动隐藏的导航栏
                mNaviState = !mNaviState
            }
        }



        /**
         * 1.加载用户信息，学生认证校验和资料校验
         * 2.链接通讯服务器  监听网络状态 done
         * 3.加载导航 done
         * 4.子页业务逻辑
         */
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
    }


    override fun onResume() {
        super.onResume()
    }


    //{动画区
    private var gestureDetector: GestureDetector? = null
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

            if (x > 0 && !mNaviState) {
                btn_img_navi_switch.performClick()
            } else if (x < 0 && mNaviState) {
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

    /**
     * 旋转开关动画
     */
    private fun rotateSwitch(btn: ImageView, state: Boolean) {
        val ani: Animation = if (state) {
            AnimationUtils.loadAnimation(this, R.anim.switch_in)

        } else {
            AnimationUtils.loadAnimation(this, R.anim.switch_out)
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
        ani.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
                rotateSwitch(btn_img_navi_switch, mNaviState)
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
        frgbar.layoutParams = parm
        frgbar.startAnimation(ani)
    }
    //动画区}


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
    }
}
