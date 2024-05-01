package com.mansoorali.i212502

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class page4_Activity_home : AppCompatActivity() {
    lateinit var img2: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var dbMentor: DatabaseReference
    private lateinit var user: String


    lateinit var search : ImageView
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page4_home)

        database= FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        user =userID.toString()


        if (userID != null) {
            fetchUser(userID)
        }

        var mntrlist= mutableListOf<Mentors>()
        //mntrlist.clear()
//---------------------------------Get Mentor data from firebase
        getFCMToken()
        dbMentor = FirebaseDatabase.getInstance().getReference("mentor")

        dbMentor.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (mntosnap in snapshot.children) {


                    // Retrieve data from the database
                    val name = mntosnap.child("mail").getValue(String::class.java)
                    val price = mntosnap.child("price").getValue(String::class.java)
                    val disc = mntosnap.child("disc").getValue(String::class.java)
                    val status = mntosnap.child("status").getValue(String::class.java)

                    val mdata = Mentors(name, status, price, disc)
                    mntrlist.add(mdata)
                    //Toast.makeText(this@page4_Activity_home,mntrlist.size.toString(), Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@page4_Activity_home,"error", Toast.LENGTH_SHORT).show()
                // Handle onCancelled event (optional)
            }


        })





//----------------------------------------top mentor

        //val inflater = LayoutInflater.from(this)
//        val mdata = Mentors("hbvh", "fgfghgkkj", "4565", "jbhfghdrvgnb")
//        mntrlist.add(mdata)
        Log.d("MyTag", "This is a debug message")

        val view1=findViewById<TextView>(R.id.view1)
        Log.d("MyTag", "Button clicked")

        view1.setOnClickListener {
            topMentor(mntrlist)
        }

//----------------------------------------educated mentor


        val view2=findViewById<TextView>(R.id.view2)

        view2.setOnClickListener {
            eduMentor(mntrlist)

        }
        val view3=findViewById<TextView>(R.id.view3)

        view3.setOnClickListener {
            recentMentor(mntrlist)

        }

