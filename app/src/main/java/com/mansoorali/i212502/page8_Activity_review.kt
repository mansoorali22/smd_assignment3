package com.mansoorali.i212502

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class page8_Activity_review : AppCompatActivity() {
    lateinit var img1: ImageView
    lateinit var img2: ImageView
    lateinit var img3: ImageView
    lateinit var img4: ImageView
    lateinit var img5: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var user: String
    private var defulturi:String="https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page8_review)

        database= FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user =userID.toString()
        if (mentorID != null) {
            fetchUser(mentorID)
        }

        img1 = findViewById(R.id.s1)
        img2 = findViewById(R.id.s2)
        img3 = findViewById(R.id.s3)
        img4 = findViewById(R.id.s4)
        img5 = findViewById(R.id.s5)
        var star="0"

        img1.setOnClickListener {
            img1.setImageResource(R.drawable.star)
            img2.setImageResource(R.drawable.empty_star)
            img3.setImageResource(R.drawable.empty_star)
            img4.setImageResource(R.drawable.empty_star)
            img5.setImageResource(R.drawable.empty_star)
            star="1"
        }
        img2.setOnClickListener {
            img1.setImageResource(R.drawable.star)
            img2.setImageResource(R.drawable.star)
            img3.setImageResource(R.drawable.empty_star)
            img4.setImageResource(R.drawable.empty_star)
            img5.setImageResource(R.drawable.empty_star)
            star="2"
        }
        img3.setOnClickListener {
            img1.setImageResource(R.drawable.star)
            img2.setImageResource(R.drawable.star)
            img3.setImageResource(R.drawable.star)
            img4.setImageResource(R.drawable.empty_star)
            img5.setImageResource(R.drawable.empty_star)
            star="3"
        }
        img4.setOnClickListener {
            img1.setImageResource(R.drawable.star)
            img2.setImageResource(R.drawable.star)
            img3.setImageResource(R.drawable.star)
            img4.setImageResource(R.drawable.star)
            img5.setImageResource(R.drawable.empty_star)
            star="4"
        }
        img5.setOnClickListener {
            img1.setImageResource(R.drawable.star)
            img2.setImageResource(R.drawable.star)
            img3.setImageResource(R.drawable.star)
            img4.setImageResource(R.drawable.star)
            img5.setImageResource(R.drawable.star)
            star="5"
        }
        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
        val rview=findViewById<EditText>(R.id.myfeedback)
        val book =findViewById<Button>(R.id.feedback)
        book.setOnClickListener {
            Review(userID.toString(),rview.text.toString(),star,mentorID.toString())
            rview.setText("")
            img1.setImageResource(R.drawable.empty_star)
            img2.setImageResource(R.drawable.empty_star)
            img3.setImageResource(R.drawable.empty_star)
            img4.setImageResource(R.drawable.empty_star)
            img5.setImageResource(R.drawable.empty_star)
        }

    }

    private fun fetchUser(userID: String) {

        val namebase1 = FirebaseDatabase.getInstance().getReference("mentor")
        namebase1.child(userID.toString()).get().addOnSuccessListener { dataSnapshot ->
            val picM:String=dataSnapshot.child("uri").value.toString()
            if (!picM.isNullOrEmpty())
            {
                defulturi=picM

            }
        }

            //********** finding name *******
            val namebase = FirebaseDatabase.getInstance().getReference("user")
            val nameTextView = findViewById<TextView>(R.id.name)
            namebase.child(userID.toString()).get().addOnSuccessListener { dataSnapshot ->
                nameTextView.text = dataSnapshot.child("namee").value.toString()
                if(nameTextView.text=="null")
                    nameTextView.text=userID.toString()
                val pic=findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
                var uri=dataSnapshot.child("imguri").value.toString()
                if(uri.length>10)
                    Glide.with(this)
                        .load(uri)
                        .into(pic)
                else
                    Glide.with(this)
                        .load(defulturi)
                        .into(pic)



        }

    }
    private fun Review(userID: String, review: String, star:String, mentor:String) {
        val database = FirebaseDatabase.getInstance().getReference("review")

        database.child(userID).child(mentor).push()
        val btime=Myreview(star, review)
        database.child(userID).child(mentor).setValue(btime)
        Toast.makeText(this,"Feedback Submitted", Toast.LENGTH_SHORT).show()




    }
}