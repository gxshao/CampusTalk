package com.mrsgx.campustalk.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View

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

    override fun destroyItem(container: View, position: Int, `object`: Any) {
    }

}