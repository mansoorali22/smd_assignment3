package com.mansoorali.i212502

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class page2_Activity_login : AppCompatActivity() {

    private lateinit var fireAuth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var dbRef: DatabaseReference
    private lateinit var database:DatabaseReference
    private lateinit var user: UserModel
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page2_login)

        val forget: TextView = findViewById(R.id.forget)
        val login: Button = findViewById(R.id.login)
        pass = findViewById(R.id.pass)
        email = findViewById(R.id.email)
        fireAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        email.setText("mansoor@gmail.com")
        pass.setText("12345678")


        forget.setOnClickListener {
            Toast.makeText(this, "link clicked", Toast.LENGTH_SHORT).show()
        }

        val signUp = findViewById<TextView>(R.id.create)
        signUp.text = "Sign Up"
        signUp.paintFlags = Paint.UNDERLINE_TEXT_FLAG or signUp.paintFlags

        login.setOnClickListener {
            var check = true
            val password = pass.text.toString()
            val mail = email.text.toString()
            if (mail.isEmpty()) {
                email.error = "Please enter email"
                check = false
            }
            if (password.isEmpty()) {
                pass.error = "Please enter password"
                check = false
            }

            if (check) {
                // Query the database to find user by email
                var nam=""

                //Toast.makeText(this, "sad", Toast.LENGTH_SHORT).show()
                fireAuth.signInWithEmailAndPassword(mail,password)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            check=true
                            val userID = mail.substringBefore('@').replace('.', ' ').replace('_', '2')
                            database=FirebaseDatabase.getInstance().getReference("user")
                            database.child(userID).get().addOnSuccessListener {
                                nam = it.child("count").value.toString()

                            }
                            sharedPreferences = getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("Login", userID).apply()
                            if (nam=="1")
                            {
                                database.child(userID).child("count").setValue("2")
                                val intent = Intent(this, page17_Activity_profileView::class.java)
                                intent.putExtra("USER_ID", userID) // Put the userID as an extra in the Intent
                                startActivity(intent)
                            }
                            else
                            {
                                val intent = Intent(this, page4_Activity_home::class.java)
                                intent.putExtra("USER_ID", userID) // Put the userID as an extra in the Intent
                                startActivity(intent)
                            }

                        }else
                        {
                            check=false
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("error",it.exception.toString())
                        }
                    }
//                val intent = Intent(this, page4_Activity_home::class.java)
//                intent.putExtra("user", user)
//                startActivity(intent)
//                finish() // Finish the login activity
            }
        }

        signUp.setOnClickListener {
            val intent = Intent(this, page3_Activity_create::class.java)

            startActivity(intent)
        }

        val forget1 = findViewById<TextView>(R.id.forget)
        forget1.setOnClickListener {
            val intent = Intent(this, page19_Activity_forgetEmail::class.java)
            startActivity(intent)
        }
    }
}

