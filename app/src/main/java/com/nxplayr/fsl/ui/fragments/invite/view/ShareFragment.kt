package com.nxplayr.fsl.ui.fragments.invite.view


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.invite.view.InviteMainFragment
import com.nxplayr.fsl.util.BlurPostprocessor
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_share.*
import java.io.ByteArrayOutputStream


class ShareFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var tabPosition: Int = 0
    var postprocessor: BlurPostprocessor? = null
    var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_share, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Fresco.initialize(mActivity);
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupUI()

    }

    private fun setupUI() {
        if (userData != null) {
            tv_username.text = userData!!.userFirstName + " " + userData!!.userLastName
            img_user_profile.setImageURI(RestClient.image_base_url_users + userData!!.userProfilePicture)
        }

        tabPosition = arguments!!.getInt("position") as Int

        postprocessor = BlurPostprocessor(mActivity, 15, 10)

        try {

            val request =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(RestClient.image_base_url_users + userData!!.userProfilePicture))
                    .setPostprocessor(postprocessor)
                    .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(blur_image?.controller)
                .build() as PipelineDraweeController
            blur_image?.controller = controller

        } catch (e: Exception) {
            e.printStackTrace()
        }



        btn_shareCard.setOnClickListener {
            val view = v?.findViewById(R.id.shareLinearLayout) as FrameLayout
            val bm = viewToBitmap(view)
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            val bytes = ByteArrayOutputStream()
            bm?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                context!!.contentResolver,
                bm, "Title", null
            )
            val imageUri = Uri.parse(path)
            share.putExtra(Intent.EXTRA_STREAM, imageUri)
            startActivity(Intent.createChooser(share, "Select"))

        }

        (parentFragment as InviteMainFragment).cardImageUri(shareLinearLayout)

    }

    fun viewToBitmap(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


}




