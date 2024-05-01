package com.mansoorali.i212502

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)

        // Example of reading the stored value
        val storedUserID = sharedPreferences.getString("Login", null)

        Handler().postDelayed({
            if(storedUserID=="" || storedUserID.isNullOrEmpty()) {
                val intent = Intent(this, page2_Activity_login::class.java)
                startActivity(intent)
            }
            else
            {
                val intent = Intent(this, page4_Activity_home::class.java)
                intent.putExtra("USER_ID", storedUserID) // Put the userID as an extra in the Intent
                startActivity(intent)
            }
            finish() // Optional: Finish the current activity if you don't want to go back to it
        }, 5000)
    }


}