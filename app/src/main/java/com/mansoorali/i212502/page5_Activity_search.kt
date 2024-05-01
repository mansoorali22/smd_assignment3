package com.mansoorali.i212502

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference

class page5_Activity_search : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page5_search) // Set your main activity layout here

        val userID = intent.getStringExtra("USER_ID")
        user = userID.toString()



        // Example array of recent searches
        val recentSearchesArray = arrayOf("Mentor 1", "Mentor 2", "Mentor 3")

        // Create a layout inflater
        val inflater = LayoutInflater.from(this)

        // Retrieve the main layout
        val mainLayout = findViewById<LinearLayout>(R.id.insertRecent) // Change LinearLayout to your main layout type

        // Loop through the recent searches array
        for (search in recentSearchesArray) {
            // Inflate the recent_search.xml layout for each search
            val recentSearchLayout = inflater.inflate(R.layout.recent_searches, null)

            // Retrieve the TextView and ImageButton from the inflated layout
            val recentSearchTextView = recentSearchLayout.findViewById<TextView>(R.id.recentSearch)
            val clearSearchImageButton = recentSearchLayout.findViewById<ImageButton>(R.id.clearSearch)

            // Set the text of the TextView
            recentSearchTextView.text = search

            // Set OnClickListener to the clear button to remove the recent search
            clearSearchImageButton.setOnClickListener {
                // Remove the parent layout (recent search item) from the main layout
                mainLayout.removeView(recentSearchLayout)
            }

            // Add the inflated layout to the main layout
            mainLayout.addView(recentSearchLayout)

        }
        val home = findViewById<LinearLayout>(R.id.home)

        home.setOnClickListener {
            val intent = Intent(this, page4_Activity_home::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }
        val add = findViewById<LinearLayout>(R.id.plus)

        add.setOnClickListener {
            val intent = Intent(this, page9_Activity_add::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }
        val search1 = findViewById<LinearLayout>(R.id.searchView)

        search1.setOnClickListener {
            val intent = Intent(this, page6_Activity_search2::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)

        }

        val chat = findViewById<LinearLayout>(R.id.chat)

        chat.setOnClickListener {
            val intent = Intent(this, page11_Activity2_chats::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }

        val profile=findViewById<LinearLayout>(R.id.profile)
        profile.setOnClickListener {
            val intent=Intent(this, page17_Activity_profileView::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }

        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }


}
