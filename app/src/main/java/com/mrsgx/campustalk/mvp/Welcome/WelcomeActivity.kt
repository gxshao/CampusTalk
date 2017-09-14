package com.mrsgx.campustalk.mvp.Welcome

import android.app.Activity
import android.os.Bundle
import com.google.gson.Gson
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.obj.CTData
import com.mrsgx.campustalk.obj.CTSchool
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.service.CTConnection
import com.zsoft.signala.SendCallback
import com.zsoft.signala.transport.longpolling.LongPollingTransport
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : Activity() {
    var url: String = "http://192.168.123.1:13614/MyConnection"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        CTConnection.getInstance(this)
        var con = CTConnection.getInstance(this)
        send.setOnClickListener {
            object : Thread() {
                override fun run() {
                    var json = Gson()
                    var school = CTSchool()
                    school.sCode = "111"
                    school.sName = "123456"


                    var user = CTUser()
                    user.school = school
                    user.sex = "0"
                    user.uid = "1230"


                    var text = CTData<CTUser>()
                    text.DataType = "0"
                    text.Body = user
                    var t = json.toJson(text).toString()
                    println(t)
                    var da=json.fromJson(t, CTData::class.java)
                    println(da.Body)

                    con.Send(json.toJson(text).toString().trim(), object : SendCallback() {
                        override fun OnError(ex: Exception?) {
                        }

                        override fun OnSent(messageSent: CharSequence?) {

                        }
                    })
                    super.run()
                }
            }.start()
        }
        disconnect.setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                       con.Stop()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    super.run()
                }
            }.start()
        }
        connect.setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                        con.Start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    super.run()
                }
            }.start()
        }

    }
}
