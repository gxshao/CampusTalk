package com.mrsgx.campustalk.service;

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.interfaces.ChatInterfaces
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.obj.CTArea
import com.mrsgx.campustalk.obj.CTData
import com.mrsgx.campustalk.obj.CTMessage
import com.mrsgx.campustalk.obj.CTUser
import com.zsoft.signala.Connection
import com.zsoft.signala.ConnectionState
import com.zsoft.signala.SendCallback
import com.zsoft.signala.transport.ITransport
import com.zsoft.signala.transport.StateBase
import com.zsoft.signala.transport.longpolling.LongPollingTransport
import java.util.*

/**
 * Created by Shao on 2017/9/6.
 */
class CTConnection(url: String?, context: Context?, transport: ITransport?) : Connection(url, context, transport) {

    lateinit var mChatListener: ChatInterfaces
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mConn: CTConnection?=null

        fun getInstance(context: Context?): CTConnection {
            if(mConn==null)
                mConn = CTConnection(GlobalVar.SERVER_URL, context, LongPollingTransport())
            return mConn!!
        }
    }

    override fun OnError(exception: Exception?) {
        Stop()
        Start()
        super.OnError(exception)
    }
    override fun OnMessage(message: String?) {
        println(message)
        if(!message.isNullOrEmpty()){
            val aType = object : TypeToken<CTData<Any>>() {}.type
            val msg=Gson().fromJson<CTData<Any>>(message,aType)
            if(msg!=null){
                when(msg.DataType){
                    CTData.DATATYPE_REPLY->{
                        val uid=msg.Body as String
                        mChatListener.onMatched(uid)
                    }
                    CTData.DATATYPE_MESSAGE->{
                        val bType = object : TypeToken<CTData<CTMessage>>() {}.type
                        val d=Gson().fromJson<CTData<CTMessage>>(message,bType)
                        mChatListener.onMessage(d.Body!!)
                    }
                    CTData.DATATYPE_PUSH->{
                        //服务器推送信息

                    }
                    CTData.DATATYPE_CONNECTED->{
                        //连接信息
                        when(msg.Body){
                            "next"->{
                                //单向连接断开，重新加载chat activity
                                mChatListener.onNextMatch()
                            }
                        }
                    }
                }
            }
        }
        super.OnMessage(message)
    }

    override fun OnStateChanged(oldState: StateBase?, newState: StateBase?) {

        if(oldState!!.state!=newState!!.state)
        {

        val intent = Intent()
        intent.action = "campustalk.disconnectSignalR"
            when(newState.state){
                ConnectionState.Connected->{
                    intent.putExtra(GlobalVar.SIGNAL_STATE,true)
                    val data=CTData<CTUser>()
                    data.DataType=CTData.DATATYPE_CONNECTED
                    data.Body=GlobalVar.LOCAL_USER
                    CTConnection.getInstance(context).Send(Gson().toJson(data),object :SendCallback(){
                        override fun OnError(ex: java.lang.Exception?) {

                        }

                        override fun OnSent(messageSent: CharSequence?) {
                            println("用户验证信息已发送:"+messageSent)
                        }
                    })
                }
                ConnectionState.Disconnected ->   intent.putExtra(GlobalVar.SIGNAL_STATE,false)
                ConnectionState.Connecting ->{}
                ConnectionState.Reconnecting -> {}
                ConnectionState.Disconnecting -> {}
            }
        context.sendBroadcast(intent)

        }
        super.OnStateChanged(oldState, newState)
    }


}