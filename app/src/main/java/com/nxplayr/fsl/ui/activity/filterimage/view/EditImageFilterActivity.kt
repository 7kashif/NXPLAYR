package com.nxplayr.fsl.ui.activity.filterimage.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.filterimage.adapter.EditImageAdapter
import com.nxplayr.fsl.data.model.ToolModel
import kotlinx.android.synthetic.main.activity_editimagefilter.*
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.item_explore_video_grid_detail_activity.view.*

class EditImageFilterActivity: AppCompatActivity() {

    var imagePath = ""
    var editImageAdapater: EditImageAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    val filterList = ArrayList<ToolModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editimagefilter)

//        imagePath = intent!!.getStringExtra("image_path")!!
//        Log.d("imagePath", "file://" + imagePath)

        filterList.add(ToolModel("BRUSH", R.drawable.ic_brush, "BRUSH"))
        filterList.add(ToolModel("TEXT", R.drawable.ic_text, "TEXT"))
        filterList.add(ToolModel("ERASER", R.drawable.ic_eraser, "ERASER"))
        filterList.add(ToolModel("FILTER", R.drawable.ic_photo_filter, "FILTER"))
        filterList.add(ToolModel("EMOJI", R.drawable.ic_insert_emoticon, "EMOJI"))


        editImageAdapater = EditImageAdapter(this@EditImageFilterActivity, object : EditImageAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, from: String) {
                Toast.makeText(this@EditImageFilterActivity,"Click " + pos, Toast.LENGTH_SHORT).show()
            }

        }, filterList!!, false)
        rvConstraintTools.layoutManager = linearLayoutManager

        rvConstraintTools.setHasFixedSize(true)
        rvConstraintTools.adapter = editImageAdapater
    }
}