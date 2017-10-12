package com.mrsgx.campustalk.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.util.Log
import com.mrsgx.campustalk.interfaces.OnAudioRecoredStatusListener
import java.io.File
import java.io.FileInputStream
import java.lang.reflect.Executable

/**
 * Created by Shao on 2017/10/10.
 */
class AudioRecoredUtils(val folderPath: String) {
    private var mMediaRecorder: MediaRecorder? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mAudioFile: String = ""
    private val MAX_LENGTH = 1000 * 60
    var startTime: Long = 0
    var endTime: Long = 0
    var audioStatusListener: OnAudioRecoredStatusListener? = null

    init {
        val file = File(folderPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**
     * 开始录音
     */
    fun startRecord() {
        if (mMediaRecorder == null) {
            mMediaRecorder = MediaRecorder()
        }
        try {
            mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
            mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            mAudioFile = folderPath + Utils.getFormatDate() + ".amr"
            mMediaRecorder!!.setOutputFile(mAudioFile)
            mMediaRecorder!!.setMaxDuration(MAX_LENGTH)
            mMediaRecorder!!.prepare()
            mMediaRecorder!!.start()
            startTime = System.currentTimeMillis()
            updateState()
        } catch (e: Exception) {
            println("录音" + e)
        }
    }

    /**
     * 停止录音
     */
    fun stopRecord(): Long {
        if (mMediaRecorder == null) {
            return 0L
        }
        endTime = System.currentTimeMillis()

        try {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            if (audioStatusListener != null&&((endTime-startTime)/1000)>0) {
                audioStatusListener!!.onStop(mAudioFile)
            }
            mAudioFile = ""
        } catch (e: Exception) {
            Log.e("结束录音", e.message)
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null

            val file = File(mAudioFile)
            if (file.exists())
                file.delete()
            mAudioFile = ""
        }
        return endTime - startTime
    }

    fun cancelRecord() {
        try {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
        } catch (e: Exception) {
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
        }

        val file = File(mAudioFile)
        if (file.exists())
            file.delete()
        mAudioFile = ""
    }

    val mHand = Handler()
    val mAudioThreadStateUpdate = Runnable {
        updateState()
    }

    /**
     * UI更新线程
     */
    private fun updateState() {
        if (mMediaRecorder != null) {
            val ratio: Double = mMediaRecorder!!.maxAmplitude.toDouble()
            val db: Double
            if (ratio > 1) {
                db = 20 * Math.log10(ratio)
                if (audioStatusListener != null) {
                    audioStatusListener!!.onRecording(db, System.currentTimeMillis() - startTime)
                }
            }
        }
        mHand.postDelayed(mAudioThreadStateUpdate, 1000)
    }

    /**
     * 开始播放
     */
    fun playAudio(path: String) {
        synchronized(this) {
            try {

                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer()
                }
                if (mMediaPlayer!!.isPlaying || mMediaPlayer!!.isLooping)
                    return
                val file = File(path)
                val fis = FileInputStream(file)
                mMediaPlayer!!.setDataSource(fis.fd)
                mMediaPlayer!!.prepare()
                mMediaPlayer!!.start()
                mMediaPlayer!!.setOnCompletionListener {
                    stopPlayAudio()
                }
            } catch (e: Exception) {
               println(e)
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