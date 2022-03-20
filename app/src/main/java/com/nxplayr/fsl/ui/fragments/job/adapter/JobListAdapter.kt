package com.nxplayr.fsl.ui.fragments.job.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.JobListData
import com.nxplayr.fsl.databinding.ItemJobListBinding
import kotlinx.android.synthetic.main.item_job_list.view.*
import kotlinx.android.synthetic.main.item_notification_list.view.*

class JobListAdapter(val context: Activity,
                     var jobListData: ArrayList<JobListData?>?,
                     val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemJobListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.getContext()),
            R.layout.item_job_list, parent, false
        )

        return JobListViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return jobListData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is JobListViewHolder) {
            holder.bind(jobListData!![position], holder.adapterPosition, onItemClick)
        }
    }

    class  JobListViewHolder(var binding: ItemJobListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(follow_list: JobListData?, adapterPosition: Int, onItemClick: OnItemClick) = with(itemView) {
            binding.joblist=follow_list
            if(follow_list?.isYouApplied.equals("Yes")){
                img_more_option.visibility=View.GONE

            } else{
                img_more_option.visibility=View.VISIBLE
            }
            img_company_placeholder.setImageURI(RestClient.image_base_url_job+follow_list?.userCompany!![0].userCompanyLogo)
            img_more_option.setOnClickListener {
                onItemClick.onClicled(adapterPosition,"threedots",img_more_option)
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, v: View)

    }

    fun setJobList(employees: java.util.ArrayList<JobListData?>) {
        this.jobListData = employees
        notifyDataSetChanged()
    }
}