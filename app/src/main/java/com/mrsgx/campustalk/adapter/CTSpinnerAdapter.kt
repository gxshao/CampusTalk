package com.mrsgx.campustalk.adapter

import android.content.Context
import android.database.DataSetObserver
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import android.widget.TextView

/**
 * Created by Shao on 2017/10/1.
 */
class CTSpinnerAdapter(private val context: Context, private val mListData: ArrayList<HashMap<String, String>>) : SpinnerAdapter {
    private var mListView:ArrayList<View>
    init {
        mListView= ArrayList()
    }
    override fun getView(pos: Int, vs: View?, p: ViewGroup?): View {
        val txt :TextView? = if(vs!=null)
        {
            vs as TextView
        }else
        {
            TextView(context)
        }
        val map = mListData[pos]
        txt!!.text = map["key2"].toString()
        txt.tag = map["key1"].toString()
        return txt
       }

    override fun isEmpty(): Boolean {
        return mListData.isEmpty()
    }


    override fun registerDataSetObserver(observer: DataSetObserver?) {
    }

    override fun getItemViewType(p0: Int): Int {
        return 1
    }

    override fun getItem(p0: Int): Any {
        if(p0>=mListView.size||p0<0)
            return Any()
        return mListView[p0]
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(p0: Int): Long {
        if(p0>=mListView.size||p0<0)
            return 0
        return mListView[p0].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getDropDownView(p: Int, convertView: View?, p2: ViewGroup?): View {
        val txt :TextView? = if(convertView!=null)
        {
            convertView as TextView
        }else
        {
            TextView(context)
        }
        txt!!.gravity=Gravity.CENTER_HORIZONTAL
        txt.setPadding(10,2,10,2)
        val map = mListData[p]
        txt.text = map["key2"].toString()
        txt.tag = map["key1"].toString()
        mListView.add(txt)
        return txt
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
    }

    override fun getCount(): Int {
        return mListData.size
    }
}