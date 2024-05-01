package com.mansoorali.i212502

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class page16_Activity2_profile : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var user:String
    private lateinit var fireAuth: FirebaseAuth

    var city1 = arrayOf("Peshawar", "Rawalpindi", "Karachi", "Islamabad")
    var city2 = arrayOf("New York", "Washington", "Alaska", "Ohio")
    var country = arrayOf("USA", "Pakistan")
    var autoCompletecoutry: AutoCompleteTextView? = null
    var autoCompletecity: AutoCompleteTextView? = null
    var adaptercity: ArrayAdapter<String>? = null
    var adaptercountry: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page16_activity2_profile)

        database= FirebaseDatabase.getInstance().getReference("user")
        var userID = intent.getStringExtra("USER_ID")
        user=userID.toString()

        if (userID != null) {
            fetchUser(userID)

        }

        autoCompletecoutry = findViewById(R.id.coutry)
        autoCompletecity = findViewById(R.id.cityy)
        

        adaptercountry = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, country)
        autoCompletecoutry?.setAdapter(adaptercountry)

        autoCompletecoutry?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val s_country = parent.getItemAtPosition(position).toString()
            autoCompletecoutry?.setText("")
            //Toast.makeText(applicationContext, "Item: $s_country", Toast.LENGTH_SHORT).show()
            if (s_country == "Pakistan") {
                adaptercity =
                    ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, city1)
                autoCompletecity?.setAdapter(adaptercity)

            } else {
                adaptercity =
                    ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, city2)
                autoCompletecity?.setAdapter(adaptercity)
            }
        })

        var mail:String=""
        var pass:String=""
        var img:String=""
        var cover:String=""
        if (userID != null) {

            database.child(userID).get().addOnSuccessListener {
                mail= it.child("mail").value.toString()
                pass =it.child("pass").value.toString()
                img =it.child("imguri").value.toString()
                cover=it.child("coveruri").value.toString()
            }
        }
        val back1 = findViewById<ImageView>(R.id.back)
        back1.setOnClickListener {
            val intent = Intent(this, page17_Activity_profileView::class.java)
            intent.putExtra("USER_ID", userID)
            startActivity(intent)
            finish()
        }
        val book =findViewById<Button>(R.id.create)
        book.setOnClickListener {
            userID=updateProfile(mail, pass,img,cover)
            Toast.makeText(applicationContext, "Profile is updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(mail:String, pass:String, img:String,cover:String):String {
        Log.d("MyTag", "entered function")
        database=FirebaseDatabase.getInstance().getReference("user")
        val name=findViewById<EditText>(R.id.name)
        val email=findViewById<EditText>(R.id.email)
        val number=findViewById<EditText>(R.id.number)
        autoCompletecoutry = findViewById(R.id.coutry)
        autoCompletecity = findViewById(R.id.cityy)
        var maiil=email.text.toString()
        val userID = maiil.substringBefore('@').replace('.', ' ').replace('_', '2')

        //deleting from database
        val userNodeReference = database.child(user)

// Delete the user from the database
        val database1 = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = database1.getReference("user")

        reference.child(user.toString()).removeValue()
            .addOnSuccessListener {
                // Handle success

            }
            .addOnFailureListener { exception ->
                // Handle failure

            }

        val newuser = UserModel(userID, name.text.toString(),email.text.toString(), number.text.toString(),
            pass, autoCompletecity?.text.toString(),"2", autoCompletecoutry?.text.toString(),
            img,cover,"","")


        database.child(userID).setValue(newuser)


//deleting from firebase
        val firebaseAuth = FirebaseAuth.getInstance()
        Log.d("mail", mail.toString())

        val deluser = FirebaseAuth.getInstance().currentUser


        val credential = EmailAuthProvider
            .getCredential(mail, pass)

        deluser!!.reauthenticate(credential)
            .addOnCompleteListener {
                deluser!!.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("firebase", "User account deleted.")
                        }
                    }
            }
        //



        firebaseAuth.createUserWithEmailAndPassword(email.text.toString(), pass)
            .addOnSuccessListener { authResult ->
                // User creation successful
                val user: FirebaseUser? = authResult.user
                // Handle user creation success, if needed
            }
            .addOnFailureListener { exception ->
                // User creation failed
                // Handle user creation failure, if needed
            }

        //user=userID
        return userID

    }



    private fun fetchUser(userId: String) {
        Log.d("MyTag", "entered function")
        database=FirebaseDatabase.getInstance().getReference("user")
        database.child(userId).get().addOnSuccessListener {
            val nam=it.child("namee").value.toString()
            val mail=it.child("mail").value.toString()
            val image=it.child("imguri").value.toString()
            val num=it.child("num").value.toString()
            val country=it.child("countr").value.toString()
            val city=it.child("city").value.toString()


            val name=findViewById<EditText>(R.id.name)
            val email=findViewById<EditText>(R.id.email)
            val number=findViewById<EditText>(R.id.number)
            autoCompletecoutry = findViewById(R.id.coutry)
            autoCompletecity = findViewById(R.id.cityy)

            val img=findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.profilepic)
            Glide.with(this)
                .load(image)
                .into(img)
            name.setText(nam)
            email.setText(mail)
            number.setText(num)
            autoCompletecity?.setText(city)
            autoCompletecoutry?.setText(country)

        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        Log.d("MyTag", "out of function")
    }


}