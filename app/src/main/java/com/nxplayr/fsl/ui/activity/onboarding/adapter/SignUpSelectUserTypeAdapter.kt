package com.nxplayr.fsl.ui.activity.onboarding.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FootballTypeListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_signup_select_user_type_adapter.view.*

@Suppress("DEPRECATION")
class SignUpSelectUserTypeAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<FootballTypeListData>?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_signup_select_user_type_adapter, parent, false)
            return ViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            holder.bind(listData!!.get(position), holder.adapterPosition, onItemClick)
        }
    }

    override fun getItemCount(): Int {
        return listData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var colorId: Int? = null
        fun bind(
            selectTypeList: FootballTypeListData,
            position: Int,
            onitemClick: OnItemClick
        ) =
                with(itemView) {

                    tv_usertype_name.text = selectTypeList.appuroleName
//
                    if (selectTypeList.checked!!) {

                        when (position) {
                            0 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_selected_icon1))
                            }
                            1 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_selected_icon2))
                            }
                            2 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_selected_icon3))
                            }
                            3 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_selected_icon4))
                            }
                        }


                        ll_userType.setBackgroundResource(R.drawable.rect_fill_blue)
                        tv_usertype_name.setTextColor(context.resources.getColor(R.color.black))
                        feedbackIconImageview.imageTintList = (ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)))
                    } else {

                        when (position) {
                            0 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_unselected_icon1))
                            }
                            1 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_unselected_icon2))
                            }
                            2 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_unselected_icon3))
                            }
                            3 -> {
                                feedbackIconImageview.setImageDrawable(resources.getDrawable(R.drawable.profile_category_unselected_icon4))
                            }
                        }

                        ll_userType.setBackgroundResource(R.drawable.rounded_edittext_primary)
                        tv_usertype_name.setTextColor(context.resources.getColor(R.color.colorPrimary))
                        feedbackIconImageview.imageTintList = (ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)))

                    }

                    itemProfileLL.tag = position
                    itemProfileLL.setOnClickListener { onitemClick.onClicklisneter(it.tag as Int, "") }

                }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)
    }
}