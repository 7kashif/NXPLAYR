package com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.ClubListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_current_club_adapter.view.*

class SuggestedClubAdapter(
    val context: Activity,
    val listData: ArrayList<ClubListData>?, val onItemClick: OnItemClick,
    var userId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var clubListFiltered: List<ClubListData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_current_club_adapter, parent, false)
        return CurrentClubViewHolder(v, context)
    }

    override fun getItemCount(): Int {
        return clubListFiltered!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CurrentClubViewHolder) {
            holder.bind(clubListFiltered[position], holder.adapterPosition, onItemClick, userId)
        }
    }

    class CurrentClubViewHolder(itemView: View, context: Activity) :
        RecyclerView.ViewHolder(itemView) {

        var sessionManager: SessionManager? = null
        var userData: SignupData? = null

        init {
            sessionManager = SessionManager(context)
            userData = sessionManager!!.get_Authenticate_User()

        }

        fun bind(
            listData: ClubListData,
            adapterPosition: Int,
            onItemClick: OnItemClick,
            userId: String
        ) = with(itemView) {

            tv_club_name.text = listData.clubName.trim()
            if (userId == userData?.userID) {
                if (listData.selected) {
//                    item_club_main.visibility = View.GONE
                    icon_Addclub.visibility = View.GONE
                    icon_close.visibility = View.VISIBLE
                } else {
//                    item_club_main.visibility = View.VISIBLE
                    icon_Addclub.visibility = View.VISIBLE
                    icon_close.visibility = View.GONE
                }
            } else {
                icon_Addclub.visibility = View.GONE
            }
            icon_Addclub.setOnClickListener {
                if (userId == userData?.userID) {
                    listData.selected = true
                    onItemClick.onClicled(listData, adapterPosition)
                }
            }
            icon_close.setOnClickListener {
                if (userId == userData?.userID) {
                    listData.selected = false
                    onItemClick.onClicled(listData, adapterPosition)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                clubListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<ClubListData> = ArrayList<ClubListData>()
                    for (row in listData!!) {

                        if (row.clubName.toLowerCase()
                                .contains(charString.toLowerCase()) || row.clubName.contains(
                                charSequence
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList

                }
                val filterResults = FilterResults()
                filterResults.values = clubListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                clubListFiltered = filterResults.values as ArrayList<ClubListData>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(clubData: ClubListData?, position: Int)
    }

    init {
        clubListFiltered = listData!!
    }

}
