package com.mrsgx.campustalk.mvp.Regesiter

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
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.adapter.CTSpinnerAdapter
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Local.DB
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTSchool
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.RegMatchs
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_regesiter.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RegisterActivity : Activity(), RegisterContract.View {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this, target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        this.finish()
    }

    override fun setPresenter(presenter: RegisterContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this, rootView!!).show(msg, level, time)
    }

    override fun Close() {
        this.finish()
    }


    var presenter: RegisterPensenter? = null
    var context: Context? = null
    var rootView: View? = null
    var userSex: String = "0"
    var userSchool: String? = ""
    var userAreaCode: String? = ""
    var mAreaAdapter: SpinnerAdapter? = null
    var mSchoolAdatpter: SpinnerAdapter? = null
    var mAreaList: ArrayList<HashMap<String, String>>? = null
    var mSchoolList: ArrayList<HashMap<String, String>>? = null
    lateinit var mHand:RegisterHandler
    private fun initData() {
        //加载地区到内存
        mAreaList = ArrayList()
        mSchoolList = ArrayList()
        mHand.post {
            val areas = DB.getInstance(context!!).queryArea()
            for (area in areas) {
                val map = HashMap<String, String>()
                map.put("key1", area.Areacode)
                map.put("key2", area.Areaname)
                mAreaList!!.add(map)
            }
            mAreaAdapter = CTSpinnerAdapter(context!!, mAreaList!!)
            ed_area.adapter = mAreaAdapter
            ed_area.onItemSelectedListener = mOnAreaSelected
        }
        //加载学校到内存
    }

    private val mOnAreaSelected = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(p0: AdapterView<*>?, v: View?, p2: Int, p3: Long) {
            if(v==null)
            {
                return
            }
            userAreaCode = v.tag.toString()
            userSchool = ""
            //重新载入数据
            if (mSchoolList != null) {
                mSchoolList!!.clear()
            } else {
                mSchoolList = ArrayList()
            }
            val schools = DB.getInstance(context!!).querySchoolByAreaCode(userAreaCode!!)
            for (school in schools) {
                val map = HashMap<String, String>()
                map.put("key1", school.SCode)
                map.put("key2", school.SName)
                mSchoolList!!.add(map)
            }
            mSchoolAdatpter = CTSpinnerAdapter(context!!, mSchoolList!!)
            ed_school.adapter = mSchoolAdatpter
        }

    }

    override fun initViews() {
        //加载数据库数据
        initData()
        //加载入场动画
        val regBoxAnim = AnimationUtils.loadAnimation(this, R.anim.reg_box_enter)
        reg_box.post {
            reg_box.startAnimation(regBoxAnim)
        }
        //邮件地址检测
        ed_email.setOnFocusChangeListener { view, b ->
            kotlin.run {
                if (!b) {
                    val email = view.ed_email.text.toString()
                    if (email.isNotEmpty() && RegMatchs.MatchEmail(email))
                        presenter!!.CheckEmail(email)
                    else
                        presenter!!.IS_EMAIL_AVILIABLE = false
                }
            }
        }
        //性别单选监听
        radio_sex.setOnCheckedChangeListener { radioGroup, i ->
            kotlin.run {
                synchronized(this) {
                    when (i) {
                        radio_female.id -> {
                            userSex = GlobalVar.SEX_MAN
                        }
                        radio_man.id -> {
                            userSex = GlobalVar.SEX_FEMALE
                        }
                    }
                }
            }
        }
        //联动下拉菜单监听

        ed_school.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(adapter: AdapterView<*>?, v: View?, p2: Int, p3: Long) {
                if (v != null) {
                    if (v.tag != null)
                        userSchool = v.tag.toString()
                }
            }
        }
        //获取验证码事件
        btn_getcode.setOnClickListener {
            //发起邮件请求
            if (!presenter!!.IS_EMAIL_AVILIABLE) {
                showMessage(context!!.getString(R.string.getcode_fail_wrong_email), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            } else {
                presenter!!.SendCode(ed_email.text.toString())
                //改变按钮状态
                btn_getcode.isClickable = false
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
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_regesiter, null)
        //提交注册
        btn_submit.setOnClickListener {
            //获取信息并判断然后提交
            //密码一致性检测
            val pass = ed_pass.text.toString()
            if (pass != ed_pass2.text.toString()) {
                showMessage(getString(R.string.warning_different_password), CTNote.LEVEL_WARNING, CTNote.TIME_SHORT)
            } else if (!presenter!!.IS_EMAIL_AVILIABLE) {
                showMessage(getString(R.string.error_invalid_email), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            } else if (userSchool == null || userSchool!!.isEmpty()) {
                showMessage(getString(R.string.error_invalid_school), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            } else if (ed_code.text.isEmpty()) {
                showMessage(getString(R.string.error_invalid_code), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            } else if (!cb_agree.isChecked) {
                showMessage(getString(R.string.error_invalid_agreement), CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            } else {
                val user = CTUser()
                user.Email = ed_email.text.toString().trim()
                user.Password = ed_pass.text.toString()
                user.Sex = userSex
                val school = CTSchool()
                school.SCode = userSchool!!
                user.School = school
                presenter!!.RegAccount(user, ed_code.text.toString())
            }
        }

    }
    override fun onStop() {
        CTNote.getInstance(this,rootView!!).hide()
        super.onStop()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        setContentView(R.layout.activity_regesiter)
        mHand= RegisterHandler(this)
        initViews()
        context = this
        presenter = RegisterPensenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onDestroy() {
        CTNote.getInstance(this, rootView!!).hide()
        super.onDestroy()
    }

    class RegisterHandler(activity: RegisterActivity):Handler(){
        private val register:WeakReference<RegisterActivity> by lazy{
            WeakReference<RegisterActivity>(activity)
        }

        override fun handleMessage(msg: Message?) {
            val activity=register.get()
            if(activity!=null){
                if (msg!!.what == 1) {
                    val value = msg.obj as Int
                    if (value == 0) {
                        activity.btn_getcode.isClickable = true
                        activity.btn_getcode.text = activity.context!!.getString(R.string.getvalidatecode)
                    } else {
                        activity.btn_getcode.text =String.format(activity.context!!.getString(R.string.resttime),value)
                    }
                }
            }

            super.handleMessage(msg)
        }
    }

}
