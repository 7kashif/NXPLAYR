package com.nxplayr.fsl.ui.fragments.userhobbies.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.HobbiesList
import kotlinx.android.synthetic.main.item_skills_adapter.view.*

class HobbiesListAdapter(
        val context: Activity,
        val listData: ArrayList<HobbiesList?>?
        , val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var hobbiesListFiltered: List<HobbiesList?>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skills_adapter, parent, false)
        return SkillsHolder(v)
    }

    override fun getItemCount(): Int {

        return hobbiesListFiltered!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SkillsHolder) {
            holder.bind(hobbiesListFiltered!![position]!!, holder.adapterPosition, onItemClick)

        }


    }

    class SkillsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listData: HobbiesList,
                 adapterPosition: Int,
                 onItemClick: OnItemClick
        ) = with(itemView) {

            icon_AddSkills.visibility = View.VISIBLE
            layout_skill.visibility = View.VISIBLE
            tv_skill.text = listData.hobbyName



            icon_AddSkills.setOnClickListener {
                onItemClick.onClicled(listData, "add_hobbies")
            }

        }


    }


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                hobbiesListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<HobbiesList?> = ArrayList<HobbiesList?>()
                    for (row in listData!!) {

                        if (row!!.hobbyName.toLowerCase().contains(charString.toLowerCase()) || row!!.hobbyName.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList


                }
                val filterResults = FilterResults()
                filterResults.values = hobbiesListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                hobbiesListFiltered = filterResults.values as ArrayList<HobbiesList?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(hobbiesData: HobbiesList, from: String)
    }

    init {
        hobbiesListFiltered = listData!!
    }


}
