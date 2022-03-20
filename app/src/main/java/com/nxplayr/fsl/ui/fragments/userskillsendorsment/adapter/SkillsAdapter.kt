package com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SkillList
import kotlinx.android.synthetic.main.item_skills_adapter.view.*

class SkillsAdapter(
        val context: Activity,
        val listData: ArrayList<SkillList>?
        , val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var skillListFiltered: List<SkillList>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skills_adapter, parent, false)
        return SkillsHolder(v)
    }

    override fun getItemCount(): Int {

        return skillListFiltered!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SkillsHolder) {
            holder.bind(skillListFiltered!![position], holder.adapterPosition, onItemClick)

        }


    }

    class SkillsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listData: SkillList,
                 adapterPosition: Int,
                 onItemClick: OnItemClick
        ) = with(itemView) {

            icon_AddSkills.visibility = View.VISIBLE
            layout_skill.visibility = View.VISIBLE
            if (listData != null) {

                tv_skill.text = listData.skillName
            }


//            itemView.setOnClickListener {
//                onItemClick.onClicled(adapterPosition, "")
//            }
            icon_AddSkills.setOnClickListener {
                onItemClick.onClicled(listData, "add_skills")
            }

        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                skillListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<SkillList> = ArrayList<SkillList>()
                    for (row in listData!!) {

                        if (row.skillName.toLowerCase().contains(charString.toLowerCase()) || row.skillName.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList


                }
                val filterResults = FilterResults()
                filterResults.values = skillListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                skillListFiltered = filterResults.values as ArrayList<SkillList>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(skillData: SkillList, from: String)
    }

    init {
        skillListFiltered = listData!!
    }

}
