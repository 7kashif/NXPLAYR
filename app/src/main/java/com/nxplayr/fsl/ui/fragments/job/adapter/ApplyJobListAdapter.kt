package com.nxplayr.fsl.ui.fragments.job.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ApplyJobData
import com.nxplayr.fsl.data.model.JobListData
import com.nxplayr.fsl.databinding.ItemApplyjobListBinding
import com.nxplayr.fsl.databinding.ItemJobListBinding
import kotlinx.android.synthetic.main.item_job_list.view.*
import kotlinx.android.synthetic.main.item_notification_list.view.*

class ApplyJobListAdapter(val context: Activity,
                          var jobListData: ArrayList<ApplyJobData?>?,
                          val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemApplyjobListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.getContext()),
            R.layout.item_applyjob_list, parent, false
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

    class  JobListViewHolder(var binding: ItemApplyjobListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(follow_list: ApplyJobData?, adapterPosition: Int, onItemClick: OnItemClick) = with(itemView) {
            binding.joblist=follow_list
                img_more_option.visibility=View.GONE


            img_company_placeholder.setImageURI(RestClient.image_base_url_job+follow_list?.postdata!![0].userCompany!![0].userCompanyLogo)
            img_more_option.setOnClickListener {
                onItemClick.onClicled(adapterPosition,"threedots",img_more_option)
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, v: View)

    }

    fun setJobList(employees: java.util.ArrayList<ApplyJobData?>) {
        this.jobListData = employees
        notifyDataSetChanged()
    }
}