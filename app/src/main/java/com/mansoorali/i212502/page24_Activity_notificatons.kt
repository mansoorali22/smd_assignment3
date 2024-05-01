package com.mansoorali.i212502

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class page24_Activity_notificatons : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page24_notificatons) // Set your main activity layout here
        var database = FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")

        val recentSearchesArray: MutableList<String> = mutableListOf()
        // Declare database reference and mutable list
        val database1 = FirebaseDatabase.getInstance().getReference("notifics")

// Add a ValueEventListener to listen for changes in the notifications node
        database1.child(userID.toString()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("data N", dataSnapshot.toString())


                for (notificationSnapshot in dataSnapshot.children) {
                    // Get the value of the notification
                    val notification = notificationSnapshot.getValue(String::class.java)

                    // Check if the notification is not null before adding it to the recentSearchesArray
                    notification?.let {
                        recentSearchesArray.add(it)
                    }
                }

                // Now you can use the recentSearchesArray containing all notifications for further processing
                for (notification in recentSearchesArray) {
                    Log.d("Notification", notification)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })


        val handler = Handler(Looper.getMainLooper())
        super.onResume()
        handler.postDelayed({
            loadNoti(recentSearchesArray)
        }, 3000)

        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
    }

    private fun loadNoti(recentSearchesArray: MutableList<String>) {
        // Create a layout inflater
        val inflater = LayoutInflater.from(this)

        // Retrieve the main layout
        val mainLayout=findViewById<LinearLayout>(R.id.insertRecent2) // Change LinearLayout to your main layout type
        var counter = 1
        // Loop through the recent searches array
        for (search in recentSearchesArray) {
            Log.d("Notification layout", search)
            // Inflate the recent_search.xml layout for each search
            val recentSearchLayout = inflater.inflate(R.layout.notifications, null)

            // Retrieve the TextView and ImageButton from the inflated layout
            val recentSearchTextView = recentSearchLayout.findViewById<TextView>(R.id.recentSearch)
            val clearSearchImageButton = recentSearchLayout.findViewById<ImageButton>(R.id.clearSearch)

            // Set the text of the TextView
            recentSearchTextView.text = search
            if (counter == 4)
                recentSearchTextView.setTypeface(null, Typeface.BOLD)
            // Set OnClickListener to the clear button to remove the recent search
            clearSearchImageButton.setOnClickListener {
                // Remove the parent layout (recent search item) from the main layout
                counter++
                mainLayout.removeView(recentSearchLayout)

            }

            val clear = findViewById<TextView>(R.id.clear)
            clear.setOnClickListener {
                val linearLayout = findViewById<LinearLayout>(R.id.insertRecent2)
                linearLayout.removeAllViews()
            }
            // Add the inflated layout to the main layout
            mainLayout.addView(recentSearchLayout)

        }
    }
}


