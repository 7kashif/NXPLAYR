package com.nxplayr.fsl.ui.activity.filterfeed.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chand.progressbutton.ProgressButton
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.filterfeed.adapter.PostHomeFilterSubItemAdapter
import com.nxplayr.fsl.data.model.FilterSubItem
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_post_home.*
import kotlinx.android.synthetic.main.activity_post_home.lylUnSelectApply
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_passport_nationality.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.toolbar1.*

class PostHomeFilterActivity : AppCompatActivity(),View.OnClickListener {

    var genders = ""
    var publicationTime = ""
    var sortBy =""
    var postType="All"

    var postTypeList:ArrayList<FilterSubItem>?=null
    var postHomeFilterSubItemAdapter: PostHomeFilterSubItemAdapter? = null
    var linearLayoutManager:LinearLayoutManager?=null
    var listpostType: List<String> = listOf("All", "Photo", "Video", "Document", "Link")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_home)
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle1.text = this@PostHomeFilterActivity.resources.getString(R.string.filters)
        toolbar1.setNavigationIcon(R.drawable.back_arrow_signup)
        llToolbarmain.visibility=View.VISIBLE

        tvToolbarNext.text=resources.getString(R.string.clear_all)
        toolbar1.setNavigationOnClickListener {
            onBackPressed()
        }
        postTypeList= ArrayList()
        postTypeList?.add(FilterSubItem("All","", false))
        postTypeList?.add(FilterSubItem("Photo","", false))
        postTypeList?.add(FilterSubItem("Video","", false))
        postTypeList?.add(FilterSubItem("Document","", false))
        postTypeList?.add(FilterSubItem("Link","", false))


        if(intent!=null){
            genders = intent.getStringExtra("gender")!!
            publicationTime = intent.getStringExtra("publicationTime")!!
            sortBy = intent.getStringExtra("sortby")!!
            if(intent.hasExtra("postType"))
                postType = intent.getStringExtra("postType")!!
        }

        recyclerview_posttype.visibility=View.GONE
        radiogroupPublication.visibility=View.GONE
        radiogroupPostedBy.visibility=View.GONE
        radiogroupSortBy.visibility=View.GONE

        if(!genders.isNullOrEmpty())
        {
            setViewColor(tvFilterTitle2,img_plus2)
            radiogroupPostedBy.visibility=View.VISIBLE
            if(genders.equals("Male",false))
            {
                ratingPostedByRB.isChecked=true

            }else if(genders.equals("Female",false)){
                ratingPostedByRB1.isChecked=true
            }
        }
        if(!publicationTime.isNullOrEmpty())
        {
            radiogroupPublication.visibility=View.VISIBLE

            setViewColor(tvFilterTitle1,img_plus1)
            if (publicationTime.equals("Anytime",false)) {
                ratingPublicationRB.isChecked = true

            } else if (publicationTime.equals("Today", false)) {
                ratingPublicationRB1.isChecked = true
            } else if (publicationTime.equals("This Week", false)) {
                ratingPublicationRB2.isChecked = true
            } else if (publicationTime.equals("This Month", false)) {
                ratingPublicationRB3.isChecked = true
            }
        }
        if(!postType.isNullOrEmpty())
        {
            setViewColor(tvFilterTitle,img_plus)
            recyclerview_posttype.visibility=View.VISIBLE
            //inflatePostType(postTypeList!!)
            /*if(postType.equals("All",false))
            {
                ratingPostTypeRB.isChecked=true
            }else if(postType.equals("Photo",false)){
                ratingPostTypeRB1.isChecked=true
                radiogroupPostType.visibility=View.VISIBLE

            }else if(postType.equals("Video",false)){
                ratingPostTypeRB2.isChecked=true
                radiogroupPostType.visibility=View.VISIBLE

            }else if(postType.equals("Document",false)){
                ratingPostTypeRB3.isChecked=true
                radiogroupPostType.visibility=View.VISIBLE

            }else if(postType.equals("Link",false)){
                ratingPostTypeRB4.isChecked=true
                radiogroupPostType.visibility=View.VISIBLE

            }*/
        }
        if(!sortBy.isNullOrEmpty())
        {
            radiogroupSortBy.visibility=View.VISIBLE
            setViewColor(tvFilterTitle3,img_plus3)
            if(sortBy.equals("mypostonly",false))
            {
                ratingSortRB.isChecked=true
            }else if(sortBy.equals("myconnectionpostonly",false)){
                ratingSortRB1.isChecked=true
            }else if(sortBy.equals("mostviewed",false)){
                ratingSortRB2.isChecked=true
            }else if(sortBy.equals("mostliked",false)){
                ratingSortRB3.isChecked=true
            }else if(sortBy.equals("mostcommented",false)){
                ratingSortRB4.isChecked=true
            }
        }

        ll_posttype.setOnClickListener(this)
        ll_Publication.setOnClickListener(this)
        ll_PostedBy.setOnClickListener (this)
        ll_Sort.setOnClickListener (this)
        tvToolbarNext.setOnClickListener(this)
        lylUnSelectApply.setOnClickListener(this)

        linearLayoutManager = LinearLayoutManager(this@PostHomeFilterActivity)

        postHomeFilterSubItemAdapter = PostHomeFilterSubItemAdapter (this@PostHomeFilterActivity, object : PostHomeFilterSubItemAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, from: String) {
                postType = postTypeList?.filter {
                    it?.isSelected!!
                }!!.joinToString {
                    it?.title
                }
            }

        }, postTypeList)

        recyclerview_posttype.layoutManager = linearLayoutManager
        recyclerview_posttype.adapter = postHomeFilterSubItemAdapter

        radiogroupPublication.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            publicationTime = radioButton.text.toString().trim()
            setViewColor(tvFilterTitle1, img_plus1)
            btnBckground(lylUnSelectApply)
        }
        radiogroupPostedBy.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            genders = radioButton.text.toString().trim()
            setViewColor(tvFilterTitle2, img_plus2)
            btnBckground(lylUnSelectApply)
        }
        radiogroupSortBy.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            sortBy = radioButton.text.toString().trim()
            setViewColor(tvFilterTitle3, img_plus3)
            btnBckground(lylUnSelectApply)
        }


    }
    private fun setViewColor(tvFilterTitle: TextView?, imgPlus: ImageView?) {
            tvFilterTitle?.setTextColor(resources.getColor(R.color.colorPrimary))
            imgPlus?.setImageResource(R.drawable.ic_baseline_remove_24)
    }

    private fun btnBckground(btn: ProgressButton)
    {
        btn.backgroundTint = (resources.getColor(R.color.colorPrimary))
        btn.textColor = resources.getColor(R.color.black)
        btn.strokeColor = resources.getColor(R.color.colorPrimary)
    }

    private fun applyFilter(isFinish: Boolean) {

        var intent = Intent().apply {
            if(!isFinish){
                putExtra("gender", "")
                putExtra("publicationTime", "")
                putExtra("postType", "")
                putExtra("sortby", "")
            }  else {
                Log.e("postType",postType)
                putExtra("postType", postType)
                if (sortBy.equals("My posts only", false)) {
                    putExtra("sortby", "mypostonly")
                } else if (sortBy.equals("Connection's posts", false)) {
                    putExtra("sortby", "myconnectionpostonly")
                } else if (sortBy.equals("Most Viewed", false)) {
                    putExtra("sortby", "mostviewed")
                } else if (sortBy.equals("Most Liked", false)) {
                    putExtra("sortby", "mostliked")
                } else if (sortBy.equals("Most Commented", false)) {
                    putExtra("sortby", "mostcommented")
                }
                putExtra("gender", genders)
                putExtra("publicationTime", publicationTime)
            }
        }
        setResult(821, intent)
      //  if (isFinish)
            onBackPressed()

    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@PostHomeFilterActivity, true)
    }

    private fun setVisibleView(radiogroupPostType: RadioGroup?, tvFilterTitle: TextView?, imgPlus: ImageView?) {
        if(radiogroupPostType?.visibility== View.VISIBLE)
        {
            radiogroupPostType?.visibility= View.GONE
            tvFilterTitle?.setTextColor(resources.getColor(R.color.white))
            imgPlus?.setImageResource(R.drawable.plus_icon_white)
        }else  if(radiogroupPostType?.visibility== View.GONE){
            radiogroupPostType?.visibility= View.VISIBLE
            tvFilterTitle?.setTextColor(resources.getColor(R.color.white))
            imgPlus?.setImageResource(R.drawable.ic_baseline_remove_white)
        }
    }

    private fun setVisibleView(radiogroupPostType: RecyclerView?, tvFilterTitle: TextView?, imgPlus: ImageView?) {
        if(radiogroupPostType?.visibility== View.VISIBLE)
        {
            radiogroupPostType?.visibility= View.GONE
            tvFilterTitle?.setTextColor(resources.getColor(R.color.white))
            imgPlus?.setImageResource(R.drawable.plus_icon_white)
        }else  if(radiogroupPostType?.visibility== View.GONE){
            radiogroupPostType?.visibility= View.VISIBLE
            tvFilterTitle?.setTextColor(resources.getColor(R.color.white))
            imgPlus?.setImageResource(R.drawable.ic_baseline_remove_white)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.ll_posttype->{
                setVisibleView(recyclerview_posttype, tvFilterTitle, img_plus)

            }
            R.id.ll_Publication->{
                setVisibleView(radiogroupPublication, tvFilterTitle1, img_plus1)

            }
            R.id.ll_PostedBy->{
                setVisibleView(radiogroupPostedBy, tvFilterTitle2, img_plus2)

            }
            R.id.ll_Sort->{
                setVisibleView(radiogroupSortBy, tvFilterTitle3, img_plus3)

            }
            R.id.tvToolbarNext->{
                sortBy.isBlank()
                genders.isBlank()
                postType.isBlank()
                publicationTime.isBlank()


                ratingPostedByRB.isChecked=false
                ratingPostedByRB1.isChecked=false
                ratingPublicationRB.isChecked = false
                ratingPublicationRB1.isChecked = false
                ratingPublicationRB2.isChecked = false
                ratingPublicationRB3.isChecked = false

                ratingSortRB4.isChecked=false
                ratingSortRB.isChecked=false
                ratingSortRB3.isChecked=false
                ratingSortRB2.isChecked=false
                ratingSortRB1.isChecked=false

                applyFilter(false)
            }
            R.id.lylUnSelectApply->{
                applyFilter(true)
            }

        }
    }
}