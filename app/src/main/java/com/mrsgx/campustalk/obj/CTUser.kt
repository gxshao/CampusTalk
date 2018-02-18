package com.mrsgx.campustalk.obj

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Shao on 2017/9/8.
 */
 class CTUser() : Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
      return 0
    }

    var Sex: String = ""  //性别
    var Uid: String = ""  //UUID+0 || +1
    var School: CTSchool? = CTSchool()//学校
    var Age:String?="0"
    var Email:String=""
    var Nickname:String?=null
    var Headpic:String?=null
    var Userexplain:String?=null
    var State:String?=null
    var Password:String?=null
    var Stucard:String?=null

   constructor(parcel: Parcel) : this() {
      Sex = parcel.readString()
      Uid = parcel.readString()
      Age = parcel.readString()
      Email = parcel.readString()
      Nickname = parcel.readString()
      Headpic = parcel.readString()
      Userexplain = parcel.readString()
      State = parcel.readString()
      Password = parcel.readString()
      Stucard = parcel.readString()
   }

   companion  object CREATOR : Parcelable.Creator<CTUser> {
      override fun createFromParcel(parcel: Parcel): CTUser {
         return CTUser(parcel)
      }

      override fun newArray(size: Int): Array<CTUser?> {
         return arrayOfNulls(size)
      }
   }
}

