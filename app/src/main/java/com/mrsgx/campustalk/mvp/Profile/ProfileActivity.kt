package com.mrsgx.campustalk.mvp.Profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.view.*
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.retrofit.Api
import com.mrsgx.campustalk.service.NetStateListening
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.utils.Utils
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File


class ProfileActivity : Activity(),ProfileContract.View,NetStateListening.NetEvent {
    override fun OnNetChanged(net: Int) {


    }

    override fun OnSignalRChanged(state: Boolean) {

    }

    override fun onStucardUpload(path: String) {
        if(user!= null)
        {
            user.Stucard =path
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this,rootView!!).show(msg,level,time)
    }
    private val CHOOSE_PHOTO=1
    private var imagepath:String?=""
    private var context: Context?=null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initViews() {
        this.actionBar.title=getString(R.string.title_complete_profile)
        this.actionBar.setBackgroundDrawable(getDrawable(R.drawable.actionbar_head))
        this.actionBar.setDisplayHomeAsUpEnabled(true)
        mAlertDialog= AlertDialog.Builder(this)
                .setCancelable(false)
        .setTitle("是否保存更改?")
        .setMessage("点击确定提交更改")
        .setPositiveButton("确定",{p,i->
            if(ed_nickname.text.isNullOrEmpty())
            {
                showMessage("昵称不能为空！",CTNote.LEVEL_ERROR,CTNote.TIME_SHORT)
                return@setPositiveButton
            }
            TalkerProgressHelper.getInstance(context!!).show(getString(R.string.upload_data))
            user!!.Nickname=ed_nickname.text.toString().trim()
            user.Age=ed_ages.text.toString().trim()
            user.Userexplain=ed_userexplain.text.toString()
            if(!imagepath.isNullOrEmpty()&&user.Stucard !=imagepath)
            {
               //需要上传
                profilepresenter.uplpadstucard(imagepath!!,user.Uid!!)
            }
            profilepresenter.submitProfile(user)
        })
        .setNegativeButton("取消",{p,i->
             Close()
        }).create()
        if(user!=null){
            //初始化控件
            ed_email_profile.setText(user.Email)
            ed_school_name.setText(user.School!!.SName)
            ed_sex.setText(if(user.Sex==GlobalVar.SEX_MAN) this.getString(R.string.man) else this.getString(R.string.female))
            ed_nickname.setText(if(!user.Nickname.isNullOrEmpty()) user.Nickname else null)
            ed_ages.setText(if(!user.Age.isNullOrEmpty()) user.Age else null)
            imagepath=user.Stucard
            if(!user.Stucard.isNullOrEmpty())
            {
                pic_stucard.setImageUrl(Api.API_STUCARD_BASE+user.Stucard,Rect())
            }
            else
            {
                pic_stucard.setImageDrawable(this.getDrawable(R.mipmap.headpic))
            }
            ed_userexplain.setText(if(!user.Userexplain.isNullOrEmpty()) user.Userexplain else null)

        }else
        {
            showMessage("登录信息校验失败,准备强制退出",CTNote.LEVEL_ERROR,CTNote.TIME_SHORT)
        }
        pic_stucard.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(kotlin.arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
                return@setOnClickListener
            }
            val imageFile = File(Environment
                    .getExternalStorageDirectory(), "stucard${File.separator}jpg")
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
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults!!.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              pic_stucard.performClick()
            } else {
                showMessage("请重新配置SD卡权限！",CTNote.LEVEL_ERROR,CTNote.TIME_SHORT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            CHOOSE_PHOTO->{
                if(resultCode== Activity.RESULT_OK)
                {
                    onStucardChanged(data!!)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onStucardChanged(data: Intent) {
         imagepath=Utils.onSelectedImage(data,this)
        if(user!=null){
           pic_stucard.setImagePath(imagepath, Rect())
        }
    }

    override fun Close() {
        CTNote.getInstance(this,rootView!!).hide()
        TalkerProgressHelper.getInstance(this).hideDialog()
        this.finish()
   }
    private lateinit var mAlertDialog:AlertDialog
    private var rootView: View?=null
    private val user=GlobalVar.LOCAL_USER
    lateinit var profilepresenter:ProfilePresenter
    override fun showMessage(msg: String?) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun startNewPage(target: Class<*>?) {
    }

    override fun setPresenter(presenter: ProfileContract.Presenter?) {
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_profile)
        context=this
        NetEventManager.getInstance().subscribe(this)
        rootView=LayoutInflater.from(this).inflate(R.layout.activity_profile,null)
        profilepresenter= ProfilePresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()),this)
        initViews()

    }

    override fun onDestroy() {
        NetEventManager.getInstance().cancelSubscribe(this)
        super.onDestroy()

    }
     override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                   mAlertDialog.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK->{
                mAlertDialog.show()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
