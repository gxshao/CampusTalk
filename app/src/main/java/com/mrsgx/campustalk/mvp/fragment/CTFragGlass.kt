package com.mrsgx.campustalk.mvp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrsgx.campustalk.R
import com.mrsgx.ctblur.CTBlur
import com.mrsgx.ctblur.CTBlurData
import com.mrsgx.ctblur.CTBlurUtils
import com.mrsgx.ctblur.ContextWrapper
import kotlinx.android.synthetic.main.fragment_glass.view.*

@SuppressLint("ValidFragment")
/**
 * 毛玻璃效果层
 * Created by mrsgx on 2018/2/27.
 */
class CTFragGlass(val rootview:View) : Fragment(),ILiveBlur {
    override fun onRefreshBlur() {
        bluror.updateBlurView()
    }

    override fun restorBlurState() {
       bluror.restoreBlurView()
        curView.isClickable=false
    }

    private lateinit var bluror: CTBlur
    //回调事件
    override fun onBluring() {
       bluror.updateBlurView(2)
        curView.isClickable=true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
    lateinit var curView:View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       curView= inflater.inflate(R.layout.fragment_glass,null)
        //1.创建配置变量
        //2.创建线程渲染器
        val mblurdata= CTBlurData()
        mblurdata.rootView=rootview
        mblurdata.blurRadius=0
        mblurdata.contextWrapper= ContextWrapper(activity!!)
        val arr=ArrayList<View>()
        arr.add(curView.front_glass)
        mblurdata.viewsToBlurOnto=arr
        mblurdata.blurAlgorithm= CTBlurUtils.getIBlurAlgorithm(CTBlurUtils.ALGORITHM_GAUSSAINFASTBLUR,mblurdata.contextWrapper!!)
        bluror=CTBlur(mblurdata)
        CTBlur.BLUR_ROUNDS_PER_UPDATE=3
        return curView
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
interface ILiveBlur{
    fun onBluring()
    fun onRefreshBlur()
    fun restorBlurState()
}