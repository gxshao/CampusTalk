package com.mrsgx.campustalk.obj

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@SuppressLint("ParcelCreator")
/**
 * 数据格式
 * Created by Shao on 2017/9/8.
 */
class CTData<T>() {
    var DataType: String? = ""
    var Body: T? = null

    companion object {
        const val DATATYPE_CONNECTED = "0"//链接信息
        const val DATATYPE_MESSAGE = "1" //普通消息
        const val DATATYPE_REPLY = "2"//服务器响应消息
        const val DATATYPE_PUSH = "3"//服务器推送消息
    }
//    constructor(parcel: Parcel) : this() {
//        DataType = parcel.readString()
//    }
//
//    companion object CREATOR : Parcelable.Creator<CTData<Any>> {
//
//            override fun createFromParcel(parcel: Parcel):CTData<Any> {
//                return CTData(parcel)
//            }
//
//            override fun newArray(size: Int): Array<CTData<Any>?> {
//                return arrayOfNulls(size)
//            }
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(DataType)
//        parcel.writeValue(Body)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }


}