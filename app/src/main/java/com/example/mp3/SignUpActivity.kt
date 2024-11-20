package com.example.mp3

//import androidx.media3.common.util.Log
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mp3.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var isSignUpMode = true // Flag to toggle between sign-up and sign-in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        // Set up toggle button for sign-up and sign-in
        binding.btnToggle.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUI()
        }

        // Set up the sign-up/sign-in button
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isSignUpMode) {
                    signUpUser(email, password)
                } else {
                    signInUser(email, password)
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize UI
        updateUI()
    }

    private fun updateUI() {
        if (isSignUpMode) {
            binding.tvTitle.text = "Sign Up"
            binding.btnSignUp.text = "Sign Up"
            binding.etName.visibility = View.VISIBLE // Show name field for sign-up
            binding.etPhone.visibility = View.VISIBLE // Show phone field for sign-up
            binding.btnToggle.text = "Switch to Sign In" // Change text for toggle button
        } else {
            binding.tvTitle.text = "Sign In"
            binding.btnSignUp.text = "Sign In"
            binding.etName.visibility = View.GONE // Hide name field for sign-in
            binding.etPhone.visibility = View.GONE // Hide phone field for sign-in
            binding.btnToggle.text = "Switch to Sign Up" // Change text for toggle button
        }
    }

    private fun signUpUser(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up successful, get the user
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        // Collect additional user info
                        collectUserInfo(user)
                    }
                } else {
                    // Sign-up failed
                    Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        // Check user role from Firestore
                        FirebaseFirestore.getInstance().collection("users").document(user.uid).get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")
                                if (role == "Owner") {
                                    // Redirect to owner home screen
                                    val intent = Intent(this, MainActivity::class.java).apply {
                                        putExtra("composable_key", "OwnerHomeScreen")
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Access denied: You are not an owner", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    // Sign-in failed
                    Toast.makeText(this, "Sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun collectUserInfo(user: FirebaseUser) {
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val role = "Owner" // Default role, can be changed based on your logic

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

        // Save the data with UID as the document ID
        db.collection("users").document(user.uid).set(userMap)
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
