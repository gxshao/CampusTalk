package com.mrsgx.campustalk.mvp.Chat

import android.Manifest
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.adapter.ChatAdapter
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.GlobalVar.Companion.CHOOSE_PHOTO
import com.mrsgx.campustalk.data.GlobalVar.Companion.TYPEFACE_HUAKANG
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.interfaces.OnAudioRecoredStatusListener
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.utils.AudioRecoredUtils
import com.mrsgx.campustalk.utils.Utils
import com.mrsgx.campustalk.widget.CTNote
import com.mrsgx.campustalk.widget.CTProfileCard
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.lang.ref.WeakReference


class ChatActivity : Activity(), ChatContract.View, OnAudioRecoredStatusListener {


    override fun onRecording(db: Double, time: Long) {
        val mAudioLong = (time / 1000).toInt()
        btn_audio.text = String.format(applicationContext!!.resources.getString(R.string.rest_audio_time), 60 - mAudioLong)
    }

    override fun onStop(audio: String) {
        //语音压缩\转码\发送触发
        chatpresenter!!.sendAudioMsg(Utils.encodeBase64File(audio))
        mAdapter.addMyMsg(audio, ChatAdapter.MSG_TYPE_AUDIO)
        chat_recycler.smoothScrollToPosition(mAdapter.itemCount - 1)
        chat_recycler.smoothScrollBy(0,
                chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())

    }


    override fun setCurrentState(state: Int) {
        synchronized(this)
        {
            mMatchingState = state
        }
    }

    override fun getCurrentState(): Int {
        return mMatchingState
    }

    override fun getPartner(): CTUser {
        return mPartner!!
    }

    override fun reset() {
        this.actionBar.hide()
        frm_mask.visibility = View.VISIBLE
        txt_matching.startAnimation(bounce_anim)
        mAdapter.clearAll()

    }

