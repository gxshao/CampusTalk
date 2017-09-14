package com.mrsgx.campustalk.service;

import android.content.Context
import android.provider.Settings
import com.mrsgx.campustalk.data.GlobalVar
import com.zsoft.signala.Connection
import com.zsoft.signala.transport.ITransport
import com.zsoft.signala.transport.StateBase
import com.zsoft.signala.transport.longpolling.LongPollingTransport

/**
 * Created by Shao on 2017/9/6.
 */
class CTConnection(url: String?, context: Context?, transport: ITransport?) : Connection(url, context, transport) {


    companion object {
        var mConn: CTConnection? = null
        fun getInstance(context: Context?): CTConnection {
            if (mConn == null)
                mConn = CTConnection(GlobalVar.SERVER_URL, context, LongPollingTransport())
            return mConn!!
        }
    }

    override fun OnError(exception: Exception?) {
        super.OnError(exception)
    }

    override fun OnMessage(message: String?) {
        println("收到信息:" + message)
        super.OnMessage(message)
    }

    override fun OnStateChanged(oldState: StateBase?, newState: StateBase?) {
        super.OnStateChanged(oldState, newState)
    }


}