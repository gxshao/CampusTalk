package com.mrsgx.campustalk.mvp.Find

import com.mrsgx.campustalk.mvp.BasePresenter
import com.mrsgx.campustalk.mvp.BaseView
import com.mrsgx.campustalk.obj.CTLocation
import com.mrsgx.campustalk.obj.CTUser

/**
 * Created by Shao on 2017/9/25.
 */
class FindContract  {
    interface  View:BaseView<Prensenter>{
        fun showMarkers(marks:ArrayList<CTLocation>)
        fun showUserList(users:ArrayList<HashMap<String,String>>)
        fun showMessage(msg: String, level: Int, time: Int)
        fun setItemEnable(pos:Int)
    }
    interface Prensenter:BasePresenter{
        fun getLocationList(times:String)
        fun getUserListByLoc(loc:CTLocation)
        fun followPartner(uid: String,pos:Int)
    }
}