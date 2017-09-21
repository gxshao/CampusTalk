package com.mrsgx.campustalk.mvp.Main

import android.content.Context
import com.mrsgx.campustalk.data.WorkerRepository

/**
 * Created by Shao on 2017/9/4.
 */
class MainPresenter(private val view:MainContract.View,private val workerRepository: WorkerRepository,private val context: Context):MainContract.Presenter {
    init {

    }
    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}