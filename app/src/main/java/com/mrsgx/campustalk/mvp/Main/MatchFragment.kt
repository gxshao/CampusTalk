package com.mrsgx.campustalk.mvp.Main

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.mvp.Chat.ChatActivity
import com.mrsgx.campustalk.utils.TalkerProgressHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_match.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MatchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MatchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MatchFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_match, container, false)
        view.setOnTouchListener { _, motionEvent ->
            true
        }
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val anim_match = AnimationUtils.loadAnimation(context, R.anim.match_btn_flash)
        anim_match.setAnimationListener(mAnim_bounce)
        txt_net_state_tips.typeface=GlobalVar.typeface
        btn_start_match.startAnimation(anim_match)
        btn_start_match.setOnClickListener {
            TalkerProgressHelper.getInstance(parentContext!!).show("正在准备匹配..")
            mHand.postDelayed({
                startActivity(Intent(context, ChatActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
                mHand.sendMessage(mHand.obtainMessage())
            },1500)
        }
    }

    var rootview: MainContract.View? = null
    var parentContext: Context? = null
    val mHand:Handler by lazy {
        @SuppressLint("HandlerLeak")
        object :Handler(){
            override fun handleMessage(msg: Message?) {
                TalkerProgressHelper.getInstance(parentContext!!).hideDialog()
                super.handleMessage(msg)
            }
        }
    }
    private val mAnim_bounce = object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            if (parentContext != null) {
                val bounce_anim = AnimationUtils.loadAnimation(parentContext, R.anim.match_btn_flash)
                bounce_anim.setAnimationListener(this)
                btn_start_match.startAnimation(bounce_anim)
            }
        }

        override fun onAnimationStart(p0: Animation?) {

        }
    }

    fun setNetworkStateIcon(state: Boolean) {
        if (icon_net_state != null)
            if (state)
                icon_net_state.setBackgroundDrawable(parentContext!!.resources.getDrawable(R.mipmap.wifi_connect))
            else
                icon_net_state.setBackgroundDrawable(parentContext!!.resources.getDrawable(R.mipmap.wifi_disconnect))

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MatchFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MatchFragment {
            val fragment = MatchFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }


}// Required empty public constructor

