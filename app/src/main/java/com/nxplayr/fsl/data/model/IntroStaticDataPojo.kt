package com.nxplayr.fsl.data.model

import android.graphics.drawable.Drawable
import java.io.Serializable

class IntroStaticDataPojo(title: String, subTitle: String, Images: Drawable?) : Serializable {

    var title = title
    var subTitle = subTitle
    var Images = Images
}