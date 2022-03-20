package com.nxplayr.fsl.data.model

/**
 * Created by anupamchugh on 09/02/16.
 */
class Model {
    var type: Int
    var img = 0
    var img1 = 0
    var img2 = 0
    var img3 = 0
    var text: String? = null

    constructor(type: Int, text: String?) {
        this.type = type
        this.text = text
    }

    constructor(type: Int, img: Int) {
        this.type = type
        this.img = img
    }

    constructor(type: Int, img: Int, img1: Int, img2: Int, img3: Int) {
        this.type = type
        this.img = img
        this.img2 = img2
        this.img1 = img1
        this.img3 = img3
    }

    constructor(type: Int, img: Int, img1: Int) {
        this.type = type
        this.img = img
        this.img1 = img1
    }

    constructor(type: Int, img: Int, img1: Int, img2: Int) {
        this.type = type
        this.img = img
        this.img2 = img2
        this.img1 = img1
    }

    companion object {
        const val TEXT_TYPE = 0
        const val IMAGE_TYPE = 1
        const val AUDIO_TYPE = 2
        const val CheckIn_TYPE = 3
        const val Video_TYPE = 5
        const val Link_TYPE = 4
        const val Document_TYPE = 16

        const val Poll_TYPE1 = 41
        const val Loder_TYPE = 6
        const val Friend_Suggestion_TYPE = 7
        const val Business_Suggestion_TYPE = 8
        const val Like_TYPE = 9
        const val DisLike_TYPE = 10
        const val Shared_TYPE = 11
        const val Comment_TYPE = 12
        const val Favorites_TYPE = 13
        const val other_user_profile = 14
        const val MyProfile_TYPE = 15
        const val threeimg = 3
        const val fouorimg = 4
    }
}