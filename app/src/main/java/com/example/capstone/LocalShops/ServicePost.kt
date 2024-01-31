package com.example.capstone.LocalShops

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.MapsActivity
import com.example.capstone.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ServicePost : AppCompatActivity() {
    private lateinit var df: DocumentReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var shop: EditText
    private lateinit var shopD: EditText
    private lateinit var shopL: EditText
    private lateinit var contactNum: EditText
    private lateinit var contactE: EditText
    private lateinit var spinner: Spinner
    private val SELECT_LOCATION_REQUEST_CODE = 789
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    companion object {
        const val USER_INPUTS_REQUEST_CODE = 456
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_post)

        fStore = FirebaseFirestore.getInstance()

        val pinLocationText = findViewById<TextView>(R.id.pinLocationText)
        val post = findViewById<Button>(R.id.post)

        // Check if the activity is restored from a previous state
        if (savedInstanceState == null) {
            // Initialize the input fields only for a new instance
            shop = findViewById<EditText>(R.id.shopName)
            shopD = findViewById<EditText>(R.id.shopDescription)
            shopL = findViewById<EditText>(R.id.shopLocation)
            contactNum = findViewById<EditText>(R.id.shopContactNumber)
            contactE = findViewById<EditText>(R.id.shopContactEmail)
            spinner = findViewById<Spinner>(R.id.type)

            val adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Puroks,
                android.R.layout.simple_spinner_item
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Restore the state of the input fields
        restoreInstanceState(savedInstanceState)

        post.setOnClickListener {
            val shopName = shop.text.toString()
            val shopDescription = shopD.text.toString()
            val location = shopL.text.toString()
            val contactNumber = contactNum.text.toString()
            val contactEmail = contactE.text.toString()
            val purok = spinner.selectedItem.toString()

            if (shop.text.isEmpty() or shopD.text.isEmpty() or shopL.text.isEmpty()) {
                emptyField(shop)
                emptyField(shopD)
                emptyField(shopL)
            } else {

                // Check if latitude and longitude are valid
                if (latitude != 0.0 && longitude != 0.0) {
                    // Add location data to the 'shops' map
                    val shops = hashMapOf(
                        "ShopName" to shopName,
                        "ShopDescription" to shopDescription,
                        "ShopLocation" to location,
                        "contactNumber" to contactNumber,
                        "contactEmail" to contactEmail,
                        "ShopPurok" to purok,
                        "TypeOfBusiness" to "Services",
                        "latitude" to latitude,
                        "longitude" to longitude
                    )

                    // Save the 'shops' map to Firestore
                    df = fStore.collection("Shops").document()
                    df.set(shops)

                    Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Handle the case where latitude or longitude is not valid
                    Toast.makeText(this, "Invalid location data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        pinLocationText.setOnClickListener {
            // Navigate to MapsActivity
            val intent = Intent(this, MapsActivity::class.java)

            // Instead of starting a new instance, use flags to bring the existing instance to the front
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

            startActivityForResult(intent, USER_INPUTS_REQUEST_CODE)
        }
    }

    private fun emptyField(textField: EditText) {
        if (textField.text.isEmpty()) {
            textField.error = "Empty Field"
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        // Restore the state of the input fields from savedInstanceState
        if (savedInstanceState != null) {
            shop.setText(savedInstanceState.getString("shopName"))
            shopD.setText(savedInstanceState.getString("shopDescription"))
            shopL.setText(savedInstanceState.getString("shopLocation"))
            contactNum.setText(savedInstanceState.getString("contactNumber"))
            contactE.setText(savedInstanceState.getString("contactEmail"))

            // Set the selected item in the spinner
            val purok = savedInstanceState.getString("purok")
            val position = (spinner.adapter as ArrayAdapter<String>).getPosition(purok)
            spinner.setSelection(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_INPUTS_REQUEST_CODE && resultCode == SELECT_LOCATION_REQUEST_CODE) {
            // Handle the result and retrieve any data, including user inputs
            val resultData = data?.extras
            if (resultData != null) {
                // Get the selected location data
                latitude = resultData.getDouble("latitude", 0.0)
                longitude = resultData.getDouble("longitude", 0.0)

                // Update your UI or save the location data as needed
                // For example, you can display the selected location in a TextView
            }
        }
    }
}