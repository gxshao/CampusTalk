package com.mrsgx.campustalk.mvp.Main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.adapter.FollowAdapter
import com.mrsgx.campustalk.interfaces.RecyclerViewClickListener
import com.mrsgx.campustalk.obj.CTUser
import kotlinx.android.synthetic.main.fragment_follow.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FollowFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FollowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowFragment : Fragment(), RecyclerViewClickListener {

    private var mCurrenSelectItem = 0
    override fun onItemClick(view: View, pos: Int) {
        mCurrenSelectItem = pos
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_follow, container, false)
    }

    val mArray = ArrayList<CTUser>()
    var layoutManager: LinearLayoutManager? = null
    var mAdataper: FollowAdapter? = null
    @SuppressLint("ResourceAsColor")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        mAdataper = FollowAdapter(mArray)
        mAdataper!!.mUnfollowListener = btnListener
        mAdataper!!.mRecyclerViewListener = this
        swipe_refresh.setColorSchemeColors(R.color.ctnote_blue_center)
        swipe_refresh.post { swipe_refresh.isRefreshing = true }
        swipe_refresh.setOnRefreshListener({
            mHandler.postDelayed({
                mArray.clear()
                initData()
            }, 1000)
        })
        follw_recyler.layoutManager = layoutManager
        follw_recyler.adapter = mAdataper
        follw_recyler.itemAnimator = DefaultItemAnimator()
        follw_recyler.addItemDecoration(DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL))
        follw_recyler.addOnScrollListener(mOnScrollListener)
        mHandler.postDelayed({
            mArray.clear()
            initData()
        }, 1500)
    }

    private var isLoading: Boolean = false
    private val mOnScrollListener = object : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    synchronized(this){
                    val lvip = layoutManager!!.findLastVisibleItemPosition()
                    println(lvip)
                    if (lvip + 1 == mAdataper!!.itemCount) {
                        if (swipe_refresh.isRefreshing) {
                            mAdataper!!.notifyItemRemoved(mAdataper!!.itemCount)
                        }else if (!isLoading) {
                            isLoading = true
                            mHandler.postDelayed({
                                initData()
                                isLoading = false
                            }, 1000)
                        }
                    }
                    }
                }
            }

        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)


        }
    }
    private val mRefreshListener = SwipeRefreshLayout.OnRefreshListener {


    }

    private fun initData() {
        mArray.clear()
        var usr = CTUser()
        usr.Nickname = "sadasdasdas"
        usr.Userexplain = "这是一个简单的说明而已"
        usr.Headpic = "http://img.blog.csdn.net/20151123180331708"
        var usr1 = CTUser()
        usr1.Nickname = "哈哈哈哈哈"
        usr1.Userexplain = "这是一个简单的说明而已"
        usr1.Headpic = "http://img.blog.csdn.net/20151123180331708"
        mArray.add(usr)
        mArray.add(usr1)
        mAdataper!!.notifyDataSetChanged()
        mAdataper!!.notifyItemRemoved(mArray.size - 1)
        if(swipe_refresh!=null)
             swipe_refresh.isRefreshing = false
    }

    var btnListener = View.OnClickListener { view ->
        synchronized(this) {
            //网络删除
            //本地删除
            var pos=follw_recyler.indexOfChild(view.tag as View)
            mAdataper!!.deleteFromFollowList(pos)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun dispatchMessage(msg: Message?) {
            if (msg == null)
                return
            when (msg.what) {

            }
            super.dispatchMessage(msg)
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
         * @return A new instance of fragment FollowFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FollowFragment {
            val fragment = FollowFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
