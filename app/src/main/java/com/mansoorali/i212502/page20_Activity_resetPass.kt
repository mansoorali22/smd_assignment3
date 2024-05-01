package com.mansoorali.i212502

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class page20_Activity_resetPass : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page20_reset_pass)
        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }

        val send = findViewById<Button>(R.id.reset)
        send.setOnClickListener {
            Toast.makeText(this, "Password Reset", Toast.LENGTH_SHORT).show()
        }
        val login = findViewById<TextView>(R.id.login)
        login.setOnClickListener {
            val intent=Intent(this, page2_Activity_login::class.java)
            startActivity(intent)
        }

    }
}