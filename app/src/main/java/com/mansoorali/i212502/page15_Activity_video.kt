package com.mansoorali.i212502

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class page15_Activity_video : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page15_video)

        val vid1=findViewById<LinearLayout>(R.id.photo)
        vid1.setOnClickListener {
            val intent= Intent(this, page14_Activity_camera::class.java)
            startActivity(intent)
        }
        val vid2=findViewById<TextView>(R.id.photo1)
        vid1.setOnClickListener {
            val intent= Intent(this, page14_Activity_camera::class.java)
            startActivity(intent)
        }
        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }
}