package com.mrsgx.campustalk.mvp.Chat

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.interfaces.ChatInterfaces
import com.mrsgx.campustalk.interfaces.NetEventManager
import com.mrsgx.campustalk.obj.CTData
import com.mrsgx.campustalk.obj.CTMessage
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.service.CTConnection
import com.mrsgx.campustalk.service.NetStateListening
import com.mrsgx.campustalk.utils.Utils
import com.mrsgx.campustalk.widget.CTNote
import com.zsoft.signala.ConnectionState
import com.zsoft.signala.SendCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Shao on 2017/9/25.
 */
class ChatPrensenter(private val view: ChatContract.View, private val workerRepository: WorkerRepository, private val context: Context):ChatContract.Prensenter,ChatInterfaces,NetStateListening.NetEvent {

    override fun OnNetChanged(net: Int) {
        when(net){
            Utils.NETWORK_NONE -> {
                view.showMessage(context.getString(R.string.tips_signal_disconnect))
                view.Close()
            }
        }
    }

    override fun OnSignalRChanged(state: Boolean) {
        if (state) {

        } else {
                view.showMessage(context.getString(R.string.tips_signal_disconnect))
                view.Close()
        }
    }

    override fun unregsiter() {
            NetEventManager.getInstance().cancelSubscribe(this)
    }

    override fun followPartner(uid: String) {

    }

    override fun unfollowPartner(uid: String) {
    }


    override fun startMatch() {
        val disposiable=workerRepository.StartMatch(GlobalVar.LOCAL_USER!!.Uid,GlobalVar.LOCAL_USER!!.School!!.SCode).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body!!)
                        {
                            view.setCurrentState(1)
                        }else
                        {
                            view.showMessage(context.getString(R.string.start_match_failed))
                            view.Close()
                        }
                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.start_match_failed))
                        view.Close()
                    }

                    override fun onComplete() {
                    }
                })
        compositeDisposable.add(disposiable)
    }

    override fun stopMatch() {
        val disposiable=workerRepository.StopMatch(GlobalVar.LOCAL_USER!!.Uid,GlobalVar.LOCAL_USER!!.School!!.SCode).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body!!)
                        {
                            view.setCurrentState(0)
                            view.Close()
                        }else
                        {
                            view.showMessage(context.getString(R.string.stop_matching_failed))
                        }
                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.stop_matching_failed))
                    }

                    override fun onComplete() {
                    }
                })
        compositeDisposable.add(disposiable)
    }

    @SuppressLint("SimpleDateFormat")
    override fun sendTextMsg(msg: String) {
        val m=CTMessage()
        m.Type=CTMessage.MESSAGE_TYPE_TEXT
        m.Body=msg
        m.From=GlobalVar.LOCAL_USER!!.Uid
        m.To=view.getPartner().Uid
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//设置日期格式
        m.Time= df.format(Date())
        val result=CTData<CTMessage>()
        result.DataType=CTData.DATATYPE_MESSAGE
        result.Body=m
        CTConnection.getInstance(context).Send(Gson().toJson(result),OnSentCallBack) //
    }
    val OnSentCallBack=object : SendCallback(){
        override fun OnError(ex: Exception?) {
        }

        override fun OnSent(messageSent: CharSequence?) {
        }
    }
    override fun sendImageMsg(path: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendAudioMsg(path: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val compositeDisposable=CompositeDisposable()
    override fun getPartnerInfo(uid: String){
        println(uid)
        val disposable = workerRepository.GetUserInfoById(uid).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<CTUser>>() {
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable?) {
                        println(e!!.message)
                       view.setPartner(CTUser())
                        getPartnerInfo(uid)
                    }

                    override fun onNext(value: ResponseResult<CTUser>) {
                      val partner=value.Body
                        if(partner!=null){
                            view.setPartner(partner)
                        }

                    }
                })
        compositeDisposable.add(disposable)


    }

    override fun onMatched(uid: String) {
            //根据uid获取对方资料信息
        view.setCurrentState(3)
        this.getPartnerInfo(uid)
    }

    override fun onMessage(msg: CTMessage) {
        //消息处理和展示
        when(msg.Type){
            CTMessage.MESSAGE_TYPE_AUDIO->{

            }
            CTMessage.MESSAGE_TYPE_EMOJI->{

            }
            CTMessage.MESSAGE_TYPE_PHOTO->{

            }
            CTMessage.MESSAGE_TYPE_TEXT->{
                view.onReceiveMsg(msg.Body.toString())
            }

        }
    }


    override fun onNextMatch() {
        //重新载入activity信息
        view.showMessage(context.getString(R.string.tips_rematch),CTNote.LEVEL_NOTIFY,CTNote.TIME_SHORT)
        view.reset()
    }
    init {
        CTConnection.getInstance(context).mChatListener=this
        NetEventManager.getInstance().subscribe(this)
    }
    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}