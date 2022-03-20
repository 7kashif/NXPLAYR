package com.nxplayr.fsl.ui.fragments.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.list_item_layout.view.*


class BottomSheetListFragment : BottomSheetDialogFragment() {

    var mListener: SelectLanguage? = null

    var bottomSheetListAdapter: BottomSheetListAdapter? = null
    var mContext: Activity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var list = ArrayList<String>()
    var from: String = ""
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    fun setOnclickLisner(mListener: SelectLanguage) {
        this.mListener = mListener
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if (arguments != null) {
            list = arguments!!.get("data") as ArrayList<String>
            from = arguments!!.getString("from")!!
        }

        linearLayoutManager = LinearLayoutManager(mContext)
        bottomSheetListAdapter = BottomSheetListAdapter(mContext!!, from, list, object : BottomSheetListAdapter.OnItemClick {
            override fun onClicled(value: String, from: String) {
                mListener!!.onLanguageSelect(value, from)
                dismiss()
            }

        })
        rv_bottomSheet.layoutManager = linearLayoutManager
        rv_bottomSheet.adapter = bottomSheetListAdapter
        rv_bottomSheet.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))

        bottomSheetListAdapter?.notifyDataSetChanged()
    }

    interface SelectLanguage {

        fun onLanguageSelect(value: String, from: String)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Activity
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as SelectLanguage
        } else {
            mContext as SelectLanguage
        }
    }


    class BottomSheetListAdapter(internal var mContext: Activity, from: String, data: List<String>, val onItemClick: OnItemClick) :
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
            holder.bind(data[position]!!)

            holder.itemView.setOnClickListener {
                if (onItemClick != null)
                    onItemClick.onClicled(data[position]!!, from)
            }

        }

        override fun getItemCount(): Int {
            return data.size
        }

        interface OnItemClick {
            fun onClicled(value: String, from: String)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(name: String) = with(itemView) {
                itemNameTv.text = name.toString().capitalize()
            }
        }
    }

}
