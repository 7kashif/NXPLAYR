package com.nxplayr.fsl.ui.fragments.userinterest.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CompanyListData
import com.nxplayr.fsl.data.model.HobbiesList
import com.nxplayr.fsl.data.model.Interest.DataItem
import com.nxplayr.fsl.data.model.ThreedotsBottomPojo
import com.nxplayr.fsl.ui.fragments.userhobbies.adapter.HobbiesListAdapter
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_skills_adapter.view.*
import kotlinx.android.synthetic.main.layout_list_addinterests.view.*
import kotlinx.android.synthetic.main.layout_list_addinterests.view.tv_followers_name
import kotlinx.android.synthetic.main.layout_list_professionals.view.*

class ViewProfessionalsAdapter(
    val context: Activity,
    var data: ArrayList<DataItem>?,
    val onItemClick: OnItemClick,
    var sessionManager: SessionManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_professionals, parent, false)
        return ViewProfessionals(v, context)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewProfessionals) {
            data?.get(position)
                ?.let { holder.bind(it, holder.adapterPosition, onItemClick, sessionManager) }
        }
    }

    class ViewProfessionals(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            company: DataItem,
            position: Int,
            onItemClick: OnItemClick,
            sessionManager: SessionManager
        ) = with(itemView) {
            tv_followers_name.text = company.userCompanyName
            if (sessionManager?.LanguageLabel != null)
                tv_followers_count.text = company.totalFollowers + " " + sessionManager?.LanguageLabel?.lngFollowers
            companyLogo.setImageURI(RestClient.image_base_url_job + company.userCompanyLogo)

            if (company.isYouFollowing.equals("Yes", false)) {
                action.setImageResource(R.drawable.following_icon)
            } else if (company.isYouFollowing.equals("No", false)) {
                action.setImageResource(R.drawable.follow_icon)
            }

            action.setOnClickListener {
                onItemClick.onClicked(company, "add_interest")
            }
        }

    }

    interface OnItemClick {
        fun onClicked(hobbiesData: DataItem, from: String)
    }
}



