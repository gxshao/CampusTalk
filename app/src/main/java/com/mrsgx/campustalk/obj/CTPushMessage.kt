package com.mrsgx.campustalk.obj

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
/**
 * Created by mrsgx on 2018/3/5.
 */
class CTPushMessage() : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Title)
        dest.writeString(Body)
    }

    override fun describeContents(): Int {
        return 0
    }


    var Title: String = ""
    var Body: String = ""

    constructor(parcel: Parcel) : this() {
        Title = parcel.readString()
        Body = parcel.readString()
    }
    companion object CREATOR : Parcelable.Creator<CTPushMessage> {
            const val PUSH_MSG = "pushMessage"
            override fun createFromParcel(parcel: Parcel): CTPushMessage {
                return CTPushMessage(parcel)
            }

            override fun newArray(size: Int): Array<CTPushMessage?> {
                return arrayOfNulls(size)
            }
        }

    }

