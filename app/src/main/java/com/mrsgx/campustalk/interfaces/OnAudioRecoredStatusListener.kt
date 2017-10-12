package com.mrsgx.campustalk.interfaces

/**
 * Created by Shao on 2017/10/10.
 */
interface OnAudioRecoredStatusListener {
    fun onRecording(db:Double,time:Long)
    fun onStop(audio:String)
}