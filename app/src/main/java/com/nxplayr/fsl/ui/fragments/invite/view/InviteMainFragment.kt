package com.nxplayr.fsl.ui.fragments.invite.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.facebook.FacebookSdk.getApplicationContext
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.ContactListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.ui.fragments.invite.adapter.InviteViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_main_invite.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.ByteArrayOutputStream


class InviteMainFragment : Fragment(),View.OnClickListener {

    var v: View? = null
    var activity: AppCompatActivity? = null
    var adapter: InviteViewPagerAdapter?=null
    var contactList: ArrayList<ContactListData?>? = ArrayList()
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var position: Int = 0
    var imageUri: Uri? = null
    var bitmap: Bitmap? = null
    var frameLayout: View? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (v == null)
            v = inflater.inflate(R.layout.fragment_main_invite, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowCustomEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupUI()

    }
    private fun setupUI() {
        tvToolbarTitle.text = activity!!.resources.getString(R.string.invite)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
            MyUtils.hideKeyboard1(activity!!)
        }
        setupViewPager(invite_item_ViewPager)
        invite_item_Tablayout!!.setupWithViewPager(invite_item_ViewPager)
        invite_item_Tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                invite_item_ViewPager.setCurrentItem(tab!!.position, true)
                if (tab.position == 0) {
                    menuToolbarItemsend.visibility = View.GONE
                } else {
                    menuToolbarItemsend.visibility = View.VISIBLE
                }

            }

        })
        menuToolbarItemsend.setOnClickListener(this)
    }

    fun inviteTabList(inviteListData: ArrayList<ContactListData?>?) {
        contactList?.clear()
        contactList?.addAll(inviteListData!!)
    }

    fun cardImageUri(shareLinearLayout: FrameLayout) {
        frameLayout = shareLinearLayout
    }


    fun viewToBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = InviteViewPagerAdapter(activity!!,childFragmentManager)
        adapter?.addFragment(ShareFragment(), resources.getString(R.string.share))
        adapter?.addFragment(InviteTabFragment(), resources.getString(R.string.sms))
        adapter?.addFragment(InviteTabFragment(), resources.getString(R.string.mail))
        viewPager.currentItem = position
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = adapter

    }

    fun sms() {
        if(contactList?.isNullOrEmpty()!!) {
            MyUtils.showSnackbar(activity!!, "Please select any contacts", ll_main_invite)

        }else{
            val bm = viewToBitmap(frameLayout!!)
            val bytes = ByteArrayOutputStream()
            bm?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

            val path: String = MediaStore.Images.Media.insertImage(context!!.contentResolver,
                    bm, "Title", null)
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra("address", contactList?.joinToString { it!!.usercontactPhone })
                intent.putExtra("sms_body", "Join me on NxPlay.R. You can download it here: " + /*activity!!.packageName*/"https://www.nxplayr.com")
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
                intent.type = "image/*"
                activity?.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendSMS(msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(contactList?.joinToString { it!!.usercontactPhone }, null, msg, null, null)
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(getApplicationContext(), ex.message.toString(),
                    Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }
    }

    private fun sendEmail(subject: String, message: String) {

        val bm = viewToBitmap(frameLayout!!)
        val bytes = ByteArrayOutputStream()
        bm?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(context!!.contentResolver,
                bm, "Title", null)

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            data = Uri.parse("mailto:")
            type = "image/png"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(contactList?.joinToString { it!!.usercontactEmail }))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_STREAM, Uri.parse(path))

        }

        try {
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                intent.setPackage("com.google.android.gm")
                startActivity(Intent.createChooser(intent, "Choose Email Client..."))
            } else {
            }
        } catch (e: Exception) {
            Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
        }

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
          R.id.menuToolbarItemsend->{
              if (invite_item_ViewPager.currentItem == 1)
              {
                  sms()
              }
              else
              if (invite_item_ViewPager.currentItem == 2) {
                  sendEmail("FSL", "Join me on NxPlay.R. You can download it here: " + /*activity!!.packageName*/"https://www.nxplayr.com")
              }

          }
        }
    }


    /*   private fun sendEmail(subject: String, message: String) {

           val mIntent = Intent(Intent.ACTION_SEND)
           mIntent.data = Uri.parse("mailto:")
           mIntent.type = "text/plain"
           mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contactList?.joinToString { it!!.usercontactEmail }))
           mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
           mIntent.putExtra(Intent.EXTRA_TEXT, message)
           try {
               startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
           } catch (e: Exception) {
               Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()
           }

       }
   */

}