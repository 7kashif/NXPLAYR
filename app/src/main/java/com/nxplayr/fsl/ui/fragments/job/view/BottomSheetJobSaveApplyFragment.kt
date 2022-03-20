package com.nxplayr.fsl.ui.fragments.job.view


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
import com.nxplayr.fsl.data.model.ThreedotsBottomPojo
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.list_item_layout.view.*


class BottomSheetJobSaveApplyFragment : BottomSheetDialogFragment() {

    var mListener: SelectList? = null
    var bottomSheetListAdapter: BottomSheetListAdapter? = null
    var mContext: Activity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var list = ArrayList<ThreedotsBottomPojo>()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager= SessionManager(mContext!!)

        if (arguments != null) {
            list = arguments!!.get("data") as ArrayList<ThreedotsBottomPojo>
            from = arguments!!.getString("from")!!
        }

        linearLayoutManager = LinearLayoutManager(mContext)

        bottomSheetListAdapter = BottomSheetListAdapter(mContext!!, from, list, object :
            OnItemClick {
            override fun onClicled(position: Int, from: String) {
                mListener?.onOptionSelect(position,from)
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


   inner class BottomSheetListAdapter(internal var mContext: Activity, from: String, data: List<ThreedotsBottomPojo>, val onItemClick: OnItemClick) :
            RecyclerView.Adapter<BottomSheetListAdapter.ViewHolder>() {
        internal var data: List<ThreedotsBottomPojo> = java.util.ArrayList()
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
            fun bind(name: ThreedotsBottomPojo,position: Int) = with(itemView) {
                itemNameTv.text = name.optionName
               image_icon_bottom_sheet.setImageResource(name.image)
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




