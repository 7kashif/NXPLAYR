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
import com.nxplayr.fsl.data.model.CreatePostPrivacyPojo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_privacy_bottom_sheet.*
import kotlinx.android.synthetic.main.privacy_list_item.view.*

class PrivacyBottomSheetFragment : BottomSheetDialogFragment() {

    var mListener : selectPrivacy? =null
    var bottomSheetListAdapter: BottomSheetListAdapter? = null
    var mContext: Activity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var list = ArrayList<CreatePostPrivacyPojo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_privacy_bottom_sheet, container, false)
    }

    fun setOnclickLisner(mListener: selectPrivacy) {
        this.mListener = mListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if (arguments != null) {
            list = arguments!!.get("data") as ArrayList<CreatePostPrivacyPojo>
        }

        linearLayoutManager = LinearLayoutManager(mContext)
        bottomSheetListAdapter =
                BottomSheetListAdapter(mContext!!, list, object : BottomSheetListAdapter.OnItemClick {
                    override fun onClicked(data: ArrayList<CreatePostPrivacyPojo>, position: Int ) {
                        mListener!!.setPrivacy(data[position],position)
                        dismiss()
                    }

                })
        bottomSheetRv.layoutManager = linearLayoutManager
        bottomSheetRv.adapter = bottomSheetListAdapter



    }

    interface selectPrivacy {

        fun setPrivacy(data: CreatePostPrivacyPojo, position: Int)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Activity
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as selectPrivacy
        } else {
            mContext as selectPrivacy
        }

    }


class BottomSheetListAdapter(
    internal var mContext: Activity,
    data: ArrayList<CreatePostPrivacyPojo>,
    val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<BottomSheetListAdapter.ViewHolder>() {
    internal var data: ArrayList<CreatePostPrivacyPojo> = java.util.ArrayList()


    init {
        this.data = data

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.privacy_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data,position)

        holder.itemView.setOnClickListener {
            onItemClick.onClicked(data,position)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClick {
        fun onClicked(data: ArrayList<CreatePostPrivacyPojo>, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ArrayList<CreatePostPrivacyPojo>, position: Int) = with(itemView) {
            privacyTextView.text = data[position].name_Privacy
            privacyImageView.setImageResource(data[position].image_Privacy)
        }
    }
}}