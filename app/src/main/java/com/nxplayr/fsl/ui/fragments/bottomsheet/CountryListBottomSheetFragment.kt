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
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.list_three_item_layout.view.*


class CountryListBottomSheetFragment : BottomSheetDialogFragment() {

    var mListener: SelectList? = null
    var threeBottomSheetListAdapter: ThreeBottomSheetListAdapter? = null
    var mContext: Activity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var list = ArrayList<CountryListData>()
    var from: String = ""
    var userId: String = ""
    var selectPosition: Int = 0
    var sessionManager: SessionManager? = null
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
        sessionManager = SessionManager(mContext!!)

        if (arguments != null) {
            list = arguments!!.get("data") as ArrayList<CountryListData>
        }

        linearLayoutManager = LinearLayoutManager(mContext)

        threeBottomSheetListAdapter = ThreeBottomSheetListAdapter(mContext!!, from, list, object :
            OnItemClick {
            override fun onClicled(position: Int, from: String,id:String) {
                mListener?.onOptionSelect(position, from,id)
            }

        })
        rv_bottomSheet.layoutManager = linearLayoutManager
        rv_bottomSheet.adapter = threeBottomSheetListAdapter


    }

    interface SelectList {

        fun onOptionSelect(value: Int, from: String,Id:String)

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


    inner class ThreeBottomSheetListAdapter(var mContext: Activity, from: String, var data: List<CountryListData>, val onItemClick: OnItemClick) :
            RecyclerView.Adapter<ThreeBottomSheetListAdapter.ViewHolder>() {
        var from: String = ""

        init {
            this.from = from
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_three_item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemNameTv.text = data[position].countryName
            holder.itemView.setOnClickListener {
                if (onItemClick != null)
                    onItemClick.onClicled(position, data[position].countryName,data[position].countryID)
            }

        }

        override fun getItemCount(): Int {
            return data.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var itemNameTv = itemView.itemNameTv

            var image_icon_bottom_sheet = itemView.image_icon_bottom_sheet
                 init {
                     image_icon_bottom_sheet.visibility=View.GONE
                 }
        }


    }



    interface OnItemClick {
        fun onClicled(position: Int, from: String,id:String)
    }

}




