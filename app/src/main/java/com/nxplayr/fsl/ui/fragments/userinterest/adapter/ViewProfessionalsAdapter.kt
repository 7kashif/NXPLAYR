package com.nxplayr.fsl.ui.fragments.userinterest.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.layout_list_addinterests.view.*

class ViewProfessionalsAdapter(
        val context: Activity,

        var data: ArrayList<String>?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_professionals, parent, false)
        return ViewProfessionals(v,context)
    }

    override fun getItemCount(): Int {

        return data!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewProfessionals) {
            data?.get(position)?.let { holder.bind(it, holder.adapterPosition) }

        }


    }

    class ViewProfessionals(itemView : View, context: Activity) : RecyclerView.ViewHolder(itemView){

        fun bind(listprofessionals: String, position: Int) = with(itemView) {
            tv_followers_name.text=listprofessionals

        }




    }

}



