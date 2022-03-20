package com.nxplayr.fsl.ui.fragments.usereducation.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.EducationData
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.item_education_adapter.view.*


class EducationAdapter(
    val context: Activity,
    val listData: ArrayList<EducationData?>?
    , val onItemClick: OnItemClick, val from: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_education_adapter, parent, false)
        return EducationViewHolder(v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is EducationViewHolder) {
            holder.bind(listData!![position]!!, holder.adapterPosition, onItemClick, from)

        }


    }

    class EducationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var toDate = ""
        fun bind(listData: EducationData,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 from: String) = with(itemView) {


            if (from.equals("edu_profile")) {
                delete_edu.visibility = View.GONE
                if (adapterPosition == 0 || adapterPosition == 1) {
                    ll_main_education.visibility = View.VISIBLE
                    tv_degree_name.text = listData.degreeName
                    tv_college_name.text = listData.universityName

                    try {
                        var formDate = MyUtils.formatDate(listData.usereducationPeriodOfTimeFrom, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")
                        if (!listData.usereducationPeriodOfTimeTo.isNullOrEmpty()) {
                            toDate = MyUtils.formatDate(listData.usereducationPeriodOfTimeTo, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")
                            tv_from_date.text = formDate + " - " + toDate
                        } else {
                            tv_from_date.text = formDate
                        }
                    } catch (e: Exception) {

                    }
                    tv_current_location.text = listData.usereducationGrade + " Grade"
                } else {
                    ll_main_education.visibility = View.GONE
                }

            } else if (from.equals("education_list")) {
                delete_edu.visibility = View.VISIBLE
                tv_degree_name.text = listData.degreeName
                tv_college_name.text = listData.universityName

                try {
                    var formDate = MyUtils.formatDate(listData.usereducationPeriodOfTimeFrom, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")
                    if (!listData.usereducationPeriodOfTimeTo.isNullOrEmpty()) {
                        toDate = MyUtils.formatDate(listData.usereducationPeriodOfTimeTo, "dd-MM-yyyy hh:mm:ss", "MMM yyyy")
                        tv_from_date.text = formDate + " - " + toDate
                    } else {
                        tv_from_date.text = formDate
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                tv_current_location.text = listData.usereducationGrade + " Grade"


            }

            delete_edu.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "delete", delete_edu)
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, v: View)
    }

}
