package com.nxplayr.fsl.ui.fragments.bottomsheet


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.list_item_layout.view.*


class BottomSheetFragment : BottomSheetDialogFragment() {

    var mListener: SelectList? = null
    var bottomSheetListAdapter: BottomSheetListAdapter? = null
    var mContext: Activity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var list = ArrayList<String>()
    var ImagesList = ArrayList<Int>()
    var from: String = ""
    var userId: String = ""
    var selectPosition: Int = 0
    var sessionManager: SessionManager?=null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    fun setOnclickLisner(mListener: SelectList) {
        this.mListener = mListener
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager= SessionManager(mContext!!)

        if (arguments != null) {
            list = arguments!!.get("data") as ArrayList<String>
            from = arguments!!.getString("from")!!
            if(arguments?.getString("userId")!=null)
            {
                userId=arguments?.getString("userId","")!!
            }
        }

        if(sessionManager?.get_Authenticate_User()?.userID?.equals(userId)!!){
            ImagesList.add(R.drawable.popup_share_via_icon)
            ImagesList.add(R.drawable.popup_hide_post_icon)
            ImagesList.add(R.drawable.popup_save_post_icon)
        }else{
            ImagesList.add(R.drawable.popup_share_via_icon)
            ImagesList.add(R.drawable.popup_hide_post_icon)
            ImagesList.add(R.drawable.popup_unfollow_icon)
            ImagesList.add(R.drawable.popup_report_icon)
            ImagesList.add(R.drawable.popup_report_icon)
            ImagesList.add(R.drawable.popup_save_post_icon)
        }


        linearLayoutManager = LinearLayoutManager(mContext)

        bottomSheetListAdapter = BottomSheetListAdapter(mContext!!, from, list, object :
            OnItemClick {
            override fun onClicled(position: Int, from: String) {
                mListener?.onOptionSelect(position,from)

                when (from) {


                    /*"MenuList" -> {

                        when (position) {

                            1 -> {
                                openBottomSheetDialog()
                            }
                            3 -> {
                                (activity as MainActivity).navigateTo(RepostReasonFragment(), RepostReasonFragment::class.java.name, true)
                            }
                            4 -> {
                                (activity as MainActivity).navigateTo(ReportCopyrightFragment(), ReportCopyrightFragment::class.java.name, true)

                            }
                        }

                    }

                    "ShareList" -> {

                        when (position) {
                            1 -> {
                                (activity as MainActivity).navigateTo(SendMessageFragment(), SendMessageFragment::class.java.name, true)
                            }
                        }
                    }*/
                }

                dismiss()


            }

        })
        rv_bottomSheet.layoutManager = linearLayoutManager
        rv_bottomSheet.adapter = bottomSheetListAdapter


    }

    private fun openBottomSheetDialog() {

        val mBottomSheetDialog = BottomSheetDialog(activity!!)
        val sheetView = activity!!.layoutInflater.inflate(com.nxplayr.fsl.R.layout.custom_alert_dialog, null)
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.show()
    }

    interface SelectList {

        fun onOptionSelect(value: Int, from: String)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Activity
        val parent = parentFragment
        /* mListener = if (parent != null) {
            parent as SelectLanguage
        } else {
            mContext as SelectLanguage
        }*/
    }


   inner class BottomSheetListAdapter(internal var mContext: Activity, from: String, data: List<String>, val onItemClick: OnItemClick) :
            RecyclerView.Adapter<BottomSheetListAdapter.ViewHolder>() {
        internal var data: List<String> = java.util.ArrayList()
        var from: String = ""

        init {
            this.data = data
            this.from = from
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(data[position]!!,position)


        }

        override fun getItemCount(): Int {
            return data.size
        }

       inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(name: String,position: Int) = with(itemView) {
                itemNameTv.text = name
//                image_icon_bottom_sheet.setImageResource(ImagesList[position])
                itemView.setOnClickListener {
                    if (onItemClick != null)
                        onItemClick.onClicled(position, from)
                }
            }
        }


    }

     interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}




