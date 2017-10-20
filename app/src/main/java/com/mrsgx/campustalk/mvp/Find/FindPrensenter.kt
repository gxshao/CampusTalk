package com.mrsgx.campustalk.mvp.Find

import android.content.Context
import com.google.gson.Gson
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTLocation
import com.mrsgx.campustalk.obj.CTUser
import com.mrsgx.campustalk.retrofit.Api
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shao on 2017/9/25.
 * have no any paimian
 */
class FindPrensenter(private val view: FindContract.View, private val workerRepository: WorkerRepository, private val context: Context) :FindContract.Prensenter{
    private val compostionDisposeable=CompositeDisposable()
    //根据选取的时间段，获取时间段内的所有坐标
    override fun getLocationList(times: String) {
        val disposeable=workerRepository.getLocationList(GlobalVar.LOCAL_USER!!.Uid,times).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<ArrayList<CTLocation>>>(){
                    override fun onNext(value: ResponseResult<ArrayList<CTLocation>>?) {
                        view.showMarkers(value!!.Body!!)
                    }

                    override fun onComplete() {
                        TalkerProgressHelper.getInstance(context).hideDialog()
                    }

                    override fun onError(e: Throwable?) {
                        println(e)
                    }
                })
        compostionDisposeable.add(disposeable)
    }
    override fun followPartner(uid: String,pos: Int) {
        val disposable=workerRepository.FollowEvents(GlobalVar.LOCAL_USER!!.Uid,uid,GlobalVar.ACTION_FOLLOW).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()!!)
                .subscribeWith(object :DisposableObserver<ResponseResult<Boolean>>(){
                    override fun onNext(value: ResponseResult<Boolean>?) {
                        if(value!!.Body as Boolean)
                        {
                           view.setItemEnable(pos)
                            view.showMessage("添加关注成功",CTNote.LEVEL_TIPS,CTNote.TIME_SHORT)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        view.showMessage(context.getString(R.string.follow_fail), CTNote.LEVEL_WARNING, CTNote.TIME_SHORT)
                    }

                    override fun onComplete() {
                    }
                })
        compostionDisposeable.add(disposable)
    }
    override fun getUserListByLoc(loc: CTLocation) {
        val disposiable=workerRepository.getUserListByLoc(GlobalVar.LOCAL_USER!!.Uid,Gson().toJson(loc)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object :DisposableObserver<ResponseResult<ArrayList<CTUser>>>(){
                    override fun onNext(value: ResponseResult<ArrayList<CTUser>>?) {
                        val maplist=ArrayList<HashMap<String,String>>()
                        for(tmp in value!!.Body!!){
                            val map=HashMap<String,String>()
                            map.put("headpic", Api.Companion.API_HEADPIC_BASE+tmp.Headpic!!)
                            map.put("uid",tmp.Uid)
                            map.put("userexplain",tmp.Userexplain!!)
                            map.put("nickname",tmp.Nickname!!)
//                            map.put("headpic",tmp.Headpic!!)
//                            map.put("headpic",tmp.Headpic!!)
//                            map.put("headpic",tmp.Headpic!!)
                            maplist.add(map)
                        }
                            view.showUserList(maplist)
                    }

                    override fun onComplete() {
                        TalkerProgressHelper.getInstance(context).hideDialog()
                    }

                    override fun onError(e: Throwable?) {

                    }
                })
        compostionDisposeable.add(disposiable)
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}