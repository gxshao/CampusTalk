package com.mrsgx.campustalk.utils

import android.content.Context
import android.media.MediaPlayer
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream

/**
 * 音频播放类
 * Created by Shao on 2017/10/10.
 */
class AudioPlayer constructor(val context: Context) {
    private var mMediaPlayer: MediaPlayer? = null

    /**
     * 开始播放
     */
    fun playAudio(path: String) {
//        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
//        } else {

        val file = File(path)
        val fis = FileInputStream(file)
        playBase(fis.fd)
    }
    fun  playAudio(id:Int){
       try {
           synchronized(this){
           val mp=MediaPlayer.create(context,id)
           mp.start()
           mp.setOnCompletionListener {
               mp.stop()
               mp.reset()
               mp.release()
           }
           }
       }catch (e:Exception)
       {
           e.printStackTrace()
       }
    }

    private fun playBase(fd: FileDescriptor) {
        synchronized(this) {
            try {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer()
                }
                if (mMediaPlayer!!.isPlaying || mMediaPlayer!!.isLooping)
                    return
                mMediaPlayer!!.setDataSource(fd)
                mMediaPlayer!!.prepare()
                mMediaPlayer!!.start()
                mMediaPlayer!!.setOnCompletionListener {
                    stopPlayAudio()
                }
            } catch (e: Exception) {

                e.printStackTrace()
            } finally {

            }
        }
    }

    /**
     * 结束播放
     */
    fun stopPlayAudio() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
        }
        mMediaPlayer = null
    }
}