package com.example.mp3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mp3.ui.theme.MP3Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        val mapIntent = Intent(this, MapsActivity::class.java)
        val composableKey = intent.getStringExtra("composable_key")

        // Debugging log
        Log.d("MainActivity", "composableKey: $composableKey")

        when (composableKey) {
            "OwnerHomeScreen" -> setContent {
                MP3Theme {
                    Scaffold(modifier = Modifier.fillMaxSize()) {
                        // Fetch properties for the current user
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val db = FirebaseFirestore.getInstance()

                        // State to hold properties
                        val properties = remember { mutableStateOf<List<Property>>(emptyList()) }

                        // Fetch properties for the current owner
                        if (currentUser != null) {
                            db.collection("properties")
                                .document(currentUser.uid)
                                .collection("ownerProperties")
                                .addSnapshotListener { snapshot, e ->
                                    if (e != null) {
                                        Log.w("Firebase", "Listen failed.", e)
                                        return@addSnapshotListener
                                    }
                                    val propertyList = mutableListOf<Property>()
                                    if (snapshot != null) {
                                        for (doc in snapshot.documents) {
                                            val address = doc.get("address") as? Map<*, *>
                                            
                                            // Fetch amenities directly from the property document
                                            val amenities = doc.get("amenities") as? Map<*, *>
                                            val flatFurnishings = amenities?.get("flatFurnishings") as? List<String> ?: emptyList()
                                            val societyAmenities = amenities?.get("societyAmenities") as? List<String> ?: emptyList()

                                            // Create property object with document ID as property ID
                                            val property = Property(
                                                id = doc.id, // Document ID as property ID
                                                propertyType = doc.getString("propertyType") ?: "",
                                                bhk = doc.getString("bhk") ?: "",
                                                buildUpArea = doc.getString("buildUpArea") ?: "",
                                                furnishType = doc.getString("furnishType") ?: "",
                                                monthlyRent = doc.getString("monthlyRent") ?: "",
                                                availableFrom = doc.getString("availableFrom") ?: "",
                                                securityDeposit = doc.getString("securityDeposit") ?: "",
                                                address = address as? Map<String, String> ?: emptyMap(),
                                                flatFurnishings = flatFurnishings,
                                                societyAmenities = societyAmenities,
                                                ownerId = currentUser.uid,
                                                ownerEmail = currentUser.email ?: ""
                                            )
                                            propertyList.add(property)
                                        }
                                    }
                                    // Update the properties state after fetching all amenities
                                    properties.value = propertyList
                                }
                        }

                        // Function to delete a property
                        fun deleteProperty(propertyId: String) {
                            if (currentUser != null) {
                                db.collection("properties")
                                    .document(currentUser.uid)
                                    .collection("ownerProperties")
                                    .document(propertyId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Property successfully deleted!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firebase", "Error deleting property", e)
                                    }
                            }
                        }

                        // Call the navigation setup function
                        RentalAppNavHost(properties = properties.value
                            //, onDelete = { propertyId -> deleteProperty(propertyId) }
                        ) // Pass the delete function to the NavHost
                    }
                }
            }

            "UserHomeScreen" -> startActivity(mapIntent)

            else -> setContent {
                MP3Theme {
                    val navController = rememberNavController()
                    IndexPage(navController = navController)
                }
            }
        }
    }

    @Composable
    fun IndexPage(
        navController: NavController,
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    // Create an Intent to start SignUpActivity
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(48.dp)
            ) {
                Text(text = "Owner", fontSize = 25.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val intent = Intent(context, UserSignUpActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(48.dp)
            ) {
                Text(text = "User", fontSize = 25.sp)
            }
        }
    }
}
