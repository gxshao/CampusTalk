package com.mrsgx.campustalk.mvp.find

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.*
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.remote.WorkerRemoteDataSource
import com.mrsgx.campustalk.data.WorkerRepository
import com.mrsgx.campustalk.obj.CTLocation
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_find.*
import android.graphics.drawable.ColorDrawable
import android.widget.*
import com.github.snowdream.android.widget.SmartImageView
import com.mrsgx.campustalk.retrofit.Api


class FindActivity : Activity(),FindContract.View {
    override fun setItemEnable(pos: Int) {
        //关注之后的状态
        if(mSelectBtn!=null){
            mSelectBtn!!.isEnabled=false
            mSelectBtn!!.text="已关注"
        }
    }


    private lateinit var mListUser:ArrayList<HashMap<String,String>>
    private lateinit var mPopListUsers:PopupWindow
    private lateinit var mAdapter:SimpleAdapter
    private lateinit var mListView:ListView
    private lateinit var mView:View
    private lateinit var context:Context
    private var mSelectBtn:Button?=null
    override fun showUserList(users: ArrayList<HashMap<String,String>>) {
        if(users.size<=0)
        {
            showMessage("附近没有一面之缘的异性用户( • ̀ω•́ )✧")
            return
        }
        mListUser.clear()
        mListUser.addAll(users)
        mAdapter.notifyDataSetChanged()
        mPopListUsers.setBackgroundDrawable(ColorDrawable())
        mPopListUsers.showAsDropDown(mView)
    }

    override fun showMarkers(marks: ArrayList<CTLocation>) {
        if(marks.size<=0){
            return
        }
        for(temp in marks){
            addMarker(temp)
        }
        setUserMapCenter(LatLng(marks[0].Latitude.toDouble(),marks[0].Longitude.toDouble()))
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (this).window.attributes
        lp.alpha = bgAlpha
        (this).window.attributes = lp
    }
    @SuppressLint("InflateParams")
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
                val nickname=view.findViewById<View>(R.id.find_nickname) as TextView
                val headpic=view.findViewById<View>(R.id.find_headpic) as SmartImageView
                val userexplain=view.findViewById<View>(R.id.find_userexplain) as TextView
                nickname.text=mListUser[position]["nickname"].toString()
                headpic.setImageUrl(Api.API_HEADPIC_BASE+mListUser[position]["headpic"].toString(),Rect())
                userexplain.text=mListUser[position]["userexplain"].toString()
                useBtn.setOnClickListener {
                    findchatpresenter.followPartner(mListUser[p]["uid"].toString(),p)
                    mSelectBtn=useBtn
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
            }
            mListView.adapter=mAdapter
        }
    }
    @SuppressLint("InflateParams")
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
            TalkerProgressHelper.getInstance(context).show("正在加载附近的人..")
            if(bundle!=null){
                    findchatpresenter.getUserListByLoc(bundle.getParcelable("markLoc"))
            }
              true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.actionBar.setBackgroundDrawable(this.getDrawable(R.drawable.actionbar_head))
        }
        this.actionBar.title="时光机"
        this.actionBar.setDisplayUseLogoEnabled(false)
        this.actionBar.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 添加新的标记到地图中
     */
    private fun addMarker(loc:CTLocation){
        val point=LatLng(loc.Latitude.toDouble(),loc.Longitude.toDouble())
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
        TODO("not implemented") //To change Body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(msg: String?) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
    override fun showMessage(msg: String, level: Int, time: Int) {
        CTNote.getInstance(this,rootView!!).show(msg,level,time)
    }

    override fun startNewPage(target: Class<*>?) {
        TODO("not implemented") //To change Body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: FindContract.Prensenter?) {
        TODO("not implemented") //To change Body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap:BaiduMap
    private lateinit var findchatpresenter:FindPrensenter
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        mView=LayoutInflater.from(this).inflate(R.layout.activity_find,null)
        setContentView(mView)
        context=this
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

    override fun onStop() {
        CTNote.getInstance(this,rootView!!).hide()
        super.onStop()
    }
}
