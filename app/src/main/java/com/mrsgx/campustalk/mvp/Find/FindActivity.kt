package com.mrsgx.campustalk.mvp.Find

import android.app.Activity
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.*
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.widget.CTNote
import kotlinx.android.synthetic.main.activity_find.*

class FindActivity : Activity(),FindContract.View {
    override fun initViews() {
        rootView=LayoutInflater.from(this).inflate(R.layout.activity_find,null)
        mMap=map_bdmap.map
        mMap.mapType=BaiduMap.MAP_TYPE_NORMAL
        map_bdmap.mapLevel
        mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(MapStatus.Builder().zoom(20f).build()))
        this.actionBar.setBackgroundDrawable(this.resources.getDrawable(R.drawable.actionbar_head))
        this.actionBar.title="时光机"
        this.actionBar.setDisplayUseLogoEnabled(false)
        this.actionBar.setDisplayHomeAsUpEnabled(true)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startNewPage(target: Class<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: FindContract.Prensenter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap:BaiduMap
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setContentView(R.layout.activity_find)
        initViews()

    }

    override fun onDestroy() {
        super.onDestroy()
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
