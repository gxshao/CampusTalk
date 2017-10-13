package com.mrsgx.campustalk.utils

import android.annotation.SuppressLint
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
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.text.SimpleDateFormat
import android.R.attr.path
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.mrsgx.campustalk.R
import android.R.attr.scaleHeight
import android.R.attr.scaleWidth
import android.graphics.Matrix
import android.os.Environment
import com.mrsgx.campustalk.data.GlobalVar
import java.io.*
import java.util.*
import android.content.ContentValues.TAG
import android.util.Log
import com.mrsgx.campustalk.app.App
import java.nio.ByteBuffer
import java.nio.file.Files.exists


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

        fun onSelectedImage(data: Intent, context: Context): String? {
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
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context)
                } else if ("com.android.providers.downloads.documents" == uri
                        .authority) {
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)!!)
                    imagePath = Utils.getImagePath(contentUri, null, context)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                // 如果不是document类型的Uri，则使用普通方式处理
                imagePath = Utils.getImagePath(uri, null, context)
            }
            return imagePath

        }

        private fun getImagePath(uri: Uri, selection: String?, context: Context): String? {
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

        @SuppressLint("SimpleDateFormat")
        fun getFormatDate(): String {
            val date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-ddHH:mm:ss")
            return formatter.format(date)
        }

        /**
         * 获取音频文件的时长
         */
        fun getfileDuration(url: String): Int {
            val mediaPlayer = MediaPlayer()
            try {
                val file = File(url);
                val fis = FileInputStream(file)
                mediaPlayer.setDataSource(fis.fd)
                mediaPlayer.prepare()
            } catch (e: Exception) {
                println(e)
            }

            return mediaPlayer.duration
        }

        /**
         * 删除文件和文件夹
         */
        fun deleteFileorFolder(path: String) {
            val file = File(path)
            if (file.exists()) {
                file.deleteRecursively()
            }
        }

        /**
         * 将文件转成base64 字符串
         * @param path文件路径
         * @return  *
         * @throws Exception
         */

        @Throws(Exception::class)
        fun encodeBase64File(path: String): String {
            val file = File(path)
            var result = ""
            if (file.exists()) {
                val inputFile = FileInputStream(file)
                val buffer = ByteArray(file.length().toInt())
                inputFile.read(buffer)
                inputFile.close()
                result = NativeUtils().encode(buffer)
                println("图片大小："+result.length)
            } else {
                println("图片不存在" + result)
            }
            return result
        }

        /**
         * 将base64字符解码保存文件
         * @param base64Code
         * @param targetPath
         * @throws Exception
         */

        @Throws(Exception::class)
        fun decoderBase64File(base64Code: String, targetPath: String) {
            try {
                val buffer = NativeUtils().decodeBuffer(base64Code)
                val out = FileOutputStream(targetPath)
                out.write(buffer)
                out.close()
                out.flush()
            } catch (e: Exception) {
                println(e)
            }
        }

        /**
         * 缩放图片
         */
        fun compressImage(filepath: String, context: Context): String {
            val newfilepath = "" + Environment.getExternalStorageDirectory() + "/campustalk/" + GlobalVar.LOCAL_USER!!.Uid + "/" + getFormatDate() + ".jpg"
            val folder=File(GlobalVar.LOCAL_USER!!.Uid )
            if(!folder.exists()){
                folder.mkdirs()
            }
            val newOpts = BitmapFactory.Options()
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            val bitmap = BitmapFactory.decodeFile(filepath, newOpts)// 此时返回bm为空
            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            val hh = 800f// 这里设置高度为800f
            val ww = 480f// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            newOpts.inSampleSize = be// 设置缩放比例
            saveBitmap(compressImage(BitmapFactory.decodeFile(filepath, newOpts)), newfilepath)
            return newfilepath
        }

        /**
         * 保存图片
         */
        fun saveBitmap(bm: ByteArray, filepath: String) {
            Log.e(TAG, "保存图片")
            try {
                val file=File(filepath)
                if(file.exists()){
                    file.delete()
                }
                file.createNewFile()
                val buffer = ByteBuffer.wrap(bm)
                val out = FileOutputStream(filepath).channel
                out.write(buffer)
                out.close()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }

        private fun compressImage(image: Bitmap): ByteArray {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 90
            while (baos.toByteArray().size / 1024 > 30 && options >1) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset() // 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
                image.density=image.density/2
                options -= 10// 每次都减少10
            }
            if (!image.isRecycled) {
                image.recycle()
                System.gc()
            }
            val arr=baos.toByteArray()
            baos.reset()
            baos.close()
            baos.flush()
            return arr
        }
    }

}