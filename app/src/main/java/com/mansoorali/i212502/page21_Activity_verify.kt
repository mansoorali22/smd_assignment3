package com.mansoorali.i212502

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class page21_Activity_verify : AppCompatActivity() {
    private val verificationCode = StringBuilder() // StringBuilder to store the entered verification code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page21_verify)

        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }

        val send = findViewById<Button>(R.id.verify)
        send.setOnClickListener {
            val intent= Intent(this, page20_Activity_resetPass::class.java)
            startActivity(intent)
        }



    }
}