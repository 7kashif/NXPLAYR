package com.nxplayr.fsl.ui.activity.onboarding.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FootballAgeGroupListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_select_age_group.view.*

@Suppress("DEPRECATION")
class AgeGroupAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<FootballAgeGroupListData>,
    var selectModeType1: Int = 0

) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>(), SectionIndexer {

    var selectedIndex = -1
    var intent: Intent? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_age_group, parent, false)
            return AgeGroupHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is AgeGroupHolder) {

            holder.bind(
                listData.get(position),
                holder.adapterPosition,
                onItemClick,
                selectModeType1
            )

        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (listData[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    class AgeGroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectModeType1: Int = 0
        var intent: Intent? = null
        var colorId: Int? = null

        fun bind(
            ageListData: FootballAgeGroupListData?,
            adapterPosition: Int,
            onitemClick: OnItemClick,
            selectModeType1: Int) =

            with(itemView) {
                age_select_item.text =
                    ageListData?.agegroupFrom.toString() + "-" + ageListData?.agegroupTo

                if (selectModeType1 == 0) {
                    if (ageListData?.status1!!) {
                        age_select_item.setBackgroundResource(R.drawable.circle_fill_blue)
                        age_select_item.setTextColor(resources.getColor(R.color.black))
                    } else {
                        age_select_item.setBackgroundResource(R.drawable.circle_border_blue)
                        age_select_item.setTextColor(resources.getColor(R.color.white))
                    }
                }
                if (selectModeType1 == 1) {
                    if (ageListData!!.status1!!) {
                        age_select_item.setBackgroundResource(R.drawable.circle_border_yellow_light)
                        age_select_item.setTextColor(resources.getColor(R.color.black))
                    } else {
                        age_select_item.setBackgroundResource(R.drawable.circle_border_yellow)
                        age_select_item.setTextColor(resources.getColor(R.color.white))
                    }
                }

                if (selectModeType1 == 2) {
                    if (ageListData!!.status1) {
                        age_select_item.setBackgroundResource(R.drawable.circle_border_accent_light)
                        age_select_item.setTextColor(resources.getColor(R.color.black))
                    } else {
                        age_select_item.setBackgroundResource(R.drawable.circle_border_accent)
                        age_select_item.setTextColor(resources.getColor(R.color.white))
                    }
                }
                age_select_item.tag = adapterPosition
                age_select_item.setOnClickListener {
                    onitemClick.onClicklisneter(it.tag as Int, "")
                }
            }
    }

    override fun getSections(): Array<Any>? {
        return null
    }

    override fun getSectionForPosition(position: Int): Int {
        return 0
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        if (sectionIndex == 35) {
            return 0
        }
        for (i in 0 until listData.size) {
//            val l = listData[i]
            /*  val firstChar = l.toUpperCase().get(0)
              if (firstChar.toInt() == sectionIndex) {
                  return i
              }*/
        }
        return -1
    }


    interface OnItemClick {
        fun onClicklisneter(position: Int, name: String)
    }
}