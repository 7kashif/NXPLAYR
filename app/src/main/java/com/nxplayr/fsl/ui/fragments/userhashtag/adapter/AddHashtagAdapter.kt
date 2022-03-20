package com.nxplayr.fsl.ui.fragments.userhashtag.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.Hashtags
import kotlinx.android.synthetic.main.layout_add_hashtag.view.*

class AddHashtagAdapter(
        val context: Activity,
        val listData: ArrayList<Hashtags?>?
        , val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var hashtagListFiltered: List<Hashtags?>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_add_hashtag, parent, false)
        return HashtagViewHolder(v)
    }

    override fun getItemCount(): Int {

        return hashtagListFiltered!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HashtagViewHolder) {
            holder.bind(hashtagListFiltered!![position]!!, holder.adapterPosition, onItemClick)

        }


    }

    class HashtagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(hashtagsList: Hashtags,
                 adapterPosition: Int,
                 onItemClick: OnItemClick
        ) = with(itemView) {


            tv_AddhashtagsName.text = hashtagsList.hashtagName

            itemView.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "", hashtagsList)

            }

        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                hashtagListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<Hashtags?> = ArrayList<Hashtags?>()
                    for (row in listData!!) {

                        if (row!!.hashtagName.toLowerCase().contains(charString.toLowerCase()) || row.hashtagName.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList

                }
                val filterResults = FilterResults()
                filterResults.values = hashtagListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                hashtagListFiltered = filterResults.values as ArrayList<Hashtags?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, hashtagsList: Hashtags)
    }

    init {
        hashtagListFiltered = listData!!
    }


}
