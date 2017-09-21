package com.mrsgx.campustalk.utils

import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.net.ConnectivityManager
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.content.Context
import com.blankj.utilcode.utils.NetworkUtils.isConnected
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE



/**
 * Created by Shao on 2017/9/20.
 */
class Utils {
    companion object {
        /**
         * 没有连接网络
         */
        private val NETWORK_NONE = -1
        /**
         * 移动网络
         */
        private val NETWORK_MOBILE = 0
        /**
         * 无线网络
         */
        private val NETWORK_WIFI = 1

        fun getNetWorkState(context: Context): Int {
            // 得到连接管理器对象
            val connectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetworkInfo = connectivityManager
                    .activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {

                if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_WIFI
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    return NETWORK_MOBILE
                }
            } else {
                return NETWORK_NONE
            }
            return NETWORK_NONE
        }
    }
}