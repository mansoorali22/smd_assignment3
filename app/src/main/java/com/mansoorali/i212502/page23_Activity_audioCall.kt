package com.mansoorali.i212502

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class page23_Activity_audioCall : AppCompatActivity() {
    private val PERMISSION_ID = 12
    private val app_id =
        "9dd4f8ef62a6460e86c69e2255776232"
    private val channelName = "Channel"
    private val token = "007eJxTYLCd/vfaVZ6M5RreccErJ4jKvuW7sIix0V3+cgH/jSuSXpsUGCxTUkzSLFLTzIwSzUzMDFItzJLNLFONjExNzc3NjIyNPk8VSGsIZGT4+r+BkZEBAkF8dgbnjMS8vNQcBgYAabcf9A=="
    private val uid =0
    var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private lateinit var time: TextView
    private lateinit var handler: Handler
    private var secondsElapsed = 0
    private val REQUESTED_PERMISSION = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    private lateinit var database: DatabaseReference
    private var defulturi: String =
        "https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"

    private lateinit var user: String
    private lateinit var person: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page23_audio_call)

        database = FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user = userID.toString()
        person = mentorID.toString()
        if (mentorID != null) {
            fetchUser(mentorID)
        }

        if(!checkSelfPermission()){
            ActivityCompat
                .requestPermissions(
                    this,REQUESTED_PERMISSION,PERMISSION_ID
                )
        }
        val message = intent.getStringExtra("200")
        var x =0
        var count = 0

        var name= ""
        var image1 = ""

        setupVideoSdkEngine()

        joinCall()
        time=findViewById<TextView>(R.id.time)
        handler = Handler()

        // Start the timer when the activity is created

            startTimer()

        val back1 = findViewById<LinearLayout>(R.id.cross)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }

    private fun fetchUser(userID: String) {
        database = FirebaseDatabase.getInstance().getReference("mentor")
        database.child(userID).get().addOnSuccessListener {
            val uid = it.child("mail").value.toString()
            //********** finding name *******
            val namebase = FirebaseDatabase.getInstance().getReference("user")
            val nameTextView = findViewById<TextView>(R.id.name)
            namebase.child(uid.toString()).get().addOnSuccessListener { dataSnapshot ->
                nameTextView.text = dataSnapshot.child("namee").value.toString()
                if (nameTextView.text == "null")
                    nameTextView.text = uid.toString()
                val pic =
                    findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
                var uri = dataSnapshot.child("imguri").value.toString()
                if (uri.length > 10)
                    Glide.with(this)
                        .load(uri)
                        .into(pic)
                else
                    Glide.with(this)
                        .load(defulturi)
                        .into(pic)


            }.addOnFailureListener { exception ->
                // Handle failure if needed
            }


        }

    }

    private fun checkSelfPermission():Boolean{
        return !(ContextCompat.checkSelfPermission(
            this,REQUESTED_PERMISSION[0]
        )!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,REQUESTED_PERMISSION[1]
                )!=PackageManager.PERMISSION_GRANTED)
    }
    private fun showMessage(message:String)
    {
        runOnUiThread {
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
        }
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
            showMessage(e.message.toString())
        }
    }

    private fun startTimer() {
        // Create a Runnable to update the timer every second
        val runnable = object : Runnable {
            override fun run() {
                    // Increment seconds elapsed
                    secondsElapsed++

                    // Calculate minutes and seconds
                    val minutes = secondsElapsed / 60
                    val seconds = secondsElapsed % 60

                    // Update the TextView with the formatted time
                    time.text = String.format("%02d:%02d", minutes, seconds)

                    // Schedule the handler to run again after 1 second (1000 milliseconds)
                    handler.postDelayed(this, 1000)
                }

        }

        // Start the handler to run the Runnable
        handler.post(runnable)
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
        else
        {
            showMessage("Permission Not Granted")
        }
    }

    private val mRtcEventHandler:IRtcEngineEventHandler =
        object : IRtcEngineEventHandler(){
            override fun onUserJoined(uid: Int, elapsed: Int) {
                showMessage("Remote User Joined $uid")
                runOnUiThread { setupRemoteVideo(uid) }
            }

                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    isJoined = true
                    showMessage("Joined Channel $channel")

                    // Start the timer when the user successfully joins the channel
                    startTimer()
                }


            override fun onUserOffline(uid: Int, reason: Int) {
                showMessage("User Offline")

                runOnUiThread {
                    remoteSurfaceView!!.visibility = GONE
                }
            }
        }
    private fun setupRemoteVideo(uid:Int){
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(false)
//        val remoteView = findViewById<FrameLayout>(R.id.remoteF)
//        remoteView.addView(remoteSurfaceView)

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
//        val localView = findViewById<FrameLayout>(R.id.local)
//        localView.addView(localSurfaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                0
            )
        )
    }
}



