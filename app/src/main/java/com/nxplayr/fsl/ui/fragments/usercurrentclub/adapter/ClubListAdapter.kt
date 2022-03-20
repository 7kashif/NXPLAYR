package com.nxplayr.fsl.ui.fragments.usercurrentclub.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.ClubData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_layout_current_club.view.*

class ClubListAdapter(
    val context: Activity,
    val listData: ArrayList<ClubData>?
    , val onItemClick: OnItemClick, var userId: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_current_club, parent, false)
        return ClubListHolder(v, context)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ClubListHolder) {
            holder.bind(listData!![position], holder.adapterPosition, onItemClick,userId)

        }

    }

    class ClubListHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        var sessionManager: SessionManager? = null
        var userData: SignupData? = null

        init {
            sessionManager = SessionManager(context)
            userData = sessionManager!!.get_Authenticate_User()
        }

        fun bind(listData: ClubData,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 userId: String
        ) = with(itemView) {



            tv_selected_club.text = listData.clubName

//            if (!userData!!.clubs.isNullOrEmpty()) {
//                if (userData!!.clubs[0].clubName.equals(listData.clubName)){
//                    tv_selected_club.text = userData!!.clubs[0].clubName
//                }
//
//            }

            itemView.setOnClickListener {
                if(userId.equals(userData?.userID))
                {
                    onItemClick.onClicled(adapterPosition, "")

                }
            }

        }


    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}
