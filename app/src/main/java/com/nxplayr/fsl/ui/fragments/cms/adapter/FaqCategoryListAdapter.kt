package com.nxplayr.fsl.ui.fragments.cms.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FaqCategoryData
import kotlinx.android.synthetic.main.item_layout_faq_category.view.*

class FaqCategoryListAdapter(
        val context: Activity,
        val listData: ArrayList<FaqCategoryData>?
        , val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_faq_category, parent, false)
        return ViewHolder(v, context)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            holder.bind(listData!![position], holder.adapterPosition, onItemClick)

        }


    }

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {


        fun bind(listData: FaqCategoryData,
                 adapterPosition: Int,
                 onItemClick: OnItemClick
        ) = with(itemView) {

            tv_faqCateName.text = listData.faqcategoryName
            if (listData.faqcategoryName.equals("Chat sections")) {
                img_faqcategory.setImageResource((R.drawable.faq_icon2))
            } else if (listData.faqcategoryName.equals("Post Section")) {
                img_faqcategory.setImageResource((R.drawable.faq_icon1))
            }


            itemView.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "")
            }

        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}
