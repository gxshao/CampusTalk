package com.mrsgx.campustalk.mvp.Login

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
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
        loginpresenter = LoginPresenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()!!), this)

        var dialog = TalkerProgressDialog(this, R.style.BeanDialog)
        dialog.show()
        btn_login.setOnClickListener {
            dialog.mText!!.text="xx"
            dialog.show()
        }
        //loginpresenter!!.Login("aaa","123") }
    }
}
