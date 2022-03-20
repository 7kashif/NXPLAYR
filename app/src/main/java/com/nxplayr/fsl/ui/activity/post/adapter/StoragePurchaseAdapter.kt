package com.nxplayr.fsl.ui.activity.post.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_purchase.view.*

class StoragePurchaseAdapter(
    val context: Activity,
    var storagePurchaseItem: ArrayList<String>,
    val onItemClick: OnItemClick,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastCheckedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false)

            return AlbumListViewHolder(v, context)

        }
    }

    override fun getItemCount(): Int {
        return storagePurchaseItem?.size
    }

   /* override fun getItemViewType(position: Int): Int {
        return if (albumListData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is AlbumListViewHolder) {

            holder.txt_purchase_price.text=storagePurchaseItem?.get(position)
            holder.itemView.setOnClickListener {
                if (lastCheckedPosition == holder.adapterPosition) {
                    lastCheckedPosition = -1
                } else {
                    lastCheckedPosition = holder.adapterPosition
                    onItemClick.onClicklisneter(holder.adapterPosition)
                }
                notifyDataSetChanged()
            }

            if (lastCheckedPosition == position)
            {
                holder.btn_purchase_price.backgroundTint = (context.resources.getColor(R.color.colorPrimary))
                holder.btn_purchase_price.textColor = context.resources.getColor(R.color.black)
                holder.tick_success.visibility=View.VISIBLE
            }
            else
            {
                holder.btn_purchase_price.strokeColor = (context.resources.getColor(R.color.btn_purchase))
                holder.btn_purchase_price.backgroundTint = (context.resources.getColor(R.color.btn_purchase))
                holder. btn_purchase_price.textColor = context.resources.getColor(R.color.white)
                holder.tick_success.visibility=View.GONE
            }
        }
    }

    class AlbumListViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView)
    {

        var btn_purchase_price=itemView.btn_purchase_price
        var txt_purchase_price=itemView.txt_purchase_price
        var tick_success=itemView.tick_success

        fun bind(position: Int, onitemClick: OnItemClick) =
                with(itemView) {


                }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)
    }

}