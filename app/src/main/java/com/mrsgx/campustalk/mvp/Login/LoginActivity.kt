package com.mrsgx.campustalk.mvp.Login

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.Window
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.mvp.Welcome.WelcomeActivity
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.TalkerProgressDialog
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity(), LoginContract.View {
    var loginpresenter: LoginPresenter? = null
    override fun initViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startNewPage(target: Class<*>?) {
        startActivity(Intent(this,target), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        this.finish()
    }

    override fun setPresenter(presenter: LoginContract.Presenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_login)
        loginpresenter = LoginPresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()), this)
        btn_login.setOnClickListener {
         TalkerProgressHelper.getInstance(this).show("what hell")
           // this.startActivity(Intent(this, WelcomeActivity::class.java))
        }
        //loginpresenter!!.Login("aaa","123") }
    }
}
