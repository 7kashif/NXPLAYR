package com.nxplayr.fsl.ui.fragments.main.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.filterfeed.view.PostHomeFilterActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.collection.view.CollectionsFragment
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreFragment
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreVideoDetailFragment
import com.nxplayr.fsl.ui.fragments.feed.view.HomeFeedListFragment
import com.nxplayr.fsl.ui.fragments.job.view.ApplyedJobListFragment
import com.nxplayr.fsl.ui.fragments.job.view.HomeJobListFragment
import com.nxplayr.fsl.ui.fragments.job.view.SaveJobListFragment
import com.nxplayr.fsl.ui.fragments.main.adapter.HomeMainViewPagerAdapter
import com.nxplayr.fsl.ui.fragments.menu.MenuFragment
import com.nxplayr.fsl.ui.fragments.notification.view.NotificationFragment
import com.nxplayr.fsl.ui.fragments.userconnection.view.SendMessageFragment
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_business_submain.*


class BusinessSubMainFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    private var adapter: HomeMainViewPagerAdapter? = null
    private val tabIcons = intArrayOf(
        R.drawable.home_feed_unselected_,
        R.drawable.home_explore_unselected_,
        R.drawable.home_chat_unselected_,
        R.drawable.home_explore_unselected_,
        R.drawable.home_explore_unselected_,)
    private var sessionManager: SessionManager? = null
    private var mActivity: AppCompatActivity? = null
    private var userData: SignupData? = null
    private var from = ""
    private var tab_position = 0
    private var footballLevel = ""
    private var countryID = ""
    private var sortby = ""
    private var postType = ""
    private var pitchPosition = ""
    private var gender = ""
    private var footballagecatID = ""
    private var footballType = ""
    private var publicationTime = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     //   if (v == null) {
            v = inflater.inflate(R.layout.fragment_business_submain, container, false)
       // }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    @SuppressLint("WrongConstant")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ll_mainLinearLayout.visibility = View.VISIBLE
        ll_mainViewPager.visibility = View.VISIBLE

        sessionManager = SessionManager(mActivity!!)

        if (arguments != null) {
            tab_position = arguments!!.getInt("tab_position", 0)
        }
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (sessionManager?.LanguageLabel != null) {
            setLanguageLable()
        }
        initView()


    }

    private fun initView() {
        if (userData != null) {
            home_profile_screen.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        }
        tv_homefeed.setOnClickListener(this)
        menu_icon.setOnClickListener(this)
        menu_filter.setOnClickListener(this)

        setupViewPager(viewPagerCommentList)
        tabLayout_home.setupWithViewPager(viewPagerCommentList)
        setupTabIcons()
        tabLayout_home.getTabAt(tab_position)?.icon?.setColorFilter(Color.parseColor("#DFA44B"), PorterDuff.Mode.SRC_IN)
        tabLayout_home.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(Color.parseColor("#DFA44B"), PorterDuff.Mode.SRC_IN)
                viewPagerCommentList.setCurrentItem(tab!!.position, true)
                when (tab.position) {
                    0 -> {
                        tabLayout_home.visibility = View.VISIBLE
                        menu_filter.visibility = View.VISIBLE
                        tv_search_skills.setText("")
                    }

                    1 -> {
                        tabLayout_home.visibility = View.VISIBLE
                        menu_filter.visibility = View.GONE
                        tv_search_skills.setText("")
                    }

                    2 -> {
                        tabLayout_home.visibility = View.VISIBLE
                        menu_filter.visibility = View.GONE
                        tv_search_skills.setText("")
                    }

                    3 -> {
                        tabLayout_home.visibility = View.VISIBLE
                        menu_filter.visibility = View.GONE
                        tv_search_skills.setText("")
                    }
                }// your logic goes here!

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        tv_search_skills.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                MyUtils.hideKeyboard1(mActivity!!)
                applySearch(tv_search_skills.text.toString().trim())
                true
            } else
                false
        }
    }

    private fun setLanguageLable() {
        if (!sessionManager?.LanguageLabel?.lngSearch.isNullOrEmpty())
            tv_search_skills.hint = sessionManager?.LanguageLabel?.lngSearch
        else
            tv_search_skills.setHint(R.string.search)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 820 && data != null) {
            if (data.hasExtra("footballLevel"))
                footballLevel = data.getStringExtra("footballLevel")!!
            if (data.hasExtra("countryID"))
                countryID = data.getStringExtra("countryID")!!
            if (data.hasExtra("sortby"))
                sortby = data.getStringExtra("sortby")!!
            if (data.hasExtra("pitchPosition"))
                pitchPosition = data.getStringExtra("pitchPosition")!!
            if (data.hasExtra("gender"))
                gender = data.getStringExtra("gender")!!
            if (data.hasExtra("footballagecatID"))
                footballagecatID = data.getStringExtra("footballagecatID")!!
            if (data.hasExtra("footballType"))
                footballType = data.getStringExtra("footballType")!!
            if (data.hasExtra("publicationTime"))
                publicationTime = data.getStringExtra("publicationTime")!!

            applyFilter()


        }

        if (requestCode == 821 && data != null) {
            if (data.hasExtra("postType"))
                postType = data.getStringExtra("postType")!!
            if (data.hasExtra("sortby"))
                sortby = data.getStringExtra("sortby")!!
            if (data.hasExtra("gender"))
                gender = data.getStringExtra("gender")!!
            if (data.hasExtra("publicationTime"))
                publicationTime = data.getStringExtra("publicationTime")!!

            applyFilter()


        }

    }

    private fun applyFilter() {
        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is HomeFeedListFragment) {
                frag1.applyFilter(
                    postType,
                    sortby,
                    gender,
                    publicationTime
                )
            }

        }
    }

    private fun applySearch(searchKeyword: String) {
        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is HomeFeedListFragment) {
                frag1.applySearch(searchKeyword)
            }
            if (frag1 is ExploreFragment) {
                frag1.applySearch(searchKeyword)
            }
            if (frag1 is CollectionsFragment) {
                frag1.applySearch(searchKeyword)
            }
            if (frag1 is SendMessageFragment) {
                frag1.applySearch(searchKeyword)
            }
        }
    }

    private fun setupTabIcons() {
        tabLayout_home.getTabAt(0)?.setIcon(tabIcons[0])
        tabLayout_home.getTabAt(1)?.setIcon(tabIcons[1])
        tabLayout_home.getTabAt(2)?.setIcon(tabIcons[2])
        tabLayout_home.getTabAt(3)?.setIcon(tabIcons[3])
        tabLayout_home.getTabAt(4)?.setIcon(tabIcons[4])
    }

    private fun setupViewPager(viewPager: ViewPager) {
       if (adapter == null) {
            adapter = HomeMainViewPagerAdapter(childFragmentManager)
            adapter?.addFragment(HomeJobListFragment(), "")
            adapter?.addFragment(SaveJobListFragment(), "")
            adapter?.addFragment(ApplyedJobListFragment(), "")
            adapter?.addFragment(RssFeedFragment(), "")
            adapter?.addFragment(RssFeedFragment(), "")
      }
        viewPager.offscreenPageLimit =3
        viewPager.adapter = adapter
        viewPager.currentItem = tab_position
    }

    fun getUpdated(
        from: String,
        datumList: List<CreatePostPhotoPojo?>?,
        stringExtraDes: String,
        stringExtraPrivcy: String
    ) {
        for (frag1 in childFragmentManager.fragments) {
            when (from) {
                "Text", "Video", "Place", "Link", "Document" -> {
                    if (frag1 is HomeFeedListFragment) {
                        (frag1 as HomeFeedListFragment).pageNo = 0
                        (frag1 as HomeFeedListFragment).setupObserver()
                    }
                }
                else -> {
                    if (frag1 is HomeFeedListFragment) {
                        (frag1 as HomeFeedListFragment).pageNo = 0
                        (frag1 as HomeFeedListFragment).setupObserver()
                    }
                }
            }
        }
    }

    fun isPogress(isVisiBle: Boolean) {
        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is HomeFeedListFragment) {
                (frag1 as HomeFeedListFragment).isProgress(isVisiBle)
            }

        }
    }

    fun getPostList(
        from: String,
        datumList: List<CreatePostData?>?,
        stringExtraDes: String,
        stringExtraPrivcy: String
    ) {
        for (frag1 in childFragmentManager.fragments) {
            when (from) {
                "images" -> {
                    if (frag1 is HomeFeedListFragment) {
                        (frag1 as HomeFeedListFragment).createPost(
                            from,
                            datumList,
                            stringExtraDes,
                            stringExtraPrivcy
                        )
                    }
                }
                else -> {
                    if (frag1 is HomeFeedListFragment) {
                        (frag1 as HomeFeedListFragment).getPostListApi()
                    }

                }

            }
        }
    }

    fun AlbumNotifyData(
        postId: String,
        userId: String,
        from: String,
        explore_video_list: ArrayList<CreatePostData?>?,
        postType: String
    ) {
        var exploreDetailFragment = ExploreVideoDetailFragment()
        Bundle().apply {
            putString("postType", postType)
            putSerializable("explore_video_list", explore_video_list)
            exploreDetailFragment.arguments = this
        }
        (activity as MainActivity).navigateTo(
            exploreDetailFragment,
            exploreDetailFragment::class.simpleName!!,
            true
        )

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_homefeed -> {
                ll_mainLinearLayout.visibility = View.GONE
                ll_mainViewPager.visibility = View.GONE
                (activity as MainActivity).navigateToBusiness(
                    BusinessMainFragment(),
                    BusinessMainFragment::class.java.name,
                    true
                )
            }
            R.id.menu_icon -> {
                MyUtils.hideKeyboard1(mActivity!!)
                (activity as MainActivity).navigateTo(
                    MenuFragment(),
                    MenuFragment::class.java.name,
                    true
                )

            }
            R.id.menu_filter -> {
                Intent(mActivity!!, PostHomeFilterActivity::class.java).apply {
                    putExtra("from", "filterHome")
                    putExtra("sortby", sortby)
                    putExtra("postType", postType)
                    putExtra("gender", gender)
                    putExtra("publicationTime", publicationTime)
                    startActivityForResult(this, 821)
                }
            }
        }
    }

    fun notifyData(
        feedDatum: CreatePostData?,
        delete: Boolean,
        deleteComment: Boolean,
        postComment: String?,
        commentId:String?
    ) {
        for (frag1 in childFragmentManager.fragments) {

            if (frag1 is HomeFeedListFragment) {
               frag1.notifyData(  feedDatum,
                delete,
                deleteComment,
                postComment,commentId)
            }


        }
    }

}




