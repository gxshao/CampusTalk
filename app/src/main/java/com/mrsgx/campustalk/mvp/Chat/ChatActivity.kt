package com.mrsgx.campustalk.mvp.Chat

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.app.Activity
import android.content.Context
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.adapter.ChatAdapter
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_chat.*
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import com.mrsgx.campustalk.utils.AndroidBug5497Workaround


class ChatActivity : Activity(), ChatContract.View {
    override fun showMessage(title: String, msg: String, level: Int) {
        CTNote.getInstance(this, mView!!).show(title, msg, level, CTNote.TIME_SHORT)
    }

    private var context: Context? = null
    private var INPUTMODE = true
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initViews() {
        context = this
        val bounce_anim = AnimationUtils.loadAnimation(this, R.anim.matching_bounce)
        bounce_anim.setAnimationListener(mAnim_bounce)
        // txt_matc-hing.startAnimation(bounce_anim)
        val rotate_anim = AnimationUtils.loadAnimation(this, R.anim.btn_more_rotate)
        val mAdapter = ChatAdapter()
        chat_recycler.layoutManager = LinearLayoutManager(this)
        chat_recycler.adapter = mAdapter
        mAdapter.addMyChat("xxxxxdsdadasdsa")
        mAdapter.addOtherChat("233333")
        /**
         * 弹出图片层
         */
        btn_more.setOnClickListener {
            btn_more.startAnimation(rotate_anim)
        }
        /**
         * 语音文字输入切换
         */
        btn_switch_inputmode.setOnClickListener {
            if (INPUTMODE) {
                btn_switch_inputmode.setImageDrawable(this.resources.getDrawable(R.drawable.btn_switch_text_bg))
                btn_audio.visibility = View.VISIBLE
                ed_content.visibility = View.INVISIBLE
            } else {
                btn_switch_inputmode.setImageDrawable(this.resources.getDrawable(R.drawable.btn_switch_voice_bg))
                btn_audio.visibility = View.INVISIBLE
                ed_content.visibility = View.VISIBLE
            }
            INPUTMODE = !INPUTMODE
        }
        /**
         * 发送语音
         */
        btn_audio.setOnLongClickListener {
            btn_audio.text = context!!.resources.getString(R.string.realse_to_send)
            true
        }
        /**
         * 松开
         */
        btn_audio.setOnTouchListener({ view, motionEvent ->
            var isFilter = false
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    btn_audio.text = context!!.resources.getString(R.string.longclick_speak)
                }
            }
            isFilter
        })
        /**
         * 切换按钮
         */
        val tmp_anim = AnimationUtils.loadAnimation(context, R.anim.btn_send_show)
        val moreshow_anim = AnimationUtils.loadAnimation(context, R.anim.btn_more_show)
        val hide_anim = AnimationUtils.loadAnimation(context, R.anim.btn_send_hide)
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
            }else
            {
                chat_recycler.smoothScrollBy(0,
                        chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
            }
        }
        btn_send.setOnClickListener {
            if (!ed_content.text.isEmpty()) {
                mAdapter.addMyChat(ed_content.text.toString())
                chat_recycler.smoothScrollBy(0,
                        chat_recycler.computeVerticalScrollExtent(), AccelerateDecelerateInterpolator())
                ed_content.text.clear()
            }

        }
    }

    override fun Close() {
        //退出匹配 发出空闲状态
        this.finish()
    }

    override fun showMessage(msg: String?) {

    }

    override fun startNewPage(target: Class<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: ChatContract.Prensenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 匹配动画
     */
    private val mAnim_bounce = object : AnimatorListener, Animation.AnimationListener {
        override fun onAnimationEnd(p0: Animator?) {
            val bounce_anim = AnimationUtils.loadAnimation(context, R.anim.matching_bounce)
            bounce_anim.setAnimationListener(this)
            //txt_matching.startAnimation(bounce_anim)
        }

        override fun onAnimationStart(p0: Animator?) {
        }

        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationCancel(p0: Animator?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            val bounce_anim = AnimationUtils.loadAnimation(context, R.anim.matching_bounce)
            bounce_anim.setAnimationListener(this)
            //txt_matching.startAnimation(bounce_anim)
        }

        override fun onAnimationStart(p0: Animation?) {

        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    }
    private var mView: View? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_chat)
        mView = LayoutInflater.from(this).inflate(R.layout.activity_chat, null)
        this.actionBar.hide()
        initViews()
        //发出匹配请求
        // match_mask.visibility = View.INVISIBLE
        loadActionBar()
        this.actionBar.show()
    }

    private fun loadActionBar() {
        var who = "mosren"
        this.actionBar.title = "正在与" + who + "聊天..."
        this.actionBar.setDisplayShowHomeEnabled(false)
        this.actionBar.setDisplayHomeAsUpEnabled(true)
        // this.actionBar.setBackgroundDrawable(this.resources.getDrawable(R.drawable.ctnote_bg_blue))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * actionbar 响应
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_follow -> {
                item.isChecked = !item.isChecked
                item.icon = if (item.isChecked)
                    context!!.resources.getDrawable(R.mipmap.follow)
                else
                    context!!.resources.getDrawable(R.mipmap.unfollow)

            }
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
