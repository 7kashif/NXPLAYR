package com.nxplayr.fsl.data.model

/**
 * Created by ADMIN on 28/12/2017.
 */
class FeedModel {
    var type: String? = null

    companion object {
        const val TEXT_TYPE = "Text"
        const val Audio_TYPE = "Audio"
        const val Photo_TYPE = "Photo"
        const val Check_IN = "Place"
        const val POll = "Poll"
        const val Media_Type = "Social"
        const val Media_Type2 = "Social"
        const val Ask_something = "Ask Something"
        const val Video_TYPE = "Video"
        const val API_CREATE_POST_TAG = "createpost"
    }
}