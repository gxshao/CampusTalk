package com.mrsgx.campustalk.utils

import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.net.ConnectivityManager
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.content.ContentUris
import android.content.Context
import com.blankj.utilcode.utils.NetworkUtils.isConnected
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore


/**
 * Created by Shao on 2017/9/20.
 */
class Utils {
    companion object {
        /**
         * 没有连接网络
         */
        val NETWORK_NONE = -1
        /**
         * 移动网络
         */
         val NETWORK_MOBILE = 0
        /**
         * 无线网络
         */
         val NETWORK_WIFI = 1

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

        fun onSelectedImage(data: Intent,context: Context):String? {
            var imagePath: String? = null
            val uri = data.data
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                DocumentsContract.isDocumentUri(context, uri)
            } else {
                TODO("VERSION.SDK_INT < KITKAT")
            }) {
                // 如果是document类型的Uri，则通过document id处理
                val docId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    DocumentsContract.getDocumentId(uri)
                } else {
                    TODO("VERSION.SDK_INT < KITKAT")
                }
                if ("com.android.providers.media.documents" == uri
                        .authority) {
                    val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] // 解析出数字格式的id
                    val selection = MediaStore.Images.Media._ID + "=" + id
                    imagePath = Utils.getImagePath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,context)
                } else if ("com.android.providers.downloads.documents" == uri
                        .authority) {
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)!!)
                    imagePath = Utils.getImagePath(contentUri, null,context)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                // 如果不是document类型的Uri，则使用普通方式处理
                imagePath = Utils.getImagePath(uri, null,context)
            }
             return imagePath

        }

        private fun getImagePath(uri: Uri,  selection:String?,context:Context):String?{
            var path = ""
            // 通过uri和selection来获取真实的图片路径
            val cursor = context.contentResolver.query(uri, null, selection, null,
                    null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                }
                cursor.close()
            }
            return path
        }
    }
}