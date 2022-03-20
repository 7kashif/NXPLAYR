package com.nxplayr.fsl.ui.activity.post.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo

class CreatePostDocumentAdapter(context: Context, private val documentArrayList: ArrayList<CreatePostPhotoPojo>, var openItem: OnClickToOpenListener, var itemClicks: OnItemClickListener):
        RecyclerView.Adapter<CreatePostDocumentAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.document_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return documentArrayList.size }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.documentTextView.text = documentArrayList[position].imageName
        when{
            documentArrayList[position].fileExtension=="pdf"||documentArrayList[position].fileExtension==".pdf"->{
                holder.documentImageView.setBackgroundResource(R.drawable.pdf_icon)
            }
            documentArrayList[position].fileExtension == "doc" || documentArrayList[position].fileExtension=="docx"||documentArrayList[position].fileExtension==".docx"||documentArrayList[position].fileExtension==".doc"-> {
                holder.documentImageView.setBackgroundResource(R.drawable.word_file_icon)
            }
            documentArrayList[position].fileExtension =="xls" || documentArrayList[position].fileExtension =="xlsx"||documentArrayList[position].fileExtension ==".xls" || documentArrayList[position].fileExtension ==".xlsx" -> {
                holder.documentImageView.setBackgroundResource(R.drawable.word_file_icon)
//                holder.documentImageView.setBackgroundResource(R.drawable.excel_icon)
            }
            documentArrayList[position].fileExtension =="ppt" || documentArrayList[position].fileExtension == "pptx"|| documentArrayList[position].fileExtension ==".ppt" || documentArrayList[position].fileExtension == ".pptx" ->{
                holder.documentImageView.setBackgroundResource(R.drawable.ppt_icon)
            }

        }

        holder.bind(documentArrayList,position,itemClicks,openItem)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var documentImageView: SimpleDraweeView = itemView.findViewById(R.id.documentImageView)
        var documentTextView: TextView = itemView.findViewById(R.id.documentTextView) as TextView
        var cancelDocumentIV: ImageView = itemView.findViewById(R.id.cancelDocumentIV) as ImageView

        fun bind(documentArrayList: ArrayList<CreatePostPhotoPojo>, position: Int, onItemClickListener: OnItemClickListener, onClickToOpenListener: OnClickToOpenListener){

            cancelDocumentIV.setOnClickListener {
                onItemClickListener.onClicked(documentArrayList,position)
            }
            documentImageView.setOnClickListener {
                onClickToOpenListener.onClickToOpen(documentArrayList, position)
            }
        }
    }
    interface OnItemClickListener{
        fun onClicked(documentArrayList: ArrayList<CreatePostPhotoPojo>, position: Int)
    }
    interface OnClickToOpenListener{
        fun onClickToOpen(documentArrayList: ArrayList<CreatePostPhotoPojo>, position: Int)
    }
}