package com.nxplayr.fsl.ui.activity.fullscreenvideo.view

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : AppCompatActivity()  {
    private var videoUri= ""

    override fun onStart() {
        super.onStart()

        videoView3.start()
    }

    override fun onResume() {
        super.onResume()

        videoView3.resume()
    }

    override fun onRestart() {
        super.onRestart()

        videoView3.resume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        videoUri=if(intent != null && intent.hasExtra("Video"))
            intent?.extras!!.getString("Video", "")
        else
            ""

        setupUI()
    }

    private fun setupUI() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        initView()
    }

    private fun initView(){

        videoView3.setVideoPath(videoUri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView3)
        videoView3.setMediaController(mediaController)

        videoView3.start()

    }
}
