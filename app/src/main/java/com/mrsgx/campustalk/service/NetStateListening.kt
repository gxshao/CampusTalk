package com.mrsgx.campustalk.service

import android.content.BroadcastReceiver;
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.utils.Utils
import com.zsoft.signala.ConnectionState
import com.zsoft.signala.transport.StateBase
import java.sql.Connection

/**
 * Created by Shao on 2017/9/20.
 */

 class NetStateListening: BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
       if(intent!!.action==ConnectivityManager.CONNECTIVITY_ACTION){
           NetEventManager.getInstance().broadcastOnNetChanged(Utils.getNetWorkState(p0!!))
       }
        if(intent.action=="campustalk.disconnectSignalR"){
            NetEventManager.getInstance().broadcastOnSignalRChanged(intent.getBooleanExtra(GlobalVar.SIGNAL_STATE,false))
        }
    }
    interface NetEvent{
        fun OnNetChanged(net:Int)
        fun OnSignalRChanged(state: Boolean)
    }
}
