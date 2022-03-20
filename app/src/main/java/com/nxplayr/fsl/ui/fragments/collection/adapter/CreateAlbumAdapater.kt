package com.nxplayr.fsl.ui.fragments.collection.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.AlbumDatum
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_create_album_list_detail_activity.view.*

class CreateAlbumAdapater (

    val context: Activity,
    val albumListData: ArrayList<AlbumDatum?>?,
    val onItemClick: OnItemClick,
    val viewAll: Boolean = false,
    val globalView: String = "show"

) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        }else{

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_create_album_list_detail_activity, parent, false)
            return AlbumListViewHolder(v ,context)

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

            if (MyUtils.GlobarViewData == globalView){

                holder.itemView.txtAlbumEdit.visibility =View.GONE
                holder.itemView.txtAlbumDelete.visibility =View.GONE
                holder.itemView.imgDots.visibility =View.VISIBLE
                holder.itemView.imgSubAlbum.visibility =View.GONE
            } else{

                holder.itemView.txtAlbumEdit.visibility =View.GONE
                holder.itemView.txtAlbumDelete.visibility =View.GONE
                holder.itemView.imgDots.visibility =View.GONE
                holder.itemView.imgSubAlbum.visibility =View.VISIBLE
            }
        }

    }

    class AlbumListViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(albumlistData: AlbumDatum, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    txtAlbumName.text = albumlistData.exalbumName
                    txtAlbumCount.text = albumlistData.albumsPostCount[0].postCount + " items"

                    txtAlbumDelete.setOnClickListener {
                        onitemClick.onClicklisneter(position)
                    }

                    txtAlbumEdit.setOnClickListener {
                        onitemClick.onEditAlbum(position)
                    }
                    lyl_album.setOnClickListener {
                        onitemClick.onSubAlbum(position)
                    }
                    imgDots.setOnClickListener {
                        onitemClick.onAlbumOption(position)
                    }
                }


    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)
        fun onEditAlbum(posistion: Int)
        fun onSubAlbum(posistion: Int)
        fun onAlbumOption(posistion: Int)
    }

}