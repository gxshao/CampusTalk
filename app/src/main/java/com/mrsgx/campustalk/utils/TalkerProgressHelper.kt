package com.mrsgx.campustalk.utils

import android.content.Context
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.widget.TalkerProgressDialog
import java.lang.ref.WeakReference

/**
 * Created by Shao on 2017/9/15.
 */
class TalkerProgressHelper {
    companion object {
        private var instance: WeakReference<TalkerProgressDialog>?=null
        fun getInstance(context: Context): TalkerProgressDialog {
            synchronized(this) {
                if(instance==null|| instance!!.get()==null){
                     instance = WeakReference(TalkerProgressDialog(context, R.style.BeanDialog))
                }
                return instance?.get()!!
            }
        }
        fun hide(){
            if(instance!=null&& instance!!.get()!=null){
                instance?.get()!!.hideDialog()
            }
            instance=null
        }
    }
}