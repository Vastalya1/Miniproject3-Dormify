package com.example.mp3

//import IndexPage
//import androidx.compose.ui.text.input.KeyboardOptions
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mp3.ui.theme.MP3Theme
import com.google.firebase.FirebaseApp

//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

//data class Property(
//    val name: String,
//    val interestedPeople: List<String>,
//    val contactInfo: List<String> // Add contact info for interested people
//)



class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        val mapintent = Intent(this, MapsActivity::class.java)
        val composableKey = intent.getStringExtra("composable_key")
        
        // Debugging log
        Log.d("MainActivity", "composableKey: $composableKey")


        when (composableKey) {
            "OwnerHomeScreen" -> setContent {
                MP3Theme {
                    Scaffold(modifier = Modifier.fillMaxSize()) {

                        val properties = remember {
                            listOf(
                                Property("Luxury Apartment", listOf("John Doe", "Jane Smith"), listOf("123-456-7890", "987-654-3210")),
                                Property("Cozy PG", listOf("Alice Johnson"), listOf("111-222-3333")),
                                Property("Beachside Hostel", listOf("Bob Brown", "Charlie White"), listOf("555-555-5555", "444-444-4444"))
                            )
                        }

                        // Call the navigation setup function
                        RentalAppNavHost(properties = properties)
                    }
                }
            }


            "UserHomeScreen" -> startActivity(mapintent)

            else -> setContent {
                MP3Theme {
                    val navController = rememberNavController()
                    IndexPage(navController = navController)
                }
            }
        }

//        setContent {
//            MP3Theme {
//               // val navController = rememberNavController()
//
//                Scaffold(modifier = Modifier.fillMaxSize()) {
//
//
//                    val properties = remember {
//                        listOf(
//                            Property("Luxury Apartment", listOf("John Doe", "Jane Smith"), listOf("123-456-7890", "987-654-3210")),
//                            Property("Cozy PG", listOf("Alice Johnson"), listOf("111-222-3333")),
//                            Property("Beachside Hostel", listOf("Bob Brown", "Charlie White"), listOf("555-555-5555", "444-444-4444"))
//                        )
//                    }
//
////
//                    val navController = rememberNavController()
////
//                    NavHost(navController = navController, startDestination = "index") {
//
//                        composable("owner") { LoginScreen(navController) }
//                        composable("index") { IndexPage(navController) }
//                        composable("amenities"){ Amenities(navController)}
//                        composable("ListProperty") { ListProperty(navController) }
//                        //composable("OwnerHomeScreen") { OwnerHomeScreen(navController, properties)}
//                        composable(
//                            route = "interested_people/{property}",
//                            arguments = listOf(navArgument("property") {
//                                type = NavType.ParcelableType(Property::class.java)
//                            })
//                        ) { backStackEntry ->
//                            val property = backStackEntry.arguments?.getParcelable<Property>("property")
//                            if (property != null) {
//                                InterestedPeopleScreen(property, navController)
//                            }
//                        }
//                    }
//
//                    //ListProperty(navController = navController)
//                    //Amenities(navController = navController)
//
//                    OwnerHomeScreen(navController = navController, properties = properties)
//
//                    //AppNavHost()
//                }
//            }
//        }
//    }

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


    @Composable
    fun LoginScreen(navController: NavController) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login Page",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(3, 169, 244, 255)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("listProperty") },
                modifier = Modifier.width(200.dp),
            ) {
                Text(
                    text = "Login",
                    fontSize = 25.sp
                )
            }

            //Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "back",
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp)
            )
        }
    }


//@Composable
//fun AppNavHost(navController: NavHostController){
//    //val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "OwnerHomeScreen") {
//        composable("index") { IndexPage(navController) }
//        composable("login") { LoginScreen(navController) }
//        composable("listProperty") { ListProperty(navController) }
//        composable("amenities") { Amenities(navController) }
//        composable(
//            route = "interested_people/{property}",
//            arguments = listOf(navArgument("property") {
//                type = NavType.ParcelableType(Property::class.java)
//            })
//        ) { backStackEntry ->
////            val property = backStackEntry.arguments?.getParcelable<Property>("property")
////            if (property!= null) {
////                InterestedPeopleScreen(property, navController)
////            }
//        }
//    }
//}

    //preview
    @Preview(showBackground = true)
    @Composable
    fun Visuals() {
        MP3Theme {
            val navController = rememberNavController() // Preview navigation setup
            //IndexPage(navController = navController)
            // LoginScreen(navController = navController)
            //ListProperty(navController= navController)
            //Amenities(navController= navController)


//        val signInLauncher = remember {
//            object : ActivityResultLauncher<Intent>() {
//                override fun launch(input: Intent, options: ActivityOptionsCompat?) {
//                    // No-op for preview
//                }
//                override fun unregister() {
//                    // No-op for preview
//                }
//            }
//        }
//
//        val signInIntent = Intent() // Mocking an empty intent for preview
//
//        IndexPage(
//            navController = navController,
//            signInLauncher = signInLauncher,
//            signInIntent = signInIntent
//        )
//
//        val properties = listOf(
//            Property("Luxury Apartment", listOf("John Doe", "Jane Smith"), listOf("123-456-7890", "987-654-3210")),
//            Property("Cozy PG", listOf("Alice Johnson"), listOf("111-222-3333")),
//            Property("Beachside Hostel", listOf("Bob Brown", "Charlie White"), listOf("555-555-5555", "444-444-4444"))
//            //comment
//        )
//
//        OwnerHomeScreen()

            //IndexPage()

        }
    }
}
