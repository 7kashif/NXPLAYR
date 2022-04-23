package com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SkillList
import com.nxplayr.fsl.data.model.UsersSkils
import kotlinx.android.synthetic.main.item_skills_adapter.view.*

class AddSkillsAdapter(
        val context: Activity,
        val listData: ArrayList<UsersSkils>?
        , val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skills_adapter, parent, false)
        return SkillsHolder(v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SkillsHolder) {
            holder.bind(listData!![position], holder.adapterPosition, onItemClick)

        }


    }

    class SkillsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listData: UsersSkils,
                 adapterPosition: Int,
                 onItemClick: OnItemClick
        ) = with(itemView) {

            icon_close.visibility = View.VISIBLE
            layout_skill.visibility = View.VISIBLE
            if (listData != null) {
                tv_skill.text = listData.skillName
            }

            itemView.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "")
            }

            icon_close.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "remove_skill")
            }
        }


    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}
