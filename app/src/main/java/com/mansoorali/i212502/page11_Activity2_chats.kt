package com.mansoorali.i212502

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

class page11_Activity2_chats : AppCompatActivity() {
    private lateinit var img2: ImageView
    private lateinit var open:LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var user: String
    private lateinit var personID:String
    private var defulturi:String="https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"
    private var msgid:String=""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page11_activity2_chats) // Set your main activity layout here

        database= FirebaseDatabase.getInstance().getReference("user")
        val userID = intent.getStringExtra("USER_ID")
        val mentorID = intent.getStringExtra("PROFILE")
        user =userID.toString()
        personID=mentorID.toString()

        val databaseReference = FirebaseDatabase.getInstance().getReference("chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                LoadMsg(userID.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })

        val databaseReference2 = FirebaseDatabase.getInstance().getReference("Commchat")

        databaseReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                LoadCommMsg(userID.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })





        val home = findViewById<LinearLayout>(R.id.home)

        home.setOnClickListener {
            val intent = Intent(this, page4_Activity_home::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }
        val add = findViewById<LinearLayout>(R.id.plus)

        add.setOnClickListener {
            val intent = Intent(this, page9_Activity_add::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }
        val search = findViewById<LinearLayout>(R.id.search)

        search.setOnClickListener {
            val intent = Intent(this, page5_Activity_search::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }

        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }
        val profile=findViewById<LinearLayout>(R.id.profile)
        profile.setOnClickListener {
            val intent=Intent(this, page17_Activity_profileView::class.java)
            intent.putExtra("USER_ID", user)
            startActivity(intent)
        }
    }

    private fun LoadMsg(userID: String){
        //--------------------------------- read chats

        Log.d("My tag", "Entered function")
        var chatlist=ArrayList<Person>()
        //chatlist.clear()

        val dbMentor = FirebaseDatabase.getInstance().getReference("chat")

        dbMentor.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userID2 = userSnapshot.key
                    Log.d("My tag 1", userID2.toString())

                    // Iterate through each booking for this user

                        for (chatSnapshot in userSnapshot.children) {
                            if(userID==chatSnapshot.child("id").getValue(String::class.java)) {
                                Log.d("My tag 2", chatSnapshot.toString())
                                val mentor = chatSnapshot.child("recId").getValue(String::class.java)
                                Log.d("My tag:-", mentor.toString())

                                var uri:String=""
                                var name:String=""
                                // Create a Bookdetail object and add it to the list
                                val booking = Person(mentor!!,name!!,  uri,"No new Message" )
                                chatlist.add(booking)
                                //Toast.makeText(this@page12_Activity_chat2,tmsg, Toast.LENGTH_SHORT).show()
                            }
                            else if(userID==chatSnapshot.child("recId").getValue(String::class.java)) {
                                Log.d("My tag2", chatSnapshot.toString())
                                val mentor = chatSnapshot.child("id").getValue(String::class.java)
                                Log.d("My tag2:-", mentor.toString())


                                val namebase = FirebaseDatabase.getInstance().getReference("user")
                                var uri:String=""
                                var name:String=""

                                namebase.child(mentor.toString()).get().addOnSuccessListener { dataSnapshot ->

                                    uri=dataSnapshot.child("imguri").value.toString()
                                    name=dataSnapshot.child("namee").value.toString()

                                }

                                // Create a Bookdetail object and add it to the list
                                val booking = Person(mentor!!,name,  uri,"No new Message" )
                                chatlist.add(booking)
                                //Toast.makeText(this@page12_Activity_chat2,tmsg, Toast.LENGTH_SHORT).show()
                            }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseError", "Error reading chats: ${error.message}")
            }
        })

        val databaseReference = FirebaseDatabase.getInstance().getReference("chat")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatMentor(chatlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })

//        val handler = Handler(Looper.getMainLooper())
//        super.onResume()
//        handler.postDelayed({
//
//        }, 1500)


        Log.d("My tag", "out of function")

    }
    private fun LoadCommMsg(userID: String){
        //--------------------------------- read chats

        Log.d("My tag", "Entered comm function")
        var chatlist=ArrayList<String>()
        //chatlist.clear()

        val dbMentor = FirebaseDatabase.getInstance().getReference("Commchat")

        dbMentor.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userID2 = userSnapshot.key
                    Log.d("My tag", userID2.toString())

                    // Iterate through each booking for this user
                    if(user==userID2)
                        chatlist.add(userID2.toString())

                    for (chatSnapshot in userSnapshot.children) {
                        if(user.toString()==chatSnapshot.child("id").getValue(String::class.java)) {
                            Log.d("My tag", chatSnapshot.toString())

                            chatlist.add(userID2.toString())
                            //Toast.makeText(this@page12_Activity_chat2,tmsg, Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseError", "Error reading chats: ${error.message}")
            }
        })

        val databaseReference = FirebaseDatabase.getInstance().getReference("chat")

// Add a ValueEventListener to listen for changes in the Commchat node
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commchatMentor(chatlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                Log.e("Firebase", "Failed to read value.", databaseError.toException())
            }
        })

//        val handler = Handler(Looper.getMainLooper())
//        super.onResume()
//        handler.postDelayed({
//
//        }, 1500)


        Log.d("My tag", "out of function")

    }

    private fun commchatMentor(chatlist: ArrayList<String>) {
        val container = findViewById<LinearLayout>(R.id.insertRecent3)

        var r = ""
        val uniqueChatList = chatlist.distinctBy { it.toString() }
        container.removeAllViews()
        for (data in uniqueChatList) {

            val itemView = layoutInflater.inflate(R.layout.comm_chat, null)

            // Populate the item view with data
            val namebase1 = FirebaseDatabase.getInstance().getReference("mentor")
            namebase1.child(data).get().addOnSuccessListener { dataSnapshot ->
                val picM:String=dataSnapshot.child("uri").value.toString()
                if (!picM.isNullOrEmpty())
                {
                    defulturi=picM

                }
            }
            val namebase = FirebaseDatabase.getInstance().getReference("user")


            namebase.child(data).get().addOnSuccessListener { dataSnapshot ->
                val pic= itemView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
                val uri=dataSnapshot.child("imguri").value.toString()
                Log.d("MyTag:", "uri: "+uri.toString())

                if(uri.toString().length<15)
                    Glide.with(this)
                        .load(defulturi)
                        .into(pic)
                else
                    Glide.with(this)
                        .load(uri)
                        .into(pic)

            }



            val click=itemView.findViewById<LinearLayout>(R.id.comm_chat)
            click.setOnClickListener {
                val intent = Intent(this, page13_Activity_chat3::class.java)
                intent.putExtra("USER_ID", user)
                intent.putExtra("PROFILE", data)
                startActivity(intent)
            }

            // Add the inflated item view to the container
            container.addView(itemView)

        }
    }

    private fun chatMentor(chatlist: MutableList<Person>) {


        val container = findViewById<LinearLayout>(R.id.insertRecent2)

        var r = ""
        val uniqueChatList = chatlist.distinctBy { it.rid }
        container.removeAllViews()
        for (data in uniqueChatList) {

            val itemView = layoutInflater.inflate(R.layout.chatperson, null)

            // Populate the item view with data
            val namebase = FirebaseDatabase.getInstance().getReference("user")
            val nameTextView = itemView.findViewById<TextView>(R.id.name)

            val rid=data.rid

            namebase.child(data.rid.toString()).get().addOnSuccessListener { dataSnapshot ->
                val pic= itemView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
                val name=dataSnapshot.child("namee").value.toString()
                var uri=dataSnapshot.child("imguri").value.toString()
                Log.d("MyTag:", "name: "+name.toString())
                Log.d("MyTag:", "uri: "+uri.toString())
                if(name!="null")
                    nameTextView.text = name
                else
                    nameTextView.text = data.rid

//                val namebase1 = FirebaseDatabase.getInstance().getReference("mentor")
//                namebase1.child(data.rid).get().addOnSuccessListener { datanapshot ->
//
//                    val picM:String=datanapshot.child("uri").value.toString()
//                    if (picM[0]=='h')
//                    {
//                        defulturi=picM
//                        uri=picM
//                    }
//                }
                if(uri.toString().length<15)
                    Glide.with(this)
                        .load(defulturi)
                        .into(pic)
                else
                    Glide.with(this)
                        .load(uri)
                        .into(pic)

            }


            val availTextView = itemView.findViewById<TextView>(R.id.new_msg)
            availTextView.text = data.msgs

            val click=itemView.findViewById<LinearLayout>(R.id.chat_person)
            click.setOnClickListener {
                val intent = Intent(this, page12_Activity_chat2::class.java)
                intent.putExtra("USER_ID", user)
                intent.putExtra("PROFILE", rid)
                startActivity(intent)
            }

            // Add the inflated item view to the container
            container.addView(itemView)

        }

    }


}

data class Person(
    val rid:String,
    val name: String,
    val img: String,
    val msgs: String,
)
