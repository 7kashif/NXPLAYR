package com.nxplayr.fsl.ui.fragments.usersetofskills.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UsersSkils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_current_club_adapter.view.*
import kotlinx.android.synthetic.main.item_layout_current_club.view.*
import kotlinx.android.synthetic.main.item_layout_current_club.view.tv_add

class SkillListAdapter(
    val context: Activity,
    val listData: ArrayList<UsersSkils>?, val onItemClick: OnItemClick, var userId: String

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_current_club, parent, false)
        return ClubListHolder(v, context)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ClubListHolder) {
            holder.bind(listData!![position], holder.adapterPosition, onItemClick, userId)

        }

    }

    class ClubListHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

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

            tv_selected_club.text = listData.skillName
            if (!userId.equals(userData?.userID, false)) {
                icon_closeClub.visibility = View.GONE
            } else {
                icon_closeClub.visibility = View.VISIBLE
            }
            icon_closeClub.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "")
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}
