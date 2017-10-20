package com.mrsgx.campustalk.obj

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Shao on 2017/10/13.
 */
class CTLocation() :Parcelable {
    var Uid =""
    var Latitude =""
    var Longitude =""
    var Datetime =""

    constructor(parcel: Parcel) : this() {
        Uid = parcel.readString()
        Latitude = parcel.readString()
        Longitude = parcel.readString()
        Datetime = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Uid)
        parcel.writeString(Latitude)
        parcel.writeString(Longitude)
        parcel.writeString(Datetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CTLocation> {
        override fun createFromParcel(parcel: Parcel): CTLocation {
            return CTLocation(parcel)
        }

        override fun newArray(size: Int): Array<CTLocation?> {
            return arrayOfNulls(size)
        }
    }
}