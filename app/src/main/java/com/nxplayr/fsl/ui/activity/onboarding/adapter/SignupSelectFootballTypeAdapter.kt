package com.nxplayr.fsl.ui.activity.onboarding.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FootballLevelListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_select_football_type_adapter.view.*
import kotlinx.android.synthetic.main.item_signup_select_football_type_adapter.view.tv_football_type

class SignupSelectFootballTypeAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    private val listData: ArrayList<FootballLevelListData>
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_select_football_type_adapter, parent, false)
            return MessageViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is MessageViewHolder) {
//            holder.imageFootballType.setImageResource(listData[position].isImage)
            holder.bind(listData.get(position), holder.adapterPosition, onItemClick)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    @Suppress("DEPRECATION")
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageFootballType: ImageView = itemView.findViewById(R.id.imageFootballType) as ImageView

        fun bind(
            footballLevelList: FootballLevelListData,
            position: Int,
            onitemClick: OnItemClick
        ) =
                with(itemView) {
                    imageFootballType.visibility=View.VISIBLE
                    tv_football_type.text = footballLevelList.footbltypeName

                    if (footballLevelList.checked) {

                        image_tick.visibility = View.VISIBLE
//                        image_tick.setColorFilter(ContextCompat.getColor(context,R.color.transperent1), android.graphics.PorterDuff.Mode.SRC_IN);
                        tv_football_type.setTextColor(context.resources.getColor(R.color.colorPrimary))

                    } else {

                        when (position) {
                            0 -> {
                                imageFootballType.setImageDrawable(resources.getDrawable(R.drawable.football_type1_03))
                            }
                            1 -> {
//                                imageFootballType.visibility = View.GONE
                                imageFootballType.setImageDrawable(resources.getDrawable(R.drawable.football_type4_480))
                            }
                            2 -> {
                                imageFootballType.setImageDrawable(resources.getDrawable(R.drawable.football_type1))
                            }
                            3 -> {
                                imageFootballType.setImageDrawable(resources.getDrawable(R.drawable.football_type3))
                            }
                            4 -> {
                                imageFootballType.setImageDrawable(resources.getDrawable(R.drawable.football_type2))
                            }
                        }

                        image_tick.visibility = View.GONE
                        tv_football_type.setTextColor(context.resources.getColor(R.color.white))
                    }
                    ll_football_type.tag = position
                    itemView.setOnClickListener {
                        onitemClick.onClicklisneter(it.tag as Int, "")

                    }

                }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)
    }
}



