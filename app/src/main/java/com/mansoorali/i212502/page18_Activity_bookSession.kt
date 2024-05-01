package com.mansoorali.i212502

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class page18_Activity_bookSession : AppCompatActivity() {

    private var bool = false // Initial state of the image
    private lateinit var database: DatabaseReference
    private lateinit var user: String
    private lateinit var dbMentor: DatabaseReference
    private lateinit var open:LinearLayout
    private var defulturi:String="https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page18_book_session) // Set your main activity layout here
        database= FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user =userID.toString()


        //---------------------------------Get Mentor data from firebase
        //Toast.makeText(this@page4_Activity_home,mntrlist.size.toString(), Toast.LENGTH_SHORT).show()
        val booklist = mutableListOf<Bookdetail>()
        val dbMentor = FirebaseDatabase.getInstance().getReference("book")

        dbMentor.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userID2 = userSnapshot.key

                    // Iterate through each booking for this user
                    if(userID==userID2) {
                        for (bookingSnapshot in userSnapshot.children) {
                            val mentor = bookingSnapshot.key
                            val date = bookingSnapshot.child("date").getValue(String::class.java)
                            val time = bookingSnapshot.child("time").getValue(String::class.java)

                            // Create a Bookdetail object and add it to the list
                            val booking = Bookdetail(mentor!!, date!!, time)
                            booklist.add(booking)
                            //Toast.makeText(this@page18_Activity_bookSession,mentor+date, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Now you have a list of all bookings
                // You can use booklist for further processing or display in your UI
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseError", "Error reading bookings: ${error.message}")
            }
        })




        // Inflate the layout for each item in the array
        val handler = Handler(Looper.getMainLooper())
        super.onResume()
        handler.postDelayed({
            if(booklist.isNotEmpty())
                topMentor(booklist)
        }, 4000)



        val back1 = findViewById<ImageView>(R.id.back)
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
                if(nameTextView.text=="null")
                    nameTextView.text=uid.toString()


            }.addOnFailureListener { exception ->
                // Handle failure if needed
            }


        }

    }
    private fun topMentor(booklist : MutableList<Bookdetail>){

        val container = findViewById<LinearLayout>(R.id.insertRecent2)
        Log.d("MyTag", "entered function")
        var r=""
        container.removeAllViews()
        for (booked in booklist) {

            if (booked.mentor.toString()!=user.toString()) {

                //Toast.makeText(this@page4_Activity_home,mntrlist[0].mail, Toast.LENGTH_SHORT).show()
                val itemView = layoutInflater.inflate(R.layout.book_session_people, null)

                // Retrieve img2 within the itemView
                val img2 = itemView.findViewById<ImageView>(R.id.profilepic)
                val goto =itemView.findViewById<LinearLayout>(R.id.click)

                // Populate the item view with mentor data

                //********** finding name *******
                val namebase = FirebaseDatabase.getInstance().getReference("user")
                val nameTextView = itemView.findViewById<TextView>(R.id.name)
                namebase.child(booked.mentor.toString()).get().addOnSuccessListener { dataSnapshot ->
                    nameTextView.text = dataSnapshot.child("namee").value.toString()
                    if(nameTextView.text=="null")
                        nameTextView.text=booked.mentor.toString()
                    var uri=dataSnapshot.child("imguri").value.toString()
                    if(uri.length>10)
                    Glide.with(this)
                        .load(uri)
                        .into(img2)
                    else
                        Glide.with(this)
                            .load(defulturi)
                            .into(img2)

                }.addOnFailureListener { exception ->
                    // Handle failure if needed
                }
                //****************************
                val postTextView = itemView.findViewById<TextView>(R.id.post)
                val mentorbase = FirebaseDatabase.getInstance().getReference("mentor")
                mentorbase.child(booked.mentor.toString()).get().addOnSuccessListener { dataSnapshot ->
                    postTextView.text = dataSnapshot.child("disc").value.toString()



                }.addOnFailureListener { exception ->
                    // Handle failure if needed
                }


                val availTextView = itemView.findViewById<TextView>(R.id.date)
                availTextView.text = booked.date

                val priceTextView = itemView.findViewById<TextView>(R.id.time)
                priceTextView.text = booked.time


//                goto.setOnClickListener {
//                    val intent = Intent(this, page7_Activity_profile::class.java)
//                    intent.putExtra("USER_ID", user)
//                    intent.putExtra("PROFILE", mentor.mail.toString())
//                    startActivity(intent)
//                }
                //**********************************




                // Add the inflated item view to the container

                container.addView(itemView)

            }
        }


    }
}

data class ItemData2(
    val name: String,
    val post: String,
    val date: String,
    val time: String
)
