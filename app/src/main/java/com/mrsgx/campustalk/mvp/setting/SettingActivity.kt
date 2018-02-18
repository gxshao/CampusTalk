package com.mrsgx.campustalk.mvp.setting

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar.Companion.CHANGE_SETTING
import com.mrsgx.campustalk.obj.CTSetor
import com.mrsgx.campustalk.utils.SharedHelper
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : Activity() {
    val mSetor=CTSetor()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initViews()
    }

    fun initViews(){
        this.actionBar.setDisplayHomeAsUpEnabled(true)
        this.title=this.getString(R.string.txt_settings)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.actionBar.setBackgroundDrawable(getDrawable(R.drawable.actionbar_head))
        }
        mSetor.naviBar=SharedHelper.getInstance(this).getBoolean(SharedHelper.IS_SHOW_NAVI,true)
        switch_nvai.isChecked= mSetor.naviBar
        switch_nvai.setOnCheckedChangeListener { buttonView, isChecked ->
            mSetor.naviBar=isChecked
        }}
    fun save(){
        val edit=SharedHelper.getInstance(this).edit()
        edit.putBoolean(SharedHelper.IS_SHOW_NAVI, mSetor.naviBar)
        edit.apply()
        val intent=Intent()
        intent.putExtra(SharedHelper.IS_SHOW_NAVI, mSetor.naviBar)
        setResult(CHANGE_SETTING,intent)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                save()
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK->{
                save()
                this.finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        save()
    }
}
