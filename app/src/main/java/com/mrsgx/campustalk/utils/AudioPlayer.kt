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

    private var mMediaPlayer:MediaPlayer = MediaPlayer()
    private var isLoop=false
    fun setLoop(b:Boolean){
        isLoop=b
    }
    /**
     * 开始播放
     */
    fun playAudio(path: String) {
        val file = File(path)
        val fis = FileInputStream(file)
        playBase(fis.fd)
    }

    fun playAudio(id: Int) {
        try {
            synchronized(this) {
                mMediaPlayer = MediaPlayer.create(context, id)
                mMediaPlayer.setVolume(50f,50f)
                mMediaPlayer.start()
                mMediaPlayer.setOnCompletionListener {
                    if(isLoop)
                        mMediaPlayer.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playBase(fd: FileDescriptor) {
        synchronized(this) {
            try {
                if (mMediaPlayer.isPlaying)
                    return
                mMediaPlayer.setDataSource(fd)
                mMediaPlayer.setVolume(50f,50f)
                mMediaPlayer.prepare()
                mMediaPlayer.start()
                mMediaPlayer.setOnCompletionListener {
                        mMediaPlayer.start()
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
        isLoop=false
        mMediaPlayer.stop()
        mMediaPlayer.reset()
        mMediaPlayer.release()
    }
}