//----------------------------------------recent mentor
        askNotificationPermission()

        val search = findViewById<LinearLayout>(R.id.search)

        search.setOnClickListener {
            val intent = Intent(this, page5_Activity_search::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }
        val add = findViewById<LinearLayout>(R.id.plus)

        add.setOnClickListener {
            val intent = Intent(this, page9_Activity_add::class.java)
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

        val notify=findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.noti)
        notify.setOnClickListener{
            val intent=Intent(this, page24_Activity_notificatons::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }
        val logout=findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            sharedPreferences = getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)

            // Change the stored value to an empty string
            sharedPreferences.edit().putString("Login", "").apply()
            val intent=Intent(this, page2_Activity_login::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
        }


    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("My Msg token", token)
                val namebase = FirebaseDatabase.getInstance().getReference("user")
                namebase.child(user).child("token").setValue(token)
            } else {
                Log.e("My Msg token", "Fetching FCM token failed: ${task.exception?.message}")
            }
        }
    }
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "allowed1 noti", Toast.LENGTH_SHORT).show()
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(this, "allowed2 noti", Toast.LENGTH_SHORT).show()
            } else {
                // Directly ask for the permission
                Toast.makeText(this, "allowed3 noti", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////
     fun topMentor(mntrlist: MutableList<Mentors>) {

        val container = findViewById<LinearLayout>(R.id.layer1)
        container.removeAllViews()
        for (mentor in mntrlist) {
            Log.d("MyTag", "entered function")
            if (mentor.status == "Available" && mentor.mail.toString()!=user.toString()) {
                //Toast.makeText(this@page4_Activity_home,mntrlist[0].mail, Toast.LENGTH_SHORT).show()
                val itemView = layoutInflater.inflate(R.layout.profiles_home, null)

                // Retrieve img2 within the itemView
                val img2 = itemView.findViewById<ImageView>(R.id.like)

                // Populate the item view with mentor data

                //********** finding name *******
                val namebase = FirebaseDatabase.getInstance().getReference("user")
                val nameTextView = itemView.findViewById<TextView>(R.id.name)
                namebase.child(mentor.mail.toString()).get().addOnSuccessListener { dataSnapshot ->
                    nameTextView.text = dataSnapshot.child("namee").value.toString()
                    if(nameTextView.text=="null")
                        nameTextView.text=mentor.mail.toString()

                }.addOnFailureListener { exception ->
                    // Handle failure if needed
                }
                //****************************

                val postTextView = itemView.findViewById<TextView>(R.id.disc)
                postTextView.text = mentor.disc

                val availTextView = itemView.findViewById<TextView>(R.id.avail)
                availTextView.text = mentor.status

                val priceTextView = itemView.findViewById<TextView>(R.id.price)
                priceTextView.text = mentor.price

                //****** Finding favourites *********
                var bool=false
                val likebase = FirebaseDatabase.getInstance().getReference("favourite")
                likebase.child(user).get().addOnSuccessListener { dataSnapshot ->
                    val namee = dataSnapshot.child("fmail").value.toString().replace("[", "")
                        .replace("]", "")
                    val fmail = namee.split(",") // Assuming your string is comma-separated

                    val updatedList = fmail.toMutableList()
                    val modifiedList = mutableListOf<String>()
                    for(aa in updatedList)      //remove space from start of the word
                    {
                        if (aa[0]==' ') {
                            modifiedList.add(aa.substring(1))
                        }
                        else
                            modifiedList.add(aa)
                    }
                    var nam=true

//                        val f = Favoutites(user, modifiedList)
//                        likebase.child(user).setValue(f)
                    nam=modifiedList.any{it==mentor.mail.toString()}

                    if (nam == true) {
                        img2.setImageResource(R.drawable.heart)

                        nam = false
                    } else {
                        img2.setImageResource(R.drawable.heart_e)
                        nam = true
                    }
                    bool=nam

                }
                img2.setOnClickListener {
                    // Handle click for img2 within this item view
                    if (bool == true) {
                        favour(user.toString(),mentor.mail.toString())
                        img2.setImageResource(R.drawable.heart)
                        bool = false
                    } else {
                        rmfavour(user.toString(),mentor.mail.toString())
                        img2.setImageResource(R.drawable.heart_e)
                        bool = true
                    }
                    //eduMentor(mntrlist)
                }
                //**********************************
                val goto =itemView.findViewById<LinearLayout>(R.id.profileforsearch)
                goto.setOnClickListener {
                    val intent = Intent(this, page7_Activity_profile::class.java)
                    intent.putExtra("USER_ID", user)
                    intent.putExtra("PROFILE", mentor.mail.toString())
                    startActivity(intent)
                }



                // Add the inflated item view to the container

                container.addView(itemView)

            }
        }

    }

    private fun eduMentor(mntrlist: MutableList<Mentors>) {
        val container2 = findViewById<LinearLayout>(R.id.layer2)
        container2.removeAllViews()
        for (mentor in mntrlist) {
            if (mentor.mail.toString() != user.toString()) {
                val itemView = layoutInflater.inflate(R.layout.profiles_home, null)

                // Retrieve img2 within the itemView
                val img2 = itemView.findViewById<ImageView>(R.id.like)

                // Populate the item view with mentor data


                //********** finding name *******
                val namebase = FirebaseDatabase.getInstance().getReference("user")
                val nameTextView = itemView.findViewById<TextView>(R.id.name)
                namebase.child(mentor.mail.toString()).get().addOnSuccessListener { dataSnapshot ->
                    nameTextView.text = dataSnapshot.child("namee").value.toString()
                    if (nameTextView.text == "null")
                        nameTextView.text = mentor.mail.toString()

                }.addOnFailureListener { exception ->
                    // Handle failure if needed
                }
                //****************************

                val postTextView = itemView.findViewById<TextView>(R.id.disc)
                postTextView.text = mentor.disc

                val availTextView = itemView.findViewById<TextView>(R.id.avail)
                availTextView.text = mentor.status

                val priceTextView = itemView.findViewById<TextView>(R.id.price)
                priceTextView.text = mentor.price

                ///****** Finding favourites *********
                var bool = true
                val likebase = FirebaseDatabase.getInstance().getReference("favourite")
                likebase.child(user).get().addOnSuccessListener { dataSnapshot ->
                    val namee = dataSnapshot.child("fmail").value.toString().replace("[", "")
                        .replace("]", "")
                    val fmail = namee.split(",") // Assuming your string is comma-separated

                    val updatedList = fmail.toMutableList()
                    val modifiedList = mutableListOf<String>()
                    for (aa in updatedList)      //remove space from start of the word
                    {
                        if (aa[0] == ' ') {
                            modifiedList.add(aa.substring(1))
                        } else
                            modifiedList.add(aa)
                    }
                    var nam = true

//                    val f = Favoutites(user, modifiedList)
//                    likebase.child(user).setValue(f)
                    nam = modifiedList.any { it == mentor.mail.toString() }

                    if (nam == true) {
                        img2.setImageResource(R.drawable.heart)

                        nam = false
                    } else {
                        img2.setImageResource(R.drawable.heart_e)

                        nam = true
                    }
                    bool = nam

                }
                img2.setOnClickListener {
                    // Handle click for img2 within this item view
                    if (bool == true) {
                        favour(user.toString(), mentor.mail.toString())
                        img2.setImageResource(R.drawable.heart)
                        bool = false
                    } else {
                        rmfavour(user.toString(), mentor.mail.toString())
                        img2.setImageResource(R.drawable.heart_e)
                        bool = true
                    }
                    //topMentor(mntrlist)
                }
                //**********************************
                val goto =itemView.findViewById<LinearLayout>(R.id.profileforsearch)
                goto.setOnClickListener {
                    val intent = Intent(this, page7_Activity_profile::class.java)
                    intent.putExtra("USER_ID", user)
                    intent.putExtra("PROFILE", mentor.mail.toString())
                    startActivity(intent)
                }

                // Add the inflated item view to the container

                container2.addView(itemView)


            }
        }


    }

    private fun recentMentor(mntrlist: MutableList<Mentors>) {
        val container2 = findViewById<LinearLayout>(R.id.layer3)
        container2.removeAllViews()
        for (mentor in mntrlist) {
            if (mentor.mail.toString() != user.toString()) {
                val itemView = layoutInflater.inflate(R.layout.profiles_home, null)

                // Retrieve img2 within the itemView
                val img2 = itemView.findViewById<ImageView>(R.id.like)

                // Populate the item view with mentor data


                //********** finding name *******
                val namebase = FirebaseDatabase.getInstance().getReference("user")
                val nameTextView = itemView.findViewById<TextView>(R.id.name)
                namebase.child(mentor.mail.toString()).get().addOnSuccessListener { dataSnapshot ->
                    nameTextView.text = dataSnapshot.child("namee").value.toString()
                    if (nameTextView.text == "null")
                        nameTextView.text = mentor.mail.toString()

                }.addOnFailureListener { exception ->
                    // Handle failure if needed
                }
                //****************************

                val postTextView = itemView.findViewById<TextView>(R.id.disc)
                postTextView.text = mentor.disc

                val availTextView = itemView.findViewById<TextView>(R.id.avail)
                availTextView.text = mentor.status

                val priceTextView = itemView.findViewById<TextView>(R.id.price)
                priceTextView.text = mentor.price

                ///****** Finding favourites *********
                var bool = true
                val likebase = FirebaseDatabase.getInstance().getReference("favourite")
                likebase.child(user).get().addOnSuccessListener { dataSnapshot ->
                    val namee = dataSnapshot.child("fmail").value.toString().replace("[", "")
                        .replace("]", "")
                    val fmail = namee.split(",") // Assuming your string is comma-separated

                    val updatedList = fmail.toMutableList()
                    val modifiedList = mutableListOf<String>()
                    for (aa in updatedList)      //remove space from start of the word
                    {
                        if (aa[0] == ' ') {
                            modifiedList.add(aa.substring(1))
                        } else
                            modifiedList.add(aa)
                    }
                    var nam = true

//                    val f = Favoutites(user, modifiedList)
//                    likebase.child(user).setValue(f)
                    nam = modifiedList.any { it == mentor.mail.toString() }

                    if (nam == true) {
                        img2.setImageResource(R.drawable.heart)

                        nam = false
                    } else {
                        img2.setImageResource(R.drawable.heart_e)

                        nam = true
                    }
                    bool = nam

                }
                img2.setOnClickListener {
                    // Handle click for img2 within this item view
                    if (bool == true) {
                        favour(user.toString(), mentor.mail.toString())
                        img2.setImageResource(R.drawable.heart)
                        bool = false
                    } else {
                        rmfavour(user.toString(), mentor.mail.toString())
                        img2.setImageResource(R.drawable.heart_e)
                        bool = true
                    }
                    //topMentor(mntrlist)
                }
                //**********************************
                val goto =itemView.findViewById<LinearLayout>(R.id.profileforsearch)
                goto.setOnClickListener {
                    val intent = Intent(this, page7_Activity_profile::class.java)
                    intent.putExtra("USER_ID", user)
                    intent.putExtra("PROFILE", mentor.mail.toString())
                    startActivity(intent)
                }

                // Add the inflated item view to the container

                container2.addView(itemView)


            }
        }


    }


    private fun fetchUser(userID: String) {
        database = FirebaseDatabase.getInstance().getReference("user")
        database.child(userID).get().addOnSuccessListener {
            val nam = it.child("namee").value.toString()

            val name = findViewById<TextView>(R.id.name)
            name.text = nam
        }

    }




    private fun favour(userID: String, like: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("favourite")
        val database = FirebaseDatabase.getInstance().getReference("favourite")

        database.child(userID).get().addOnSuccessListener { dataSnapshot ->
            val namee = dataSnapshot.child("fmail").value.toString().replace("[", "")
                .replace("]", "")

            val fmail = namee.split(",") // Assuming your string is comma-separated

            val likeString = like.toString()
            val updatedList = fmail.toMutableList()
            val modifiedList = mutableListOf<String>()
            for(aa in updatedList)      //remove space from start of the word
            {
                if (aa[0]==' ') {
                    modifiedList.add(aa.substring(1))
                }
                else
                    modifiedList.add(aa)
            }
            val present=modifiedList.any{it==like}
            modifiedList.add(likeString)


            // Create the Favoutites object and set its value in the database
            if (present==false){
                val f = Favoutites(userID, modifiedList)
                dbRef.child(userID).setValue(f)
                sendNotification("Added you in favourites", like)
                val str="Your favourite list is expanding"
                val database = FirebaseDatabase.getInstance().getReference("notifics").child(like)
                val newMessageRef = database.push()
                newMessageRef.setValue(str)
            }
        }.addOnFailureListener { exception ->
            // Handle failure if needed
        }
    }

    private fun rmfavour(userID :String, like:String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("favourite")
        val database = FirebaseDatabase.getInstance().getReference("favourite")

        database.child(userID).get().addOnSuccessListener { dataSnapshot ->
            val namee = dataSnapshot.child("fmail").value.toString().replace("[", "")
                .replace("]", "")
            val fmail = namee.split(",") // Assuming your string is comma-separated
            val likeString = like.toString()
            val updatedList = fmail.toMutableList()
            val modifiedList = mutableListOf<String>()
            for(aa in updatedList)      //remove space from start of the word
            {
                if (aa[0]==' ') {
                    modifiedList.add(aa.substring(1))
                }
                else
                    modifiedList.add(aa)
            }
//            for (aa in updatedList)
//            {
//                Toast.makeText(this@page4_Activity_home,aa.toString(), Toast.LENGTH_LONG).show()
//            }

            modifiedList.remove(like)
            var like2=" "+like

            updatedList.remove(like2)
            // Create the Favoutites object and set its value in the database
            val f = Favoutites(userID, modifiedList)
            dbRef.child(userID).setValue(f)
        }.addOnFailureListener { exception ->
            // Handle failure if needed
        }
    }
    private fun sendNotification(message: String,person:String) {
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
                    FirebaseDatabase.getInstance().getReference("user").child(person)
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