package com.mrsgx.campustalk.mvp.Setting

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MenuItem
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.GlobalVar.Companion.CHANGE_SETTING
import com.mrsgx.campustalk.utils.SharedHelper
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : Activity() {

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
        switch_nvai.isChecked=SharedHelper.getInstance(this).getBoolean(SharedHelper.IS_SHOW_NAVI,true)
    }
    fun save(){
        val edit=SharedHelper.getInstance(this).edit()
        edit.putBoolean(SharedHelper.IS_SHOW_NAVI,switch_nvai.isChecked)
        edit.apply()
        val intent=Intent()
        intent.putExtra(SharedHelper.IS_SHOW_NAVI,switch_nvai.isChecked)
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
    }
}
