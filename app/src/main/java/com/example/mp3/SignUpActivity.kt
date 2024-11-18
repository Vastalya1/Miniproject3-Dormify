package com.example.mp3

//import androidx.media3.common.util.Log
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mp3.databinding.ActivitySignUpBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Successfully signed up
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is authenticated, now collect additional user info
                collectUserInfo(user)
            }
        } else {
            Toast.makeText(this, "Sign-up failed", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)



        // Set up the sign-up button and trigger FirebaseUI sign-in
        binding.btnSignUp.setOnClickListener {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }
    }



    private fun collectUserInfo(user: FirebaseUser) {
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val role = "user" // Default role, can be changed based on your logic

        if (name.isNotBlank() && phone.isNotBlank()) {
            saveUserToFirestore(user, name, phone, role)
        } else {
            Toast.makeText(this, "Please enter your name and phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToFirestore(user: FirebaseUser, name: String, phone: String, role: String) {
        val db = FirebaseFirestore.getInstance()

        val userMap = hashMapOf(
            "name" to name,
            "email" to user.email,
            "phone" to phone,
            "role" to role
        )

        // Correct Firestore structure: users -> user document (UID)
        val usersCollection = db.collection("users")

        // Save the data with UID as the document ID
        usersCollection.document(user.uid).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User successfully registered", Toast.LENGTH_SHORT).show()
                // Redirect to another activity or home page
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("composable_key", "OwnerHomeScreen")
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
