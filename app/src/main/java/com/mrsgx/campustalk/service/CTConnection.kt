package com.mrsgx.campustalk.service;

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.TableRow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.interfaces.ChatInterfaces
import com.mrsgx.campustalk.obj.CTData
import com.mrsgx.campustalk.obj.CTMessage
import com.mrsgx.campustalk.obj.CTPushMessage
import com.mrsgx.campustalk.obj.CTUser
import com.zsoft.signala.Connection
import com.zsoft.signala.ConnectionState
import com.zsoft.signala.SendCallback
import com.zsoft.signala.transport.ITransport
import com.zsoft.signala.transport.StateBase
import com.zsoft.signala.transport.longpolling.LongPollingTransport
import java.lang.ref.WeakReference

/**
 * 聊天服务器通信
 * Created by Shao on 2017/9/6.
 */
class CTConnection(url: String?, context: Context, transport: ITransport?) : Connection(url, context, transport) {

    var mChatListener: ChatInterfaces? = null
    val mContext:Context by lazy {
        context
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mConn: CTConnection? = null

        fun getInstance(context: Context): CTConnection {
            if (mConn == null)
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
        if (!message.isNullOrEmpty()) {
            val aType = object : TypeToken<CTData<Any>>() {}.type
            val msg = Gson().fromJson<CTData<Any>>(message, aType)
            if (msg != null) {
                when (msg.DataType) {
                    CTData.DATATYPE_REPLY -> {
                        val uid = msg.Body as String
                        if (mChatListener != null)
                            mChatListener!!.onMatched(uid)
                    }
                    CTData.DATATYPE_MESSAGE -> {
                        val bType = object : TypeToken<CTData<CTMessage>>() {}.type
                        val d = Gson().fromJson<CTData<CTMessage>>(message, bType)
                        if (mChatListener != null)
                            mChatListener!!.onMessage(d.Body!!)
                    }
                    CTData.DATATYPE_PUSH -> {
                        //服务器推送信息
                        val bType=object :TypeToken<CTData<CTPushMessage>>(){}.type
                        val d=Gson().fromJson<CTData<CTPushMessage>>(message,bType)
                        val intent=Intent("campustalk.receivePushMessage")
                        intent.putExtra(CTPushMessage.PUSH_MSG,d.Body)
                        mContext.sendBroadcast(intent)
                    }
                    CTData.DATATYPE_CONNECTED -> {
                        //连接信息
                        when (msg.Body) {
                            "next" -> {
                                //单向连接断开，重新加载chat activity
                                if (mChatListener != null)
                                    mChatListener!!.onNextMatch()
                            }
                        }
                    }
                }
            }
        }
        super.OnMessage(message)
    }

    private var isReconnected = false
    override fun OnStateChanged(oldState: StateBase?, newState: StateBase?) {

        val reconnection = Runnable {
            if (!isReconnected) {
                Thread.sleep(2000)
                CTConnection.getInstance(context).Start()
            }
        }
        val th = Thread(reconnection)

        if (oldState!!.state != newState!!.state) {

            val intent = Intent()
            intent.action = "campustalk.disconnectSignalR"
            when (newState.state) {
                ConnectionState.Connected -> {
                    isReconnected = true
                    intent.putExtra(GlobalVar.SIGNAL_STATE, true)
                    context.sendBroadcast(intent)
                    val data = CTData<CTUser>()
                    data.DataType = CTData.DATATYPE_CONNECTED
                    data.Body = GlobalVar.LOCAL_USER
                    CTConnection.getInstance(context).Send(Gson().toJson(data), object : SendCallback() {
                        override fun OnError(ex: java.lang.Exception?) {
                        }

                        override fun OnSent(messageSent: CharSequence?) {
                        }
                    })
                }
                ConnectionState.Disconnected -> {
                    intent.putExtra(GlobalVar.SIGNAL_STATE, false)
                    isReconnected = false
                    th.start()
                }
                ConnectionState.Connecting -> {
                    isReconnected=true
                }
                ConnectionState.Reconnecting -> {
                    isReconnected=true
                }
                ConnectionState.Disconnecting -> {

                }
                null -> {
                }
            }

        }
        super.OnStateChanged(oldState, newState)
    }


}