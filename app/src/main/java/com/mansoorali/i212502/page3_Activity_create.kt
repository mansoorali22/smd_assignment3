package com.mansoorali.i212502

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class page3_Activity_create : AppCompatActivity() {

    var city1 = arrayOf("Peshawar", "Rawalpindi", "Karachi", "Islamabad")
    var city2 = arrayOf("New Delhi", "Mumbai", "Chennai", "Kolkata")
    var country = arrayOf("India", "Pakistan")
    var autoCompletecoutry: AutoCompleteTextView? = null
    var autoCompletecity: AutoCompleteTextView? = null
    var adaptercity: ArrayAdapter<String>? = null
    var adaptercountry: ArrayAdapter<String>? = null
    private lateinit var fireAuth: FirebaseAuth
    private  lateinit var ename: EditText
    private  lateinit var eemail: EditText
    private  lateinit var enum: EditText
    private  lateinit var epass: EditText
    private  lateinit var signup: Button
    private  lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page3_create)

        ename= findViewById(R.id.name)
        eemail= findViewById(R.id.email)
        enum= findViewById(R.id.num)
        epass= findViewById(R.id.pass)

        dbRef= FirebaseDatabase.getInstance().getReference("user")
        signup= findViewById(R.id.create)
        fireAuth = FirebaseAuth.getInstance()

        val login : TextView= findViewById(R.id.loginC)
        autoCompletecoutry = findViewById(R.id.coutry)
        autoCompletecity = findViewById(R.id.cityy)

        adaptercountry = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, country)
        autoCompletecoutry?.setAdapter(adaptercountry)

        autoCompletecoutry?.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val s_country = parent.getItemAtPosition(position).toString()
            //Toast.makeText(applicationContext, "Item: $s_country", Toast.LENGTH_SHORT).show()
            if(s_country=="Pakistan")
            {
                adaptercity = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, city1)
                autoCompletecity?.setAdapter(adaptercity)

            }
            else{
                adaptercity = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, city2)
                autoCompletecity?.setAdapter(adaptercity)
            }


        })
        login.setOnClickListener {
            //Toast.makeText(applicationContext, "hello", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, page2_Activity_login::class.java)
            startActivity(intent)
            finish()
        }

        signup.setOnClickListener {
            //Toast.makeText(this, "fhs", Toast.LENGTH_LONG).show()
            saveUserDetails()
        }
    }

    private fun saveUserDetails() {
        val name = ename.text.toString()
        val mail = eemail.text.toString()
        val num = "+92" + enum.text.toString()
        val pass = epass.text.toString()
        val city = autoCompletecity?.text.toString()
        val countr = autoCompletecoutry?.text.toString()
        var check = true
        if (name.isEmpty()) {
            ename.error = "Please enter name"
            check = false
        }
        if (mail.isEmpty()) {
            eemail.error = "Please enter email"
            check = false
        }
        if (num.isEmpty()) {
            enum.error = "Please enter number"
            check = false
        }
        if (pass.isEmpty()) {
            epass.error = "Please enter password"
            check = false
        }
        if (city.isEmpty()) {
            autoCompletecity?.error = "Please select city"
            check = false
        }
        if (countr.isEmpty()) {
            autoCompletecoutry?.error = "Please select country"
            check = false
        }


        if(check==true)
        {
            fireAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        check=true
                    }else
                    {
                        check=false
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }

        if (check == true) {
            val userID = mail.substringBefore('@').replace('.', ' ').replace('_', '2')
            val user = UserModel(userID, name,mail, num, pass, city,"1", countr, "","","","")

            dbRef.child(userID).setValue(user)
                .addOnCompleteListener {
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                    ename.text.clear()
                    eemail.text.clear()
                    epass.text.clear()
                    enum.text.clear()
                    autoCompletecoutry?.text?.clear()
                    autoCompletecity?.text?.clear()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
