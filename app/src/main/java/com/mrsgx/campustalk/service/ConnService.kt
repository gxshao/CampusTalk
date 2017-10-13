package com.mrsgx.campustalk.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.google.gson.Gson
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.GlobalVar.Companion.UPLOAD_SPAN
import com.mrsgx.campustalk.data.Local.DB
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.ResponseResult
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class ConnService : Service() {
    private val mLocationClient: LocationClient by lazy {
        LocationClient(applicationContext).apply {
            registerLocationListener(myListener)
        }
    }

    private var IS_UPLOAD=true
   private val mThread:Thread by lazy {
        Thread({
            val worker= WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance())
            while(IS_UPLOAD){
                val str=Gson().toJson(DB.getInstance(applicationContext).getAllLocation())
                println(str)
                worker.uploadGpsInfo(str).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<ResponseResult<Boolean>>(){
                            override fun onComplete() {

                            }

                            override fun onNext(value: ResponseResult<Boolean>?) {
                                println("上传完成"+value!!.Body)
                                DB.getInstance(applicationContext).clearLocation()
                            }

                            override fun onError(e: Throwable?) {
                                println(e)
                            }
                        })
                Thread.sleep(UPLOAD_SPAN)
            }
        })
    }
    private val BAIDU_READ_PHONE_STATE = 100

    private val  myListener: BDAbstractLocationListener = MyLocationListener();

    private fun MyLocationListener()=object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            println("收到定位")
            //获取定位结果
            location.getTime();    //获取定位时间
            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.getLocType();    //获取定位类型
            location.getLatitude();    //获取纬度信息
            location.getLongitude();    //获取经度信息
            location.getRadius();    //获取定位精准度
            location.getAddrStr();    //获取地址信息
            location.getCountry();    //获取国家信息
            location.getCountryCode();    //获取国家码
            location.getCity();    //获取城市信息
            location.getCityCode();    //获取城市码
            location.getDistrict();    //获取区县信息
            location.getStreet();    //获取街道信息
            location.getStreetNumber();    //获取街道码
            location.getLocationDescribe();    //获取当前位置描述信息
            location.getPoiList();    //获取当前位置周边POI信息

            location.getBuildingID();    //室内精准定位下，获取楼宇ID
            location.getBuildingName();    //室内精准定位下，获取楼宇名称
            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息


            if(GlobalVar.LOCAL_USER!=null){
                var loc:CTLocation?=null
            if (location.getLocType() == BDLocation.TypeGpsLocation){

                //当前为GPS定位结果，可获取以下信息
                location.getSpeed();    //获取当前速度，单位：公里每小时
                location.getSatelliteNumber();    //获取当前卫星数
                location.getAltitude();    //获取海拔高度信息，单位米
                location.getDirection();    //获取方向信息，单位度
                loc=CTLocation()
                loc.Uid =GlobalVar.LOCAL_USER!!.Uid
                loc.Datetime =location.time
                loc.Latitude =location.latitude.toString()
                loc.Longitude =location.longitude.toString()
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                //当前为网络定位结果，可获取以下信息
                location.getOperators();    //获取运营商信息
                loc=CTLocation()
                loc.Uid =GlobalVar.LOCAL_USER!!.Uid
                loc.Datetime =location.time
                loc.Latitude =location.latitude.toString()
                loc.Longitude =location.longitude.toString()
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                //当前为网络定位结果
                loc=CTLocation()
                loc.Uid =GlobalVar.LOCAL_USER!!.Uid
                loc.Datetime =location.time
                loc.Latitude =location.latitude.toString()
                loc.Longitude =location.longitude.toString()

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                //当前网络定位失败
                //可将定位唯一ID、IMEI、定位失败时间反馈至loc-bugs@baidu.com

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                //当前网络不通

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                //当前缺少定位依据，可能是用户没有授权，建议弹出提示框让用户开启权限
                //可进一步参考onLocDiagnosticMessage中的错误返回码
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(applicationContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(applicationContext,"定位权限不足，请修改权限后重试",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    TODO("VERSION.SDK_INT < M")
                    // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)


                }

            }
                if(loc!=null)
                {
                    DB.getInstance(applicationContext).insertLocation(loc)
                }
            }
        }
        override fun onLocDiagnosticMessage(p0: Int, p1: Int, p2: String?) {
            super.onLocDiagnosticMessage(p0, p1, p2)

        }
    }
    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        CTConnection.getInstance(this).Start()
        //声明LocationClient类
        initLocation()
        mLocationClient.start()
        mThread.start()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        IS_UPLOAD=false
        super.onDestroy()
    }
    //初始化百度位置信息
    private fun initLocation() {

        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll")
        //可选，默认gcj02，设置返回的定位结果坐标系

        val span = 300000 //五分钟获取一次
        option.setScanSpan(span)
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true)
        //可选，设置是否需要地址信息，默认不需要

        option.isOpenGps = true
        //可选，默认false,设置是否使用gps

        option.isLocationNotify = true
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false)
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.isIgnoreCacheException=true
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false)
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        option.setWifiCacheTimeOut(5 * 60 * 1000)
        //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位

        mLocationClient.locOption = option
    }

}
