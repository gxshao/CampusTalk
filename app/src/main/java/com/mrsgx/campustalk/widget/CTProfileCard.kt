package com.mrsgx.campustalk.widget

import android.content.Context
import android.graphics.Rect
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.github.snowdream.android.widget.SmartImageView
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.obj.CTUser


/**
 * Created by Shao on 2017/10/9.
 */
class CTProfileCard(context: Context, val rootView: View, widths: Int, heights: Int) : PopupWindow(LayoutInflater.from(context).inflate(R.layout.card_profile, null), widths, heights) {
    private var view: View? = null
    private var txt_nickname: TextView? = null
    private var txt_email: TextView? = null
    private var txt_sex: TextView? = null
    private var txt_age: TextView? = null
    private var txt_schoolname: TextView? = null
    private var txt_userexplain: TextView? = null
    private var pic_headpic:SmartImageView?=null
    init {
        view = LayoutInflater.from(context).inflate(R.layout.card_profile, null)
        this.contentView = view
        txt_nickname = view!!.findViewById(R.id.txt_card_nickname)
        txt_sex = view!!.findViewById(R.id.txt_card_sex)
        txt_age = view!!.findViewById(R.id.txt_card_age)
        txt_schoolname = view!!.findViewById(R.id.txt_card_schoolname)
        txt_userexplain = view!!.findViewById(R.id.txt_card_userexplain)
        txt_email = view!!.findViewById(R.id.txt_card_email)
        pic_headpic=view!!.findViewById(R.id.pic_card_headpic)
        view!!.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
        view!!.layoutParams = ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        isClippingEnabled = false
        this.setBackgroundDrawable(android.graphics.drawable.BitmapDrawable())
        this.isOutsideTouchable = true
        this.isFocusable = true
    }

    fun showUser(user: CTUser) {
        txt_nickname!!.text=user.Nickname
        txt_email!!.text=user.Email
        txt_age!!.text=user.Age
        txt_schoolname!!.text=user.School!!.SName
        txt_userexplain!!.text=user.Userexplain
        txt_sex!!.text=if(user.Sex == GlobalVar.SEX_MAN) "男" else "女"
        pic_headpic!!.setImageUrl(user.Headpic, Rect())
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0)
    }


}