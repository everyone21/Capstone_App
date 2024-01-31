package com.example.capstone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.List.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var df: DocumentReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var database: DatabaseReference
    private var valid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        auth = Firebase.auth
        fStore = FirebaseFirestore.getInstance()

        val create: Button = findViewById(R.id.submit)

        // User SignUp function
        create.setOnClickListener {
            userSignUp()
        }
    }

    private fun userSignUp() {
        database =  Firebase.database.reference
        val store = fStore.collection("users")

        val fname: EditText = findViewById(R.id.firstName)
        val lname: EditText = findViewById(R.id.lastName)
        val email: EditText = findViewById(R.id.email)
        val phone_num: EditText = findViewById(R.id.cellnum)
        val pass: EditText = findViewById(R.id.password)
        val confirmPass: EditText = findViewById(R.id.confirm_pass)

        val Email = email.text.toString()
        val Password = pass.text.toString()
        val Confirm = confirmPass.text.toString()
        val firstName = fname.text.toString()
        val lastName = lname.text.toString()
        val pn = phone_num.text.toString()

        if (email.text.isEmpty() or pass.text.isEmpty() or fname.text.isEmpty() or phone_num.text.isEmpty() or lname.text.isEmpty()){
            Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_SHORT).show()
            checkFields(fname)
            checkFields(lname)
            checkFields(email)
            checkFields(phone_num)
            checkFields(pass)
            checkFields(confirmPass)
            return
        } else if(Password == Confirm) {
            auth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, To navigation

                        val user = hashMapOf(
                            "uid" to auth.currentUser?.uid,
                            "email" to Email,
                            "code" to "1"
                        )

                        database.child("users").child(auth.currentUser?.uid!!).setValue(User(firstName, lastName, Email, pn, auth.currentUser?.uid))

                        store.add(user)

                        val main = Intent(this, navigation::class.java)
                        startActivity(main)
                        Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "An Error Occured ${it.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
        } else {
            Toast.makeText(this, "Password and Confirm-Password Does not match", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkFields(text: EditText){
        valid = if(text.text.isEmpty()){
            text.setError("Empty Field")
            Toast.makeText(this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }
}