package com.nxplayr.fsl.ui.fragments.userhobbies.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.HobbiesList
import kotlinx.android.synthetic.main.item_skills_adapter.view.*

class AddHobbiesListAdapter(
        val context: Activity,
        val listData: ArrayList<HobbiesList?>?
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
            holder.bind(listData!![position]!!, holder.adapterPosition, onItemClick, position)

        }


    }

    class SkillsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listData: HobbiesList,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 position: Int) = with(itemView) {

            icon_close.visibility = View.VISIBLE
            layout_skill.visibility = View.VISIBLE
            tv_skill.text = listData.hobbyName


//            itemView.setOnClickListener {
//                onItemClick.onClicled(adapterPosition, "")
//            }

            icon_close.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "remove_hobbies")
            }

        }


    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}
