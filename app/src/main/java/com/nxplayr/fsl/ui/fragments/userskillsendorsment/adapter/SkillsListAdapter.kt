package com.nxplayr.fsl.ui.fragments.userskillsendorsment.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.UsersSkils
import kotlinx.android.synthetic.main.item_skills_adapter.view.*

class SkillsListAdapter(
    val context: Activity,
    val listData: ArrayList<UsersSkils?>?
    , val onItemClick: OnItemClick,
    var otheruserId: String,
    var userID: String?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var skillListFiltered: List<UsersSkils?>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skills_adapter, parent, false)
        return SkillsHolder(v)
    }

    override fun getItemCount(): Int {

        return skillListFiltered!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SkillsHolder) {
            holder.bind(skillListFiltered!![position]!!, holder.adapterPosition, onItemClick,otheruserId,userID)

        }
    }

    class SkillsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listData: UsersSkils,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 otheruserId: String,
                 userID: String?) = with(itemView) {

            icon_close.visibility = View.VISIBLE
            layout_skill.visibility = View.VISIBLE
            tv_skill.text = listData.skillName


            icon_close.setOnClickListener {
                if(otheruserId.equals(userID)){
                    onItemClick.onClicled(adapterPosition, "delete_skill",listData)
                }
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
                    val filteredList: MutableList<UsersSkils?> = ArrayList<UsersSkils?>()
                    for (row in listData!!) {

                        if (row!!.skillName.toLowerCase().contains(charString.toLowerCase()) || row!!.skillName.contains(charSequence)) {
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
                skillListFiltered = filterResults.values as ArrayList<UsersSkils?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String,skillData: UsersSkils)
    }

    init {
        skillListFiltered = listData!!
    }


}
