package com.nxplayr.fsl.ui.fragments.cms.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FaqData
import kotlinx.android.synthetic.main.item_layout_faq.view.*

class FaqAdapter(
        val context: Activity,
        val listData: ArrayList<FaqData>?
        , val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_faq, parent, false)
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


        fun bind(listData: FaqData,
                 adapterPosition: Int,
                 onItemClick: OnItemClick
        ) = with(itemView) {

            tv_faqQuestion.text = listData.faqQuestion
            tv_question.text = listData.faqQuestion
            tv_faqAnswer.text = listData.faqAnswer


            icon_downQue.setOnClickListener {
                ll_expanded_layout.visibility = View.VISIBLE
            }
            icon_upanswer.setOnClickListener {
                ll_expanded_layout.visibility = View.GONE
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
