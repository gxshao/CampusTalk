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
        private var O_context:WeakReference<Context>?=null
        fun getInstance(context: Context): TalkerProgressDialog {
            synchronized(this) {
                if(O_context!=context)
                     instance = WeakReference(TalkerProgressDialog(context, R.style.BeanDialog))
                O_context=WeakReference(context)
                return instance?.get()!!
            }
        }
        fun hide(){
            if(instance!=null){
                instance?.get()!!.hideDialog()
            }
            instance=null
            O_context=null
        }
    }
}