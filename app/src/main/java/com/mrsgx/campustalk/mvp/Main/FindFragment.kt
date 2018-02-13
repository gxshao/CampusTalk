package com.mrsgx.campustalk.mvp.Main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrsgx.campustalk.R
import com.mrsgx.campustalk.data.GlobalVar
import com.mrsgx.campustalk.data.GlobalVar.Companion.ALLOW_FIND_DAY
import com.mrsgx.campustalk.mvp.Find.FindActivity
import kotlinx.android.synthetic.main.fragment_find.*
import java.sql.Time
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FindFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FindFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FindFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var rootview: MainContract.View? = null
    private var mListener: OnFragmentInteractionListener? = null
    lateinit var parentContext: Context

    private lateinit var mDatePicker: DatePickerDialog
    private lateinit var mStartTimePcker: TimePickerDialog
    private lateinit var mEndPicker: TimePickerDialog

    private var mDay = ""
    private var mStartTime = "00:00:00"
    private var mEndTime = "00:00:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the people_list_layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()

    }

    private val mOnDateSelected = DatePickerDialog.OnDateSetListener { p, yyyy, MM, dd ->
        val ms=MM+1
        mDay = "$yyyy-$ms-$dd"
        ed_date.setText(mDay)
    }


    private val mOnStartTimeSelected = TimePickerDialog.OnTimeSetListener { p0, hh, mm ->
        mStartTime = "$hh:$mm:0"
        ed_start_time.setText(mStartTime)
    }

    private val mOnEndTimeSelected = TimePickerDialog.OnTimeSetListener { p0, hh, mm ->
        mEndTime ="$hh:$mm:0"
        if(Time.valueOf(mEndTime)<=Time.valueOf(mStartTime)){
            rootview!!.showMessage("时间区间选择错误")
            return@OnTimeSetListener
        }
        ed_end_time.setText(mEndTime)
    }

    private fun initViews() {

        fint_title.typeface = GlobalVar.typeface
        mDatePicker = DatePickerDialog(context, R.style.ThemeDialog, mOnDateSelected, 0, 0, 0)
        mDatePicker.datePicker.minDate = System.currentTimeMillis() - ALLOW_FIND_DAY
        mDatePicker.datePicker.maxDate = System.currentTimeMillis()
        mStartTimePcker = TimePickerDialog(context, R.style.ThemeDialog, mOnStartTimeSelected, 0, 0, true)
        mEndPicker = TimePickerDialog(context, R.style.ThemeDialog, mOnEndTimeSelected, 0, 0, true)
        btn_search.setOnClickListener {
            //待传入的数据为
            val times= "$mDay $mStartTime$$mDay $mEndTime"
            val intent=Intent(context, FindActivity::class.java)
            intent.putExtra(GlobalVar.SELECT_TIME_RANGE,times)
            startActivity(intent)
        }
        ed_date.setOnClickListener {
            mDatePicker.show()
        }
        ed_start_time.setOnClickListener {
            mStartTimePcker.show()
        }
        ed_end_time.setOnClickListener {
            mEndPicker.show()
        }
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
         * @return A new instance of fragment FindFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FindFragment {
            val fragment = FindFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
