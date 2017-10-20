package com.mrsgx.campustalk.mvp.Find

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.*
import android.widget.Button
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.SimpleAdapter
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.Remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTLocation
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_find.*


class FindActivity : Activity(),FindContract.View {
    override fun setItemEnable(pos: Int) {
        //关注之后的状态
    }


    private lateinit var mListUser:ArrayList<HashMap<String,String>>
    private lateinit var mPopListUsers:PopupWindow
    private lateinit var mAdapter:SimpleAdapter
    private lateinit var mListView:ListView
    override fun showUserList(users: ArrayList<HashMap<String,String>>) {
        mListUser.clear()
        mListUser.addAll(users)
        mAdapter.notifyDataSetChanged()
    }

    override fun showMarkers(marks: ArrayList<CTLocation>) {
        if(marks.size<=0){
            return
        }
        setUserMapCenter(LatLng(marks[0].Longitude.toDouble(),marks[0].Latitude.toDouble()))
        for(temp in marks){
            addMarker(temp)
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (this).window.attributes;
        lp.alpha = bgAlpha
        (this).window.attributes = lp
    }
    fun initPopWindow(){
        mPopListUsers= PopupWindow(this)
        mPopListUsers.contentView=LayoutInflater.from(this).inflate(R.layout.people_list_layout,null)
        mPopListUsers.setOnDismissListener {
            setBackgroundAlpha(1f)
        }
        val ids=IntArray(4).apply {
            R.id.find_headpic
            R.id.find_nickname
            R.id.find_userexplain
        }
        mAdapter= object :SimpleAdapter(this,mListUser,R.layout.item_find_layout ,arrayOf("headpic","nickname","userexplain"), ids){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val p = position
                val view = super.getView(position, convertView, parent)
                val useBtn = view.findViewById<View>(R.id.find_btn_follow) as Button
                useBtn.setOnClickListener {
                    findchatpresenter.followPartner(mListUser[p]["uid"].toString(),p)
                }
                return view
            }
        }
        mPopListUsers.setBackgroundDrawable(android.graphics.drawable.BitmapDrawable())
        mPopListUsers.isOutsideTouchable=true
        mPopListUsers.isFocusable=true
        val view=mPopListUsers.contentView
        val btnclose:Button
        if(view!=null){
            mListView=view.findViewById(R.id.list_people)
            btnclose=view.findViewById(R.id.btn_close_peoplelist)
            btnclose.setOnClickListener {
                //关闭当前列表
                mPopListUsers.dismiss()
                mListUser.clear()
                mAdapter.notifyDataSetChanged()
                mListView.removeAllViews()
            }
            mListView.adapter=mAdapter
        }
    }
    override fun initViews() {
        rootView=LayoutInflater.from(this).inflate(R.layout.activity_find,null)
        mMap=map_bdmap.map
        mMap.mapType=BaiduMap.MAP_TYPE_NORMAL
        /**
         * 点击标记时，去网络请求附近的人
         */
        mMap.setOnMarkerClickListener {m->
            //当坐标被点击时
            setUserMapCenter(m.position)
            //弹出附近的人
            val bundle=m.extraInfo
            TalkerProgressHelper.getInstance(applicationContext).show("正在加载附近的人..")
            findchatpresenter.getUserListByLoc(bundle.getParcelable("markLoc"))
            false
        }

        this.actionBar.setBackgroundDrawable(this.resources.getDrawable(R.drawable.actionbar_head))
        this.actionBar.title="时光机"
        this.actionBar.setDisplayUseLogoEnabled(false)
        this.actionBar.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 添加新的标记到地图中
     */
    private fun addMarker(loc:CTLocation){
        val point=LatLng(loc.Longitude.toDouble(),loc.Latitude.toDouble())
        val icon=com.baidu.mapapi.map.BitmapDescriptorFactory
                .fromResource(R.mipmap.marker)
        val option:MarkerOptions = MarkerOptions()
                .position(point)
                .icon(icon)
        val bundle=Bundle()
        bundle.putParcelable("markLoc",loc)
        option.extraInfo(bundle)
        mMap.addOverlay(option) // 将标注添加到地图中
    }

    /**
     * 地图中心
     */
    private fun setUserMapCenter(x:LatLng) {
        //定义地图状态
        val mMapStatus = MapStatus.Builder()
                .target(x)
                .zoom(20f)
                .build()
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus)
        //改变地图状态
        mMap.setMapStatus(mMapStatusUpdate)

    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun Close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {

    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this,rootView!!).show(msg,level,time)
    }

    override fun startNewPage(target: Class<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: FindContract.Prensenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap:BaiduMap
    private lateinit var findchatpresenter:FindPrensenter
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_find)
        mListUser=ArrayList()
        findchatpresenter= FindPrensenter(this, WorkerRepository.getInstance(WorkerRemoteDataSource.getInstance()),this)
        TalkerProgressHelper.getInstance(this).show("正在加载坐标数据请稍候...")
        val times=intent.getStringExtra(GlobalVar.SELECT_TIME_RANGE)
        findchatpresenter.getLocationList(times)
        //远程获取坐标数据
        //加载坐标数据点
        //设置监听跳转用户列表
        initViews()
        initPopWindow()
    }

    override fun onDestroy() {
        super.onDestroy()
        TalkerProgressHelper.hide()
        map_bdmap.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        map_bdmap.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_bdmap.onPause()
    }

    private var rootView: View?=null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onStop() {
        CTNote.getInstance(this,rootView!!).hide()
        super.onStop()
    }
}
