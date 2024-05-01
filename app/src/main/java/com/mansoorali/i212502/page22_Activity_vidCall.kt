package com.mansoorali.i212502


import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class page22_Activity_vidCall : AppCompatActivity() {
    private val PERMISSION_ID = 12
    private val app_id =
            "9dd4f8ef62a6460e86c69e2255776232"
    private val channelName = "Channel"
    private val token = "007eJxTYLCd/vfaVZ6M5RreccErJ4jKvuW7sIix0V3+cgH/jSuSXpsUGCxTUkzSLFLTzIwSzUzMDFItzJLNLFONjExNzc3NjIyNPk8VSGsIZGT4+r+BkZEBAkF8dgbnjMS8vNQcBgYAabcf9A=="
    private var uid =0
    var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private val REQUESTED_PERMISSION = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )
    private fun checkSelfPermission():Boolean{
        return !(ContextCompat.checkSelfPermission(
            this,REQUESTED_PERMISSION[0]
        )!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,REQUESTED_PERMISSION[1]
                )!=PackageManager.PERMISSION_GRANTED)
    }
    private fun setupVideoSdkEngine(){
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = app_id
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableVideo()
        }
        catch(e: Exception)
        {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page22_vid_call)

        if(!checkSelfPermission()){
            ActivityCompat
                .requestPermissions(
                    this,REQUESTED_PERMISSION,PERMISSION_ID
                )
        }
        setupVideoSdkEngine()
        joinCall()
        val back1 = findViewById<LinearLayout>(R.id.cross)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }

    private fun leaveCall() {
        if(!isJoined)
        {
        }
        else{
            agoraEngine?.leaveChannel()
            if(remoteSurfaceView!=null) remoteSurfaceView!!.visibility = GONE
            if(localSurfaceView!=null) localSurfaceView!!.visibility = GONE

            isJoined = false
        }

    }
    override fun onDestroy() {
        super.onDestroy()

        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun joinCall() {
        if(checkSelfPermission()){
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION

            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token,channelName,uid,option)
        }
    }

    private val mRtcEventHandler:IRtcEngineEventHandler =
        object : IRtcEngineEventHandler(){
            override fun onUserJoined(uid: Int, elapsed: Int) {
                runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                isJoined=true
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                runOnUiThread {
                    remoteSurfaceView!!.visibility = GONE
                }
            }
        }
    private fun setupRemoteVideo(uid:Int){
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(false)
        val remoteView = findViewById<FrameLayout>(R.id.remortuser)
        remoteView.addView(remoteSurfaceView)

        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
    }
    private fun setupLocalVideo(){
        localSurfaceView = SurfaceView(baseContext)
        localSurfaceView!!.setZOrderMediaOverlay(true)
        val localView = findViewById<FrameLayout>(R.id.sendervid)
        localView.addView(localSurfaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                0
            )
        )
    }

    fun end_ac(view: View)
    {
        leaveCall()
        finish()
    }
}

