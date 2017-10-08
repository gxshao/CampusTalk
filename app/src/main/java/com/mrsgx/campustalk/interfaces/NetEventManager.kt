package com.mrsgx.campustalk.interfaces

import com.mrsgx.campustalk.service.NetStateListening
import com.zsoft.signala.transport.StateBase

/**
 * Created by Shao on 2017/9/20.
 */
class NetEventManager {
    companion object {
        private  var mNetEvents: NetEventManager?=null
        fun getInstance(): NetEventManager {
            if(mNetEvents==null)
             mNetEvents = NetEventManager()
            return mNetEvents!!
        }
    }

    private  var mList: ArrayList<NetStateListening.NetEvent> = ArrayList()
    /**
     * 订阅网络消息
     */
    fun subscribe(subscriber: NetStateListening.NetEvent) {
        synchronized(this) {
            if (!mList.contains(subscriber)) {
                mList.add(subscriber)
            }
        }
    }

    var Count: Int = 0
    get() {
        return mList.size
    }


    /**
     * 取消订阅
     */
    fun cancelSubscribe(subscriber: NetStateListening.NetEvent) {
        synchronized(this) {
            if (mList.contains(subscriber)) {
                mList.remove(subscriber)
            }
        }
    }

    fun broadcastOnNetChanged(net: Int) {
        synchronized(this) {
            for (event in mList) {
                event.OnNetChanged(net)
            }
        }
    }

    fun broadcastOnSignalRChanged(state: Boolean) {
        synchronized(this) {
            for (event in mList) {
                event.OnSignalRChanged(state)
            }
        }
    }

}