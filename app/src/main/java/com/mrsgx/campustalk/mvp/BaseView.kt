package com.mrsgx.campustalk.mvp;


interface BaseView<T> {

    fun initViews()
    fun Close()
    fun showMessage (msg:String?)
    fun startNewPage(target:Class<*>?)
    fun setPresenter(presenter:T?)
}
