package com.example.capstone

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LoginPage : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var df: DocumentReference
    private lateinit var fStore: FirebaseFirestore

    companion object {
        const val ADMIN_EMAIL = "admin@email.com"
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {

        fAuth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val loginbtn: Button = findViewById(R.id.LoginBtn)
        val signup: TextView = findViewById(R.id.signup)

        signup.setOnClickListener {
            val signuppage = Intent(this, SignUpPage::class.java)
            startActivity(signuppage)
        }

        loginbtn.setOnClickListener {
            // User Login function
            Login()
        }
    }

    private fun Login(){

        val Uname: EditText = findViewById(R.id.Username)
        val Pswrd: EditText = findViewById(R.id.Password)

        if (Uname.text.isEmpty() or Pswrd.text.isEmpty()){

            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        val login_email = Uname.text.toString()
        val login_pass = Pswrd.text.toString()

        fAuth.signInWithEmailAndPassword(login_email, login_pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = fAuth.currentUser
                    if (user?.email == ADMIN_EMAIL){
                        val intent = Intent(this, navigation::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show()
                    }else {
                        val intent = Intent(this, navigation::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Welcome User", Toast.LENGTH_SHORT).show()
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Authentication Failed ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}