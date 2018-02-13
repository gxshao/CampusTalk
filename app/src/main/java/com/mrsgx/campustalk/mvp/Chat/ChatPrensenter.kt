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
             CTConnection.getInstance(context).mChatListener=null
    }

    override fun followPartner(uid: String) {
        val disposable=workerRepository.FollowEvents(GlobalVar.LOCAL_USER!!.Uid,uid,GlobalVar.ACTION_FOLLOW).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object :DisposableObserver<ResponseResult<Boolean>>(){
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body as Boolean)
                        {
                            view.setFollowState(true)  //关注上了
                        }

                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.follow_fail),CTNote.LEVEL_WARNING,CTNote.TIME_SHORT)
                    }

                    override fun onComplete() {
                    }
                })
        compositeDisposable.add(disposable)
    }

    override fun unfollowPartner(uid: String) {
        val disposable=workerRepository.FollowEvents(GlobalVar.LOCAL_USER!!.Uid,uid,GlobalVar.ACTION_UNFOLLOW).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object :DisposableObserver<ResponseResult<Boolean>>(){
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body as Boolean)
                        {
                            view.setFollowState(false)  //取消关注成功
                        }

                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.unfollow_fail),CTNote.LEVEL_WARNING,CTNote.TIME_SHORT)
                    }

                    override fun onComplete() {
                    }
                })
        compositeDisposable.add(disposable)
    }


    override fun startMatch() {
        view.setCurrentState(1)
        val disposiable=workerRepository.StartMatch(GlobalVar.LOCAL_USER!!.Uid,GlobalVar.LOCAL_USER!!.School!!.SCode).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>() {
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body!!)
                        {
                            view.setCurrentState(1)
                        }else
                        {
                            view.setCurrentState(0)
                            view.showMessage(context.getString(R.string.start_match_failed))
                            view.Close()
                        }

                    }

                    override fun onError(e: Throwable?) {
                        view.setCurrentState(0)
                        view.showMessage(context.getString(R.string.start_match_failed))
                        view.Close()
                    }

                    override fun onComplete() {
                    }
                })
        compositeDisposable.add(disposiable)
    }

    override fun stopMatch() {
        val disposiable=workerRepository.StopMatch(GlobalVar.LOCAL_USER!!.Uid,GlobalVar.LOCAL_USER!!.School!!.SCode)
                .observeOn(AndroidSchedulers.mainThread()!!)
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
                            compositeDisposable.dispose()
                        }
                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.stop_matching_failed))
                        compositeDisposable.dispose()
                    }

                    override fun onComplete() {
                    }
                })
        compositeDisposable.add(disposiable)
    }



    @SuppressLint("SimpleDateFormat")
    private fun sendMessage(msg:String,type:String){
        val m=CTMessage()
        m.Type=type
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

    /**
     * 发送图片
     */
    override fun sendImageMsg(picBase: String) {
        sendMessage(picBase,CTMessage.MESSAGE_TYPE_IMAGE)
    }

    /**
     * 发送音频
     */
    override fun sendAudioMsg(audioBase: String) {
        sendMessage(audioBase,CTMessage.MESSAGE_TYPE_AUDIO)
    }

    /**
     * 发送文本
     */
    override fun sendTextMsg(msg: String) {
        sendMessage(msg,CTMessage.MESSAGE_TYPE_TEXT)
    }

    private val compositeDisposable=CompositeDisposable()
    override fun getPartnerInfo(uid: String){
        val disposable = workerRepository.GetUserInfoById(uid).observeOn(AndroidSchedulers.mainThread()!!)
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object : DisposableObserver<ResponseResult<CTUser>>() {
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable?) {

                       view.setPartner(CTUser())
                        getPartnerInfo(uid)
                    }

                    override fun onNext(value: ResponseResult<CTUser>) {
                      val partner=value.Body
                        if(partner!=null&&!partner.Nickname.isNullOrEmpty()){
                            view.setPartner(partner)
                            view.setCurrentState(1)
                        }else
                        {
                            startMatch()
                            view.reset()
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
                var file=view.getChatFolder()
                file=file+Utils.getFormatDate() + ".amr"
                Utils.decoderBase64File(msg.Body!!,file)
                view.onReceiveAudio(file)
            }
            CTMessage.MESSAGE_TYPE_EMOJI->{

            }
            CTMessage.MESSAGE_TYPE_IMAGE ->{
                var file=view.getChatFolder()
                file=file+Utils.getFormatDate()+".png"

                Utils.decoderBase64File(msg.Body!!,file)
                view.onReceiveImage(file)
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