package com.mansoorali.i212502

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class page14_Activity_camera : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page14_camera)

        val vid1=findViewById<LinearLayout>(R.id.video)
        vid1.setOnClickListener {
            val intent=Intent(this, page15_Activity_video::class.java)
            startActivity(intent)
        }
        val vid2=findViewById<TextView>(R.id.video1)
        vid1.setOnClickListener {
            val intent=Intent(this, page15_Activity_video::class.java)
            startActivity(intent)
        }
        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }
}