package com.nxplayr.fsl.ui.activity.filterimage.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.ToolModel

class EditImageAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val filterList: ArrayList<ToolModel>,
    val viewAll: Boolean = false
) : RecyclerView.Adapter<EditImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_editing_tools, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filterList[position], position, onItemClick)
    }

    override fun getItemCount(): Int {
      return filterList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(toolModel: ToolModel, position: Int, onItemClick: OnItemClick) {
            TODO("Not yet implemented")
        }

    }

    interface OnItemClick {

        fun onClicklisneter(pos: Int, filterName: String) {

        }
    }
}