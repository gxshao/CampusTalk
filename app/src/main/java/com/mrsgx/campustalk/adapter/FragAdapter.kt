package com.mrsgx.campustalk.adapter

import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * Created by Shao on 2017/9/22.
 */
class FragAdapter(private val fm: FragmentManager,private var fragments:List<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
       return fragments[position]
    }

    override fun getCount(): Int {
       return fragments.size
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
    }

}