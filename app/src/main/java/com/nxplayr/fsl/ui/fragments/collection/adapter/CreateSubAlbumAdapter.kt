package com.nxplayr.fsl.ui.fragments.collection.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SubAlbum
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_create_sub_album_detail.view.*

class CreateSubAlbumAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val albumListData: ArrayList<SubAlbum>,
    val viewAll: Boolean = false,
    val globalView: String? = ""

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastCheckedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null

        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_create_sub_album_detail, parent, false)

            return AlbumListViewHolder(v, context)

        }
    }

    override fun getItemCount(): Int {
        return albumListData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (albumListData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is AlbumListViewHolder) {

            holder.bind(albumListData!![position]!!, position, onItemClick)

        }

        if (globalView.equals("SubAlbumShow")){

            holder.itemView.lyl_select.visibility = View.VISIBLE
            holder.itemView.lyl_Unselect.visibility = View.GONE


        } else  if (globalView.equals("SubAlbumHide")){

            holder.itemView.lyl_select.visibility = View.GONE
            holder.itemView.lyl_Unselect.visibility = View.VISIBLE

        } else {

            holder.itemView.lyl_Unselect.visibility = View.VISIBLE
            holder.itemView.lyl_select.visibility = View.GONE

        }

        holder.itemView.lyl_Subalbum.setOnClickListener {
//            lastCheckedPosition = position
//            notifyDataSetChanged()
//            onItemClick.onClicklisneter(position)

            if (lastCheckedPosition == holder.adapterPosition) {
                lastCheckedPosition = -1
            } else {
                lastCheckedPosition = holder.adapterPosition
                if (onItemClick != null)
                    onItemClick!!.onClicklisneter(holder.adapterPosition)
            }
            notifyDataSetChanged()

        }

        if (lastCheckedPosition == position) {

            holder.itemView.imgSelected.visibility = View.VISIBLE
            holder.itemView.imgUnselected.visibility = View.GONE

        }else if (lastCheckedPosition == position){
            
            holder.itemView.imgSelected.visibility = View.GONE
            holder.itemView.imgUnselected.visibility = View.VISIBLE

        } else {

            holder.itemView.imgSelected.visibility = View.GONE
            holder.itemView.imgUnselected.visibility = View.VISIBLE
        }


    }

    class AlbumListViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {


        fun bind(albumlistData: SubAlbum, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    txtSubAlbumName.text = albumlistData!!.exsubalbumName
                    txtSubAlbumCount.text = albumlistData!!.subAlbumsPostCount!![0]!!.postCount + " items"

                    txtSubAlbumEdit.setOnClickListener {
                        onitemClick.onEditClickListner(position)
                    }
                    txtSubAlbumDelete.setOnClickListener {
                        onitemClick.onDeleteClickListner(position)
                    }

                    lyl_Subalbum.setOnClickListener {
                        onitemClick.onClicklisneter(position)
                    }
                    imgSubDot.setOnClickListener {

                        onitemClick.onSubAlbumClick(position)
                    }
                }

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)
        fun onEditClickListner(pos: Int)
        fun onDeleteClickListner(pos: Int)
        fun onSubAlbumClick(pos: Int)

    }

}