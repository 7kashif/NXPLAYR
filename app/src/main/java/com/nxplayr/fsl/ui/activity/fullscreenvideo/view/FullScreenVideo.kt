package com.nxplayr.fsl.ui.activity.fullscreenvideo.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.CacheDataSourceFactory1

class FullScreenVideo : AppCompatActivity() {
    var closefullscreen: ImageView? = null
    var videoviewfullscreen: PlayerView? = null
    var exoPlayer: SimpleExoPlayer? = null
    var videouri: String? = null
    var seekTo = 0L
    var progressBar: ProgressBar? = null
    var getOrientation = 0
    var ivFullscreen1: ImageButton? = null
    var ivFullscreen: ImageButton? = null
    private var isMuteing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_video)
        closefullscreen = findViewById<View>(R.id.closefullscreen) as ImageView
        videoviewfullscreen = findViewById<View>(R.id.videoviewfullscreen) as PlayerView
        videouri = intent.extras!!.getString("videouri")
        if (intent.hasExtra("Seek")) {
            seekTo = intent.getLongExtra("Seek", 0L)
        }
        val controlView = findViewById<PlayerControlView>(R.id.exo_controller)
        ivFullscreen = controlView.findViewById<View>(R.id.exo_fullscreen_icon) as ImageButton
        progressBar = controlView.findViewById<View>(R.id.progressBar) as ProgressBar
        ivFullscreen!!.visibility = View.GONE
        ivFullscreen1 = controlView.findViewById<View>(R.id.exo_fullscreen_icon1) as ImageButton
        ivFullscreen1!!.visibility = View.VISIBLE
        val ivVolume1 = controlView.findViewById<View>(R.id.exo_volume_icon1) as ImageButton
        ivVolume1.visibility = View.VISIBLE
        val ivVolume = controlView.findViewById<View>(R.id.exo_volume_icon) as ImageButton
        ivVolume.visibility = View.GONE
        ivFullscreen1!!.background =
            this@FullScreenVideo.resources.getDrawable(R.drawable.ic_fullscreen_exit_black_24dp)
        //   ivFullscreen1.setBackground(FullScreenVideo.this.getResources().getDrawable(R.drawable.ic_screen_rotation));
        try {
            val videoURI = Uri.parse(videouri)
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


// This is the MediaSource representing the media to be played.
            val videoSource: MediaSource = ExtractorMediaSource.Factory(
                CacheDataSourceFactory1(
                    this@FullScreenVideo,
                    5 * 1024 * 1024
                )
            )
                .createMediaSource(videoURI)
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this@FullScreenVideo, trackSelector)
            exoPlayer!!.prepare(videoSource)
            videoviewfullscreen!!.player = exoPlayer
            exoPlayer!!.playWhenReady = true
            exoPlayer!!.repeatMode =0
            exoPlayer!!.seekTo(seekTo)
            exoPlayer!!.addListener(object : Player.EventListener {
                override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
                override fun onTracksChanged(
                    trackGroups: TrackGroupArray,
                    trackSelections: TrackSelectionArray
                ) {
                }

                override fun onLoadingChanged(isLoading: Boolean) {}
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                        }
                        Player.STATE_ENDED -> {
                        }
                        Player.STATE_IDLE -> {
                        }
                        Player.STATE_READY -> {
                        }
                        else -> {
                        }
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {}
                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
                override fun onPlayerError(error: ExoPlaybackException) {
                    progressBar!!.visibility = View.GONE
                }

                override fun onPositionDiscontinuity(reason: Int) {}
                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
                override fun onSeekProcessed() {}
            })
        } catch (e: Exception) {
            Log.e("MainAcvtivity", " exoplayer error $e")
        }
        closefullscreen!!.setOnClickListener { onBackPressed() }
        ivFullscreen1!!.setOnClickListener {
            getOrientation = this@FullScreenVideo.resources.configuration.orientation
            setOrientation(getOrientation)
        }
        ivVolume1.setOnClickListener {
            if (isMuteing) {
                muteVideo(false)
                ivVolume1.background =
                    this@FullScreenVideo.resources.getDrawable(R.drawable.ic_volume_up_black_24dp)
            } else {
                muteVideo(true)
                ivVolume1.background =
                    this@FullScreenVideo.resources.getDrawable(R.drawable.ic_volume_off_black_24dp)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (exoPlayer != null) startPlayer()
    }

    override fun onPause() {
        super.onPause()
        if (exoPlayer != null) pausePlayer()
    }

    private fun pausePlayer() {
        exoPlayer!!.playWhenReady = false
        exoPlayer!!.playbackState
    }

    private fun startPlayer() {
        exoPlayer!!.playWhenReady = true
        exoPlayer!!.playbackState
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer != null) exoPlayer!!.release()
    }

    fun muteVideo(isMute: Boolean) {
        if (exoPlayer != null) {
            if (isMute) exoPlayer!!.volume = 0f else exoPlayer!!.volume = 1f
            isMuteing = isMute
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK, Intent())
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun setOrientation(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}