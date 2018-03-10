package com.mrsgx.campustalk.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.obj.CTPushMessage
import com.mrsgx.campustalk.utils.Utils


/**
 * 网络状态的广播接收者
 * Created by Shao on 2017/9/20.
 */

class NetStateListening : BroadcastReceiver() {
    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent!!.action == ConnectivityManager.CONNECTIVITY_ACTION) NetEventManager.getInstance().broadcastOnNetChanged(Utils.getNetWorkState(context!!))
        if (intent.action == "campustalk.disconnectSignalR") {
            NetEventManager.getInstance().broadcastOnSignalRChanged(intent.getBooleanExtra(GlobalVar.SIGNAL_STATE, false))
        }
        if (intent.action == "campustalk.receivePushMessage") {
            //发起notification
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val msg: CTPushMessage = intent.extras.getParcelable(CTPushMessage.PUSH_MSG)
            val mBuilder = NotificationCompat.Builder(context)
            mBuilder.setSmallIcon(R.mipmap.ic_launcher) //设置小图标
            mBuilder.setContentTitle(msg.Title) //设置标题
            mBuilder.setContentText(msg.Body)//设置内容
            GlobalVar.PUSH_MSG=msg.Body
            mBuilder.setContentIntent(pendingIntent)
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mBuilder.setVibrate(longArrayOf(100,200,300,400,500))
            mBuilder.setSound(Uri.parse("android.resource://"+context.packageName +"/"+R.raw.warning))
            mBuilder.setFullScreenIntent(pendingIntent,true)
            mNotificationManager.notify(0, mBuilder.build())
        }
    }

    interface NetEvent {
        fun onNetChanged(net: Int)
        fun onSignalRChanged(state: Boolean)
    }
}
