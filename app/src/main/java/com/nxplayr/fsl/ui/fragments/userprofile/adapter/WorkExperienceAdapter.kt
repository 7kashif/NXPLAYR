package com.nxplayr.fsl.ui.fragments.userprofile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.EmploymentData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_work_experince_adapter_list.view.*


class WorkExperienceAdapter(
    val context: Activity,
    val employementlistData: ArrayList<EmploymentData?>?
    , val onItemClick: OnItemClick, val from: String, var userId: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_work_experince_adapter_list, parent, false)
        return WorkExpperienceViewHolder(v, context)
    }

    override fun getItemCount(): Int {

        return employementlistData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is WorkExpperienceViewHolder) {
            holder.bind(employementlistData!![position]!!, holder.adapterPosition, onItemClick, from,userId)

        }


    }

    class WorkExpperienceViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var toDate = ""
        var formDate = ""
        var sessionManager:SessionManager?=null
        init {
            sessionManager= SessionManager(context)

        }
        fun bind(employementlistData: EmploymentData,
                 position: Int,
                 onItemClick: OnItemClick,
                 from: String,
                 userId: String) = with(itemView) {


            if (from.equals("emp_profile")) {
                delete_exp.visibility = View.GONE
                if (position == 0 || position == 1) {
                    ll_mainWorkExp.visibility = View.VISIBLE
                    tv_graphics_designer.text = employementlistData.jobfuncName
                    tv_company_name.text = employementlistData.companyName
                    tv_currentWork_location.text = employementlistData.cityName
                    try {
                        formDate = MyUtils.formatDate(employementlistData.useremployementPeriodOfTimeFrom, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")

                        if (!employementlistData.useremployementPeriodOfTimeTo.isNullOrEmpty()) {
                            toDate = MyUtils.formatDate(employementlistData.useremployementPeriodOfTimeTo, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")
                            tv_date.text = formDate + " - " + toDate
                        } else {
                            tv_date.text = formDate
                        }
                    } catch (e: Exception) {

                    }
                } else {
                    ll_mainWorkExp.visibility = View.GONE

                }
            } else if (from.equals("emp_list")) {
                delete_exp.visibility = View.VISIBLE

                tv_graphics_designer.text = employementlistData.jobfuncName
                tv_company_name.text = employementlistData.companyName
                tv_currentWork_location.text = employementlistData.cityName
                try {
                    formDate = MyUtils.formatDate(employementlistData.useremployementPeriodOfTimeFrom, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")

                    if (!employementlistData.useremployementPeriodOfTimeTo.isNullOrEmpty()) {
                        toDate = MyUtils.formatDate(employementlistData.useremployementPeriodOfTimeTo, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")
                        tv_date.text = formDate + " - " + toDate
                    } else {
                        tv_date.text = formDate
                    }
                } catch (e: Exception) {

                }
            }



            delete_exp.setOnClickListener {
                if(userId.equals(sessionManager?.get_Authenticate_User()?.userID)){
                    onItemClick.onClicled(position, "delete", delete_exp, employementlistData)
                }
            }


        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, v: View, empData: EmploymentData?)
    }

}
