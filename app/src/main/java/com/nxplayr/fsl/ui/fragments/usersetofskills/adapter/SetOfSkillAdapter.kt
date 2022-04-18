package com.nxplayr.fsl.ui.fragments.usersetofskills.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UsersSkils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_current_club_adapter.view.*

class SetOfSkillAdapter(
    val context: Activity,
    val listData: ArrayList<UsersSkils>?, val onItemClick: OnItemClick,
    var userId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var clubListFiltered: List<UsersSkils>

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
            holder.bind(clubListFiltered!![position], holder.adapterPosition, onItemClick, userId)
        }

    }

    class CurrentClubViewHolder(itemView: View, context: Activity) :
        RecyclerView.ViewHolder(itemView) {

        var sessionManager: SessionManager? = null
        var userData: SignupData? = null

        init {
            sessionManager = SessionManager(context)
        }

        fun bind(
            listData: UsersSkils,
            adapterPosition: Int,
            onItemClick: OnItemClick, userId: String
        ) = with(itemView) {

            userData = sessionManager!!.get_Authenticate_User()

            tv_club_name.text = listData.skillName
            if (!userId.equals(userData?.userID, false)) {
                icon_Addclub.visibility = View.GONE
            } else {
                icon_Addclub.visibility = View.VISIBLE
            }
            icon_Addclub.setOnClickListener {
                onItemClick.onClicled(listData, adapterPosition)
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
                    val filteredList: MutableList<UsersSkils> = ArrayList<UsersSkils>()
                    for (row in listData!!) {

                        if (row.skillName.toLowerCase()
                                .contains(charString.toLowerCase()) || row.skillName.contains(
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
                clubListFiltered = filterResults.values as ArrayList<UsersSkils>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(clubData: UsersSkils?, position: Int)
    }

    init {
        clubListFiltered = listData!!
    }

}
