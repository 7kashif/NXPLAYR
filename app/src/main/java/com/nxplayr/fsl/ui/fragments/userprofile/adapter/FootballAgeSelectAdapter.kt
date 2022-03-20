package com.nxplayr.fsl.ui.fragments.userprofile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.AgecategoryList
import kotlinx.android.synthetic.main.item_select_football_age.view.*
import java.util.*


class FootballAgeSelectAdapter(
    val context: Activity,
    val list_language: ArrayList<AgecategoryList>?,
    val onItemClick: OnItemClick,
    var footballagecatId: String,
    var otheruserId: String,
    var userId:String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(com.nxplayr.fsl.R.layout.item_select_football_age, parent, false)

        return FootballAgeSelectHolder(v, context)
    }

    override fun getItemCount(): Int {

        return list_language!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is FootballAgeSelectHolder) {

            var holder1 = holder as FootballAgeSelectHolder
            holder.bind(list_language!![position], holder.adapterPosition, onItemClick, footballagecatId)

            holder1.btn_football_age.setOnClickListener {
                if(userId.equals(otheruserId))
                {
                    onItemClick.onClicled(holder1.adapterPosition, "")
                    footballagecatId = list_language[position].footballagecatID
                    notifyDataSetChanged()
                }
            }
        }

    }


    class FootballAgeSelectHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var mSelection: Int = 0
        var whitespaceCount = 0


        var btn_football_age: TextView = itemView.findViewById(R.id.btn_football_age)


        fun bind(list_language: AgecategoryList,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 footballagecatId: String?) = with(itemView) {



            mSelection = adapterPosition

            btn_football_age.text = list_language.footballagecatName

            if ((list_language.isSelect!!)) {
                btn_football_age.setBackgroundResource(R.drawable.circle_fill_blue)
                btn_football_age.setTextColor(resources.getColor(R.color.black))
            } else {
                btn_football_age.setBackgroundResource(R.drawable.circle_border_blue)
                btn_football_age.setTextColor(resources.getColor(R.color.white))
            }

                if (footballagecatId.equals(list_language.footballagecatID, false)) {
                    btn_football_age.setBackgroundResource(R.drawable.circle_fill_blue)
                    btn_football_age.setTextColor(resources.getColor(R.color.black))
                } else {
                    btn_football_age.setBackgroundResource(R.drawable.circle_border_blue)
                    btn_football_age.setTextColor(resources.getColor(R.color.white))
                }

                /*btn_football_age.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "")
                }*/

//            val s = list_language.footballagecatName
//            var result = s.split(" ").map { it.trim() }
//            result.forEach { btn_football_age.setText(it) }


        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }


}