    private lateinit var mHand: Handler
    private var INPUTMODE = true
    private var mPartner: CTUser? = CTUser()
    private var mView: View? = null
    private var chatpresenter: ChatContract.Prensenter? = null
    private lateinit var mAdapter: ChatAdapter
    private var mMatchingState: Int = 0
    private var mAudioRecorder: AudioRecoredUtils? = null
    private var mChatToolWindow: PopupWindow? = null
    private var mChatLogPath: String = ""
    private val bounce_anim:Animation by lazy{
        AnimationUtils.loadAnimation(applicationContext, R.anim.matching_bounce)
    }
    private val mAudioPlaerListener = View.OnClickListener { view ->
        val tag = view.tag
        if (tag != null) {
            val path = tag as String
            mAudioRecorder!!.playAudio(path)
        }
    }
    private val mHeadPicClickListener = View.OnClickListener { view ->
        var who = 1
        if (view.tag != null) {
            who = view.tag as Int
        }
        if (who == 1) {
            showUserProfile(GlobalVar.LOCAL_USER!!)
        } else {
            showUserProfile(mPartner!!)
        }

    }
    private val mAnim_bounce = object : AnimatorListener, Animation.AnimationListener {
        override fun onAnimationEnd(p0: Animator?) {

            bounce_anim.setAnimationListener(this)
            txt_matching.startAnimation(bounce_anim)
        }

        override fun onAnimationStart(p0: Animator?) {
        }

        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationCancel(p0: Animator?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            bounce_anim.setAnimationListener(this)
            txt_matching.startAnimation(bounce_anim)
        }

        override fun onAnimationStart(p0: Animation?) {

        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    }
    private var mProfileDialog: CTProfileCard? = null

    class ChatHandler(activity: ChatActivity) : Handler() {
        private val mChatHand: WeakReference<ChatActivity> by lazy {
            WeakReference<ChatActivity>(activity)
        }

        override fun handleMessage(msg: Message?) {
            val activity = mChatHand.get()
            if (activity != null && msg != null) {
                when (msg.what) {
                    1 -> {
                        //匹配成功
                        activity.frm_mask.visibility = View.INVISIBLE
                        activity.frm_mask.clearAnimation()
                        activity.loadActionBar()
                    }
                }
            }
            super.handleMessage(msg)
        }
    }

    //显示用户资料卡
    private fun showUserProfile(user: CTUser) {
        mProfileDialog!!.showUser(user)
        setBackgroundAlpha(0.5f)
    }


    /**
     * 权限请求结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pic_stucard.performClick()
            } else {
                this.showMessage("请重新配置SD卡权限！", CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            }
        }
    }

    //图片选择结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CHOOSE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    var imagepath = Utils.onSelectedImage(data!!, this)
                    val srcfile = File(imagepath)
                    if (srcfile.length() / 8 / 1024 > 300) {
                        Toast.makeText(this, "您选择的图片太大", Toast.LENGTH_SHORT).show()
                        return
                    }
                    imagepath = Utils.compressImage(imagepath!!, this)
                    //压缩图片到新的路径
                    mHand.postDelayed({
                        println("新的图片地址" + imagepath)
                        mAdapter.addMyMsg(imagepath!!, ChatAdapter.MSG_TYPE_IMGAE)
                        chatpresenter!!.sendImageMsg(Utils.encodeBase64File(imagepath!!))

                        //linshidaima
//                        val x=mChatLogPath+Utils.getFormatDate()+".jpg"
//                        val basestr=Utils.encodeBase64File(imagepath!!)
//                        Utils.decoderBase64File(basestr,x)
//                       mAdapter.addOtherMsg(x,ChatAdapter.MSG_TYPE_IMGAE)
                        chat_recycler.smoothScrollToPosition(mAdapter.itemCount - 1)
                        chat_recycler.smoothScrollBy(0, chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
                    }, 1000)


                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this, mView!!).show(msg, level, time)
    }

    //连接成功
    override fun setPartner(p: CTUser) {
        mPartner = p
        mAdapter.Partner = p
        //待设置显示关注
        val msg = mHand.obtainMessage()
        msg.what = 1
        mHand.sendMessage(msg)
    }

    //收到消息
    override fun onReceiveMsg(msg: String) {
        mAdapter.addOtherChat(msg)
        chat_recycler.smoothScrollToPosition(mAdapter.itemCount - 1)
        chat_recycler.smoothScrollBy(0,
                chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
    }

    //收到语音
    override fun onReceiveAudio(path: String) {
        mAdapter.addOtherMsg(path, ChatAdapter.MSG_TYPE_AUDIO)
        chat_recycler.smoothScrollToPosition(mAdapter.itemCount - 1)
        chat_recycler.smoothScrollBy(0,
                chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
    }

    //收到图片
    override fun onReceiveImage(path: String) {
        mAdapter.addOtherMsg(path, ChatAdapter.MSG_TYPE_IMGAE)
        chat_recycler.smoothScrollToPosition(mAdapter.itemCount - 1)
        chat_recycler.smoothScrollBy(0,
                chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
    }

    override fun startNewPage(target: Class<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: ChatContract.Prensenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //获取缓存文件路径
    override fun getChatFolder(): String {
        return mChatLogPath
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_chat)
        mView = LayoutInflater.from(this).inflate(R.layout.activity_chat, null)
        mHand = ChatHandler(this)
        chatpresenter = ChatPrensenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)

        mChatLogPath = "" + Environment.getExternalStorageDirectory() + GlobalVar.LOCAL_DIRECTORY + GlobalVar.LOCAL_USER!!.Uid + "/" //当前用户音频缓存目录
         mAudioRecorder = AudioRecoredUtils(mChatLogPath)
         mAudioRecorder!!.audioStatusListener = this
        this.actionBar.hide()
        initViews()
        //发出匹配请求
        chatpresenter!!.startMatch()

        //loadActionBar()
    }

    //导入ActionBar
    private fun loadActionBar() {
        mMatchingState = 2
        this.actionBar.setDisplayShowHomeEnabled(false)
        this.actionBar.setDisplayHomeAsUpEnabled(true)
        this.actionBar.setBackgroundDrawable(this.resources.getDrawable(R.drawable.actionbar_head))
        this.actionBar.title = "正在与" + mPartner!!.Nickname + "聊天..."
        mProfileDialog = CTProfileCard(this, mView!!, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        mProfileDialog!!.setOnDismissListener {
            setBackgroundAlpha(1f)
        }
        this.actionBar.show()
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (this).window.attributes
        lp.alpha = bgAlpha
        (this).window.attributes = lp
    }

    override fun onStop() {
        CTNote.getInstance(this, mView!!).hide()
        super.onStop()
    }

    private lateinit var mMenu: Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        mMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * actionbar 响应
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_follow -> {
                synchronized(this) {
                    if (!item.isChecked) {
                        chatpresenter!!.followPartner(mPartner!!.Uid)
                    } else {
                        chatpresenter!!.unfollowPartner(mPartner!!.Uid)
                    }
                    item.isChecked = !item.isChecked
                }
            }
            android.R.id.home -> {
                //询问是否退出匹配
                AlertDialog.Builder(this).setTitle(getString(R.string.tips)).setMessage(getString(R.string.tips_quit_or_not)).setPositiveButton(getString(R.string.yes), { d, i ->
                    this.Close()
                }).setNegativeButton(getString(R.string.no), null).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun setFollowState(state: Boolean) {
        val item = mMenu.findItem(R.id.menu_follow)
        item.icon = if (state)
            applicationContext!!.resources.getDrawable(R.mipmap.follow)
        else
            applicationContext!!.resources.getDrawable(R.mipmap.unfollow)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (frm_mask.visibility == View.VISIBLE)
                    AlertDialog.Builder(this).setTitle(getString(R.string.tips)).setMessage(getString(R.string.tips_quit_or_not_match)).setPositiveButton(getString(R.string.yes), { d, i ->
                        this.Close()
                    }).setNegativeButton(getString(R.string.no), null).show()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun initViews() {
        mChatToolWindow = PopupWindow(this)
        val chattoolview = LayoutInflater.from(this).inflate(R.layout.chat_tools_layout, null)
        mChatToolWindow!!.contentView = chattoolview
        mChatToolWindow!!.isOutsideTouchable = true
        mChatToolWindow!!.isFocusable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mChatToolWindow!!.setBackgroundDrawable(getDrawable(R.drawable.bg_transparent))
        }
        mChatToolWindow!!.background.alpha = 0
        val btn_pic_select = chattoolview.findViewById<View>(R.id.btn_image_select)
        val btn_emoji_select = chattoolview.findViewById<View>(R.id.btn_emoji_select)
        btn_pic_select.setOnClickListener {
            if (mChatToolWindow!!.isShowing) {
                mChatToolWindow!!.dismiss()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(kotlin.arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
                return@setOnClickListener
            }
            val imageFile = File(mChatLogPath, "chatpic${File.separator}jpg")
            if (imageFile.exists()) {
                imageFile.delete()
            }
            try {
                imageFile.createNewFile()
            } catch (e: Exception) {
                println(e)
            }

            //转换成Uri
            val imageUri = Uri.fromFile(imageFile)
            //开启选择呢绒界面
            val intent = Intent("android.intent.action.GET_CONTENT")
            //设置可以缩放
            intent.putExtra("scale", true)
            //设置可以裁剪
            intent.putExtra("crop", true)
            intent.type = "image/*"
            //设置输出位置
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            //开始选择
            startActivityForResult(intent, CHOOSE_PHOTO)
        }
        btn_emoji_select.setOnClickListener {
            if (mChatToolWindow!!.isShowing) {
                mChatToolWindow!!.dismiss()
            }
            Toast.makeText(this, "别急铁子，表情功能马上上线", Toast.LENGTH_SHORT).show()
        }
        bounce_anim.setAnimationListener(mAnim_bounce)
        txt_matching.typeface = TYPEFACE_HUAKANG
        txt_matching.startAnimation(bounce_anim)
        val rotate_anim = AnimationUtils.loadAnimation(this, R.anim.btn_more_rotate)
        mAdapter = ChatAdapter()
        mAdapter.OnAudioPlayerListener = mAudioPlaerListener
        mAdapter.OnHeadPicClickListener = mHeadPicClickListener
        chat_recycler.layoutManager = LinearLayoutManager(this)
        chat_recycler.adapter = mAdapter
        /**
         * 弹出图片层
         */
        btn_more.setOnClickListener {
            btn_more.startAnimation(rotate_anim)
            val location = IntArray(2)
            btn_more.getLocationOnScreen(location)
            //在控件上方显示
            mChatToolWindow!!.showAtLocation(btn_more, Gravity.NO_GRAVITY, (location[0] + btn_more.width / 2) - mChatToolWindow!!.width / 2, location[1] - btn_more.measuredHeight - 100)
        }
        /**
         * 语音文字输入切换
         */
        val bg_text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getDrawable(R.drawable.btn_switch_text_bg)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        val bg_voice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getDrawable(R.drawable.btn_switch_voice_bg)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        btn_switch_inputmode.setOnClickListener {
            if (INPUTMODE) {
                btn_switch_inputmode.setImageDrawable(bg_text)
                btn_audio.visibility = View.VISIBLE
                ed_content.visibility = View.INVISIBLE
            } else {
                btn_switch_inputmode.setImageDrawable(bg_voice)
                btn_audio.visibility = View.INVISIBLE
                ed_content.visibility = View.VISIBLE
            }
            INPUTMODE = !INPUTMODE
        }
        /**
         * 发送语音
         */
        btn_audio.setOnLongClickListener {
            btn_audio.text = applicationContext!!.resources.getString(R.string.realse_to_send)
            mAudioRecorder!!.startRecord()
            true
        }
        /**
         * 松开
         */
        btn_audio.setOnTouchListener({ view, motionEvent ->
            var isFilter = false
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    if (mAudioRecorder!!.stopRecord() <= 0) {
                        showMessage("录音时间太短")
                    } else {
                        btn_audio.text = applicationContext!!.resources.getString(R.string.longclick_speak)
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    mAudioRecorder!!.cancelRecord()
                }
            }
            isFilter
        })
        /**
         * 切换按钮
         */
        val tmp_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.btn_send_show)
        val moreshow_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.btn_more_show)
        val hide_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.btn_send_hide)
        /**
         * 编辑文本
         */
        ed_content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {
                if (edit != null) {
                    if (edit.isEmpty()) {
                        btn_send.startAnimation(hide_anim)
                        btn_send.visibility = View.INVISIBLE
                        btn_more.startAnimation(moreshow_anim)
                        btn_more.visibility = View.VISIBLE

                    } else if (btn_send.visibility == View.INVISIBLE) {
                        btn_more.visibility = View.INVISIBLE
                        btn_send.visibility = View.VISIBLE
                        btn_send.startAnimation(tmp_anim)

                    }
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        ed_content.setOnFocusChangeListener { view, b ->
            if (!b) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } else {
                chat_recycler.smoothScrollBy(0,
                        chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
            }
        }
        btn_send.setOnClickListener {
            if (!ed_content.text.isEmpty()) {
                mAdapter.addMyChat(ed_content.text.toString())
                chat_recycler.smoothScrollToPosition(mAdapter.itemCount - 1)
                chat_recycler.smoothScrollBy(0,
                        chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
                chatpresenter!!.sendTextMsg(ed_content.text.toString())
                ed_content.text.clear()
            }

        }
    }

    override fun Close() {
        //退出匹配 发出空闲状态
        if (chatpresenter != null) {
            println("当前状态" + mMatchingState)
            when (mMatchingState) {
                0 -> {//空闲

                }
                1 -> {
                    //匹配中
                    chatpresenter!!.stopMatch()
                    mMatchingState = 0

                }
                2 -> {
                    //聊天中
                    chatpresenter!!.stopMatch()
                    mMatchingState = 0
                }

            }
            chatpresenter!!.unregsiter()
        }
        if (mChatToolWindow != null) {
            mChatToolWindow!!.dismiss()
            mChatToolWindow!!.contentView=null
        }
        if (mProfileDialog != null) {
            mProfileDialog!!.dismiss()
        }
        mAdapter.clearAll()
        mChatToolWindow = null
        mProfileDialog = null

        mPartner = null
        Utils.deleteFileorFolder(mChatLogPath)
        chat_recycler.removeAllViews()
        this.finish()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onDestroy() {
        if (chatpresenter != null) {
            chatpresenter!!.unregsiter()
        }
        chatpresenter = null
        mView = null
        if (mAudioRecorder != null) {
            mAudioRecorder!!.stopPlayAudio()
            mAudioRecorder!!.stopRecord()
        }
        mAudioRecorder = null
        System.gc()
        super.onDestroy()
    }
}
