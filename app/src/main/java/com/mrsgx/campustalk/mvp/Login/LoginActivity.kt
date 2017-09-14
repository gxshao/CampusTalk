package com.mrsgx.campustalk.mvp.Login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mrsgx.campustalk.R

class LoginActivity : AppCompatActivity(),LoginContract.View {
    override fun initViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startNewPage(target: Class<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: LoginContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun xx() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
