package com.mansoorali.i212502

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class page7_Activity_profile : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var user: String
    private var defulturi:String="https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page7_profile)
        database= FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user =userID.toString()
        if (mentorID != null) {
            fetchUser(mentorID)
        }


        // Initialize the TextView
        val gabout: TextView = findViewById(R.id.about)

        // Define the about text
        val about = "I am a passionate UX designer at Google with a focus on " +
                "creating user-centric and intuitive interfaces. With 10 years of " +
                "experience, I have had the opportunity to work on diverse " +
                "projects that have shaped my understanding of design " +
                "principles and user experience."


        gabout.text = about
        val review1 = findViewById<LinearLayout>(R.id.review)

        review1.setOnClickListener {
            val intent = Intent(this, page8_Activity_review::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }
        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
        val book =findViewById<Button>(R.id.book_session)
        book.setOnClickListener {
            val intent = Intent(this, page10_Activity2_calendar::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }

        val community = findViewById<LinearLayout>(R.id.join_com)

        community.setOnClickListener {
            val intent = Intent(this, page13_Activity_chat3::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }
    }

    private fun fetchUser(userID: String) {

        //*************** finding disc ******
        val namebase1 = FirebaseDatabase.getInstance().getReference("mentor")
        namebase1.child(userID.toString()).get().addOnSuccessListener { dataSnapshot ->

            val disc = findViewById<TextView>(R.id.disc)
            disc.text = dataSnapshot.child("disc").value.toString()
            val picM:String=dataSnapshot.child("uri").value.toString()
            if (picM[0]=='h')
            {
                defulturi=picM

            }
        }
        //********** finding name *******
        val namebase = FirebaseDatabase.getInstance().getReference("user")
        val nameTextView = findViewById<TextView>(R.id.name)
        namebase.child(userID.toString()).get().addOnSuccessListener { dataSnapshot ->
            nameTextView.text = dataSnapshot.child("namee").value.toString()
            if (nameTextView.text == "null")
                nameTextView.text = userID.toString()
            val pic = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
            var uri = dataSnapshot.child("imguri").value.toString()
            if (uri.length > 10)
                Glide.with(this)
                    .load(uri)
                    .into(pic)
            else
                Glide.with(this)
                    .load(defulturi)
                    .into(pic)
        }




    }
}
