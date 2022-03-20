package com.nxplayr.fsl.ui.fragments.userhobbies.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.Hobby
import kotlinx.android.synthetic.main.item_skills_adapter.view.*

class HobbiesAdapter(
    val context: Activity,
    val listData: ArrayList<Hobby?>?
    , val onItemClick: OnItemClick, var otheruserId:String, var userID:String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var hobbiesListFiltered: List<Hobby?>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skills_adapter, parent, false)
        return HobbiesHolder(v)
    }

    override fun getItemCount(): Int {

        return hobbiesListFiltered!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HobbiesHolder) {
            holder.bind(hobbiesListFiltered!![position]!!, holder.adapterPosition, onItemClick,otheruserId,userID)
        }
    }

    class HobbiesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listData: Hobby,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 otheruserId: String,
                 userID: String) = with(itemView) {

            layout_skill.visibility = View.VISIBLE
            tv_skill.text = listData.hobbyName

            if(otheruserId.equals(userID)) {
                icon_close.visibility = View.VISIBLE
            }else{
                icon_close.visibility = View.GONE

            }
            icon_close.setOnClickListener {
                if(otheruserId.equals(userID)) {
                onItemClick.onClicled(adapterPosition, "delete_hobbies",listData)
                }
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
                    val filteredList: MutableList<Hobby?> = ArrayList<Hobby?>()
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
                hobbiesListFiltered = filterResults.values as ArrayList<Hobby?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String,hobbiesData: Hobby)
    }

    init {
        hobbiesListFiltered = listData!!
    }
}
