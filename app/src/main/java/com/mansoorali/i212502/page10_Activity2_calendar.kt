package com.mansoorali.i212502

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class page10_Activity2_calendar : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var defulturi: String =
        "https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"

    private lateinit var user: String
    private lateinit var person: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page10_activity2_calendar)

        database = FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user = userID.toString()
        person = mentorID.toString()
        if (mentorID != null) {
            fetchUser(mentorID)
        }

        val calendarView = findViewById<CalendarView>(R.id.calender)
        val dateF = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentD = java.util.Date()
        var formattedDate = dateF.format(currentD)


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Create a Calendar instance and set the selected date
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            // Format the date as Day-Month-Year
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            formattedDate = dateFormat.format(calendar.time).toString()

            // Display the formatted date
            //Toast.makeText(this, "Selected Date: $formattedDate", Toast.LENGTH_SHORT).show()
        }
        var stime = "11.00AM"

        val t10 = findViewById<TextView>(R.id.t10)
        val t11 = findViewById<TextView>(R.id.t11)
        val t12 = findViewById<TextView>(R.id.t12)
        t11.setBackgroundColor(Color.parseColor("#42A399"))
        t10.setBackgroundColor(Color.GRAY)
        t12.setBackgroundColor(Color.GRAY)
        t10.setOnClickListener {
            stime = "10.00AM"
            t10.setBackgroundColor(Color.parseColor("#42A399"))
            t11.setBackgroundColor(Color.GRAY)
            t12.setBackgroundColor(Color.GRAY)
        }
        t11.setOnClickListener {
            stime = "11.00AM"
            t11.setBackgroundColor(Color.parseColor("#42A399"))
            t10.setBackgroundColor(Color.GRAY)
            t12.setBackgroundColor(Color.GRAY)
        }
        t12.setOnClickListener {
            stime = "12.00AM"
            t12.setBackgroundColor(Color.parseColor("#42A399"))
            t11.setBackgroundColor(Color.GRAY)
            t10.setBackgroundColor(Color.GRAY)
        }


        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
        val book = findViewById<Button>(R.id.booked)
        book.setOnClickListener {

            Boook(userID.toString(), formattedDate, stime, mentorID.toString())
            val str="You have a new booking"
            val database = FirebaseDatabase.getInstance().getReference("notifics").child(mentorID.toString())
            val newMessageRef = database.push()
            newMessageRef.setValue(str)

        }
        val chat = findViewById<LinearLayout>(R.id.chat)
        chat.setOnClickListener {
            val intent = Intent(this, page12_Activity_chat2::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }
        val vidcall = findViewById<LinearLayout>(R.id.vidaCal)
        vidcall.setOnClickListener {
            val intent = Intent(this, page22_Activity_vidCall::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }

        val call = findViewById<LinearLayout>(R.id.call)
        call.setOnClickListener {
            val intent = Intent(this, page23_Activity_audioCall::class.java)
            intent.putExtra("USER_ID", user)
            intent.putExtra("PROFILE", mentorID.toString())
            startActivity(intent)
        }

    }

    private fun fetchUser(userID: String) {
        database = FirebaseDatabase.getInstance().getReference("mentor")
        database.child(userID).get().addOnSuccessListener {
            val uid = it.child("mail").value.toString()
            val picM:String=it.child("uri").value.toString()
            if (!picM.isNullOrEmpty())
            {
                defulturi=picM

            }

            //********** finding name *******
            val namebase = FirebaseDatabase.getInstance().getReference("user")
            val nameTextView = findViewById<TextView>(R.id.name)
            namebase.child(uid.toString()).get().addOnSuccessListener { dataSnapshot ->
                nameTextView.text = dataSnapshot.child("namee").value.toString()
                if (nameTextView.text == "null")
                    nameTextView.text = uid.toString()
                val pic =
                    findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
                var uri = dataSnapshot.child("imguri").value.toString()
                if (uri.length > 10)
                    Glide.with(this)
                        .load(uri)
                        .into(pic)
                else
                    Glide.with(this)
                        .load(defulturi)
                        .into(pic)


            }.addOnFailureListener { exception ->
                // Handle failure if needed
            }


        }

    }

    private fun Boook(userID: String, date: String, time: String, mentor: String) {
        val database = FirebaseDatabase.getInstance().getReference("book")

        database.child(userID).child(mentor).push()
        val btime = BookSession(date, time)
        database.child(userID).child(mentor).setValue(btime)
        Toast.makeText(this, "Appointment is booked", Toast.LENGTH_SHORT).show()
        sendNotification("Booked an appointment")

    }

    private fun sendNotification(message: String) {
        // Variables to hold username and other user's token
        var username: String? = null
        var othertoken: String? = null

        // Reference to the current user's node in the database
        val currentUserRef =
            FirebaseDatabase.getInstance().getReference("user").child(user.toString())

        // Retrieve the current user's username asynchronously
        currentUserRef.child("namee").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                username = dataSnapshot.value.toString()

                // Once the username is retrieved, proceed to retrieve the other user's token
                val otherUserRef =
                    FirebaseDatabase.getInstance().getReference("user").child(person.toString())
                otherUserRef.child("token")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(otherTokenSnapshot: DataSnapshot) {
                            othertoken = otherTokenSnapshot.value.toString()

                            // Now, you have both the username and the other user's token, proceed to send the notification
                            if (username != null && othertoken != null) {
                                sendNotificationToOtherUser(username!!, othertoken!!, message)
                            } else {
                                // Handle the case where either the username or the other user's token is null
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors that occur while retrieving the other user's token
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur while retrieving the current user's username
            }
        })
    }

    private fun sendNotificationToOtherUser(username: String, othertoken: String, message: String) {
        try {
            // Create JSON objects for notification and data
            val notificationObj = JSONObject()
            notificationObj.put("title", username)
            notificationObj.put("body", message)

            val dataObj = JSONObject()
            dataObj.put("userId", user.toString())

            // Create the main JSON object for the FCM request
            val jsonObject = JSONObject()
            jsonObject.put("notification", notificationObj)
            jsonObject.put("data", dataObj)
            jsonObject.put("to", othertoken)

            callAPI(jsonObject)
        } catch (e: Exception) {
            // Handle any exceptions that occur during JSON object creation
        }
    }

    fun callAPI(jsonObject: JSONObject) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"

        val requestBody = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header(
                "Authorization",
                "Bearer AAAAjvnlv6Y:APA91bHgDlJZ2h504qWrXafG89CzgU6VQJQQkBfuU_53Rj0Y-ZMMjmFDvmNNw1vR2sFeERHELSsman0236A48Bl_bplCvmn_d2TAY2kJSTVhrcrLNB9vGEaK51c4uqBl_HMWZB1RFaMG"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // Handle success response
                val responseData = response.body?.string()
                // Process responseData as needed
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }
        })

    }
}

