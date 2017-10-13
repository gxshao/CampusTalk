package com.mrsgx.campustalk.mvp.Main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.GlobalVar.Companion.CHANGE_SETTING
import com.mrsgx.campustalk.data.GlobalVar.Companion.CHOOSE_PHOTO
import com.mrsgx.campustalk.data.Local.DB
import com.mrsgx.campustalk.mvp.Profile.ProfileActivity
import com.mrsgx.campustalk.mvp.Setting.SettingActivity
import com.mrsgx.campustalk.retrofit.Api
import com.mrsgx.campustalk.utils.SharedHelper
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.utils.Utils
import com.mrsgx.campustalk.widget.CTNote
import com.mrsgx.campustalk.widget.TalkerProgressDialog
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.item_follow_list.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var rootview: MainContract.View? = null
    var parentContext: Context? = null
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }


    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pic_stucard.performClick()
            } else {
                rootview!!.showMessage("请重新配置SD卡权限！", CTNote.LEVEL_ERROR, CTNote.TIME_SHORT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CHOOSE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    onStucardChanged(data!!)
                }
            }
            CHANGE_SETTING->{
                println("读取配置")
                if(rootview!=null)
                rootview!!.setNavigator(if(data!!.getBooleanExtra(SharedHelper.IS_SHOW_NAVI,true)) View.VISIBLE else View.INVISIBLE)

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onStucardChanged(data: Intent) {

        var imagepath = Utils.onSelectedImage(data, parentContext!!)
        imagepath=Utils.compressImage(imagepath!!,parentContext!!)
        btn_change_headpic.setImagePath(imagepath, Rect())
        rootview!!.uploadImg(imagepath, GlobalVar.LOCAL_USER!!.Uid)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_logout.setOnClickListener {
            AlertDialog.Builder(parentContext).setTitle("退出").setMessage("注销并退出请点击确定！").setPositiveButton(context.getString(R.string.yes), { dialogInterface, i ->
                TalkerProgressHelper.getInstance(parentContext!!).show("正在注销..")
                val ed = SharedHelper.getInstance(parentContext!!).edit()
                ed.putString(SharedHelper.KEY_EMAIL, "")
                ed.putString(SharedHelper.KEY_PWD, "")
                ed.putBoolean(SharedHelper.FIRST_LOAD, true)
                ed.apply()
                rootview!!.Close()
            }).setNegativeButton("取消", null).show()
        }
        btn_change_pass.setOnClickListener {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val content_url = Uri.parse("http://www.mrsgx.cn")
            intent.data = content_url
            startActivity(intent)
        }
        btn_about.setOnClickListener {
            AlertDialog.Builder(context).setTitle(context.getString(R.string.txt_about)).setMessage("CampusTalk小组作品，欢迎访问www.mrsgx.cn获取更多资讯").setNegativeButton(context.getString(R.string.quit), null).show()
        }
        btn_settings.setOnClickListener {
            startActivityForResult(Intent(context,SettingActivity::class.java), CHANGE_SETTING)
        }
        if (!GlobalVar.LOCAL_USER!!.Headpic.isNullOrEmpty())
            btn_change_headpic.setImageUrl(Api.API_HEADPIC_BASE + GlobalVar.LOCAL_USER!!.Headpic, Rect())
        btn_change_profile.setOnClickListener {
            rootview!!.startNewPage(ProfileActivity::class.java)
        }
        btn_change_headpic.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(kotlin.arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
                return@setOnClickListener
            }
            val imageFile = File(Environment
                    .getExternalStorageDirectory(), "headpic${File.separator}jpg")
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
        txt_settings_nickname.text = GlobalVar.LOCAL_USER!!.Nickname
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): SettingFragment {
            val fragment = SettingFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
