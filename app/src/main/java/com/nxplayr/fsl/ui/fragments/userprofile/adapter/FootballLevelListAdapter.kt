package com.nxplayr.fsl.ui.fragments.userprofile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FootballLevelData
import kotlinx.android.synthetic.main.item_football_level_list.view.*


class FootballLevelListAdapter(
    val context: Activity,
    val list_FootballLevel: ArrayList<FootballLevelData>,
    val onItemClick: OnItemClick,
    var footballLevelId: String,
    var otheruserID: String?,
    var userID1: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(com.nxplayr.fsl.R.layout.item_football_level_list, parent, false)

        return FootballLevelSelectHolder(v, context)
    }

    override fun getItemCount(): Int {

        return list_FootballLevel!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is FootballLevelSelectHolder) {

//            holder.bind(list_FootballLevel!![position], holder.adapterPosition, onItemClick)

            var holder1 = holder as FootballLevelSelectHolder
            holder.bind(list_FootballLevel!![position]!!, holder.adapterPosition, onItemClick, footballLevelId)

            holder1.itemView.setOnClickListener {
                if(otheruserID.equals(userID1))
                {
                    onItemClick.onClicled(holder1.adapterPosition, "")
                    footballLevelId = list_FootballLevel[position].footbllevelID
                    notifyDataSetChanged()
                }

            }

        }
    }


    class FootballLevelSelectHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {


        var mSelection: Int = 0



        fun bind(list_footballLevel: FootballLevelData,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 footballLevelId: String) = with(itemView) {

            mSelection = adapterPosition

            tv_football_level.text = list_footballLevel.footbllevelName!!


            if ((list_footballLevel.isSelect)) {
                ll_layout.background = resources.getDrawable(R.drawable.rounded_corner_selected)
                tv_football_level.setTextColor(resources.getColor(R.color.black))
                tv_football_level.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_black, 0)
//                select_level.visibility = View.VISIBLE
            } else {
                ll_layout.background = resources.getDrawable(R.drawable.rounded_edittext)
                tv_football_level.setTextColor(resources.getColor(R.color.white))
                tv_football_level.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

//                select_level.visibility = View.GONE
            }

                if (footballLevelId.equals(list_footballLevel.footbllevelID, false)) {
                    ll_layout.background = resources.getDrawable(R.drawable.rounded_corner_selected)
                    tv_football_level.setTextColor(resources.getColor(R.color.black))
                    tv_football_level.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_black, 0)

//                    select_level.visibility = View.VISIBLE
                } else {
                    ll_layout.background = resources.getDrawable(R.drawable.rounded_edittext)
                    tv_football_level.setTextColor(resources.getColor(R.color.white))
//                    select_level.visibility = View.GONE
                    tv_football_level.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                }




        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }

}





