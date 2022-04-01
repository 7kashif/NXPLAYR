package com.nxplayr.fsl.ui.activity.onboarding.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.UserRoleListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_signup_select_football_type_adapter.view.*


@Suppress("DEPRECATION")
class UserRoleListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<UserRoleListData>?,
    var selectModeType1: Int = 0

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_signup_select_football_type_adapter, parent, false)
            return ViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
//            holder.img_selectMode.setImageURI(listData!![position].apputypeImage)
            holder.bind(listData!![position], holder.adapterPosition, onItemClick)
        }
    }

    override fun getItemCount(): Int {
        return listData!!.size - 1
    }


//    override fun getItemViewType(position: Int): Int {
//        return if (listData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //        var img_selectMode: ImageView = itemView.findViewById(R.id.img_selectMode) as ImageView
        var colorId = R.color.colorPrimary
        var selectModeType: Int = 0

        fun bind(
            selectTypeList: UserRoleListData,
            position: Int,
            onitemClick: OnItemClick
        ) =
                with(itemView) {

                    tv_football_type.text = selectTypeList.apputypeName
                    if (selectTypeList.checked!!) {

                        when (position) {
                            0 -> {
                                colorId = R.color.colorPrimary
                                img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.mode_selected_talent))
                            }
                            1 -> {
                                colorId = R.color.yellow_modes
                                img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.mode_selected_business_network))
//                                img_selectMode.imageTintList = (ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow_modes)))
//                                val sampleDrawable: Drawable = context.resources.getDrawable(R.drawable.mode_selected_marketplace)
//                                sampleDrawable.setColorFilter(PorterDuffColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY))
//                                img_selectMode.background.setColorFilter((resources.getColor(R.color.yellow_modes)),PorterDuff.Mode.SRC_OVER)
                            }
                            2 -> {
                                colorId = R.color.colorAccent
                                img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.mode_selected_marketplace))
//                                img_selectMode.imageTintList = (ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)))
                            }
                        }

//                        img_selectMode.setColorFilter(ContextCompat.getColor(context, colorId!!), android.graphics.PorterDuff.Mode.SRC_IN)
                        MyUtils.setSelectedModeTypeViewColor(context, arrayListOf(tv_football_type), colorId!!)

                    } else {

                        when (position) {
                            0 -> {
                                colorId = R.color.colorPrimary
                                img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.mode_unselected_talent))

                            }
                            1 -> {
//                                val text = "<font color=#FFF38D>First Color</font> <font color=#F6CF4C>Second Color</font>"
//                                tv_football_type.setText(Html.fromHtml(text))
                                colorId = R.color.yellow_modes
//                                var mDrawable = resources.getDrawable(R.drawable.mode_unselected_business_network)
//                                mDrawable.setColorFilter(ContextCompat.getColor(context, colorId!!), android.graphics.PorterDuff.Mode.SRC_IN)
//                                img_selectMode.setImageDrawable(mDrawable)
                                img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.mode_unselected_business_network))
                            }
                            2 -> {
                                colorId = R.color.colorAccent
//                                var mDrawable = resources.getDrawable(R.drawable.mode_unselected_marketplace)
//                                mDrawable.setColorFilter(ContextCompat.getColor(context, colorId!!), android.graphics.PorterDuff.Mode.SRC_IN)
//                                img_selectMode.setImageDrawable(mDrawable)
                                img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.mode_unselected_marketplace))
                            }
                        }

//                        img_selectMode.imageTintList = (ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)))
//                        img_selectMode.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN)
                        tv_football_type.setTextColor(context.resources.getColor(colorId))
                    }


                    ll_user_type.setOnClickListener { onitemClick.onClicklisneter(position, "selectModeType") }


                }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)
    }
}