package com.example.mp3

//import IndexPage
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mp3.ui.theme.MP3Theme
import com.google.firebase.FirebaseApp
import java.util.Calendar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

//ak
fun savePropertyData(
    propertyType: String,
    bhk: String,
    buildUpArea: String,
    furnishType: String,
    amenities: List<String>,
    location: Location,
    monthlyRent: String,
    availableFrom: String,
    securityDeposit: String,
    customValue: String? // Only used if security deposit is custom
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return  // Ensure the user is logged in

    // Create the property data model
    val propertyData = DataModel(
        propertyType = propertyType,
        bhk = bhk,
        buildUpArea = buildUpArea,
        furnishType = furnishType,
        amenities = amenities,
        location = location,
        monthlyRent = monthlyRent,
        availableFrom = availableFrom,
        securityDeposit = securityDeposit,
        customSecurityDepositAmount = customValue.takeIf { securityDeposit == "Custom" }
    )

    // Save the data in Firestore under the user's properties
    db.collection("owners")  // Collection name: "owners"
        .document(userId)  // Use the logged-in user's UID as the document ID
        .collection("properties")  // Sub-collection name: "properties"
        .add(propertyData)  // Add the property data to Firestore
        .addOnSuccessListener {
            println("Property data added successfully!")
        }
        .addOnFailureListener { e ->
            println("Error adding property: $e")
        }
}


data class Property(
    val name: String,
    val interestedPeople: List<String>,
    val contactInfo: List<String> // Add contact info for interested people
)
class MainActivity : ComponentActivity() {


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)


        setContent {
            MP3Theme {
                //val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) {

//                    val composableKey = intent.getStringExtra("composable_key")
//                    when (composableKey) {
//                        "OwnerHomeScreen" -> OwnerHomeScreen()
//                        else -> IndexPage()
//                    }

                    //ListProperty(navController = navController)
                    //Amenities(navController = navController)

                    //OwnerHomeScreen()

                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}


@Composable
fun IndexPage(
    //navController: NavController,
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
            onClick = { /* TODO: Implement navigation to the user-specific page */ },
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
        Text(text = "Login Page",
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

@Composable
fun ListProperty(navController: NavController){
    //ak
//    val focusManager = LocalFocusManager.current
    val focusManager = LocalFocusManager.current
    var propertyType by remember { mutableStateOf("Flat") }
    var bhk by remember { mutableStateOf("1 BHK") }
    var buildUpArea by remember { mutableStateOf("") }
    var furnishType by remember { mutableStateOf("Fully Furnished") }
    var amenities by remember { mutableStateOf(listOf<String>()) }
    var locationAddress by remember { mutableStateOf("") }
    var locationCity by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var availableFrom by remember { mutableStateOf("") }
    var securityDeposit by remember { mutableStateOf("") }
    var customValue by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "List of Properties",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(3, 169, 244, 255)
        )

        Spacer(modifier = Modifier.height(16.dp))


        //property type
        Text(text = "Property Type",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0, 0, 0, 255)
        )

        val propertyType = remember { mutableStateOf("Flat") }
        val propertyTypeOptions = listOf("Flat", "PG", "Hostel")


        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            propertyTypeOptions.forEach { option ->
                Box(
                    modifier = Modifier
                        .clickable { propertyType.value = option }
                        .border(
                            width = 2.dp,
                            color = if (propertyType.value == option) Color.Blue else Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = if (propertyType.value == option) Color.Blue.copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = option,
                        color = if (propertyType.value == option) Color.Blue else Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }


        //BHK
        Text(text = "BHK",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0, 0, 0, 255)
        )

        val bhk = remember { mutableStateOf("1 RK") }
        val bhkOptions = listOf("1 RK", "1 BHK", "2 BHK", "3 BHK","3+ BHK")


        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bhkOptions.forEach { option ->
                Box(
                    modifier = Modifier
                        .clickable { bhk.value = option }
                        .border(
                            width = 2.dp,
                            color = if (bhk.value == option) Color.Blue else Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = if (bhk.value == option) Color.Blue.copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = option,
                        color = if (bhk.value == option) Color.Blue else Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //build up area
//        Text(
//            text = "Build Up Area",
//            fontSize = 20.sp,
//
//        )
        var buildUpArea by remember { mutableStateOf("") }

        OutlinedTextField(
            value = buildUpArea,
            onValueChange = { buildUpArea = it },
            label = { Text("Build Up Area") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next // Set the Enter key to display "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus() // Dismiss the keyboard and exit the field
                }
            ),
            trailingIcon = {
                Text("sq. ft", color = Color.Gray)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))


        //furnish type
        Text(text = "Furnish Type",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0, 0, 0, 255)
        )

        val furnishType = remember { mutableStateOf("Fully Furnished") }
        val furnishOptions = listOf("Fully Furnished", "Semi Furnished", "Unfurnished")

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            furnishOptions.forEach { option ->
                Box(
                    modifier = Modifier
                        .clickable { furnishType.value = option }
                        .border(
                            width = 2.dp,
                            color = if (furnishType.value == option) Color.Blue else Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = if (furnishType.value == option) Color.Blue.copy(alpha = 0.2f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = option,
                        color = if (furnishType.value == option) Color.Blue else Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        //Spacer(modifier = Modifier.height(8.dp))
        //add amenities
        Text(
            text = "+ Add Amenities",
            fontSize = 20.sp,
            textDecoration = TextDecoration.Underline,
            color = Color(103, 58, 183, 255),
            modifier = Modifier
                .clickable { navController.navigate("amenities") }
                .padding(8.dp)
        )

        //monthly rent
        var monthlyRent by remember { mutableStateOf("") }
        OutlinedTextField(
            value = monthlyRent,
            onValueChange = { monthlyRent = it },
            label = { Text("Monthly Rent") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next // Set the Enter key to display "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus() // Dismiss the keyboard and exit the field
                }
            ),
            leadingIcon ={
                Text("Rs.", color = Color.Gray)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


        //available from -> calender
        var selectedDate by remember { mutableStateOf("") }
        val context = LocalContext.current

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text("Available From") },
            readOnly = true, // Make TextField read-only
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Open Date Picker dialog
                    val calendar = Calendar.getInstance()

                    // Set calendar to currently selected date, if available
                    if (selectedDate.isNotEmpty()) {
                        val parts = selectedDate.split("/")
                        val day = parts[0].toInt()
                        val month = parts[1].toInt() - 1 // Calendar months are 0-based
                        val year = parts[2].toInt()
                        calendar.set(year, month, day)
                    }

                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    DatePickerDialog(
                        context,
                        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        },
                        year,
                        month,
                        day
                    ).show()
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Security Deposit",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )


        //security deposit
        var securityDeposit by remember { mutableStateOf("") }
        var customValue by remember { mutableStateOf("") }
        val options = listOf("None", "1 month", "2 months", "Custom")
        Column {
            // Arrange options in pairs
            options.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowOptions.forEach { option ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clickable {
                                    securityDeposit = option
                                    if (option != "Custom") customValue = ""
                                }
                                .border(
                                    width = 2.dp,
                                    color = if (securityDeposit == option) Color.Blue else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = if (securityDeposit == option) Color.Blue.copy(alpha = 0.2f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                color = if (securityDeposit == option) Color.Blue else Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Show the custom input field if "Other" is selected


            if (securityDeposit == "Custom") {
                OutlinedTextField(
                    value = customValue,
                    onValueChange = { customValue = it },
                    label = { Text("Enter Security Deposit Amount") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next // Set the Enter key to display "Next"
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.clearFocus() // Dismiss the keyboard and exit the field
                        }
                    ),
                    leadingIcon = {
                        Text("Rs.", color = Color.Gray)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(103, 58, 183, 255))
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text("Post Property")
        }
    }





}

@Composable
fun Amenities(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Make the entire page scrollable
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Flat furnishings
        Text(
            text = "Flat Furnishings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(75, 81, 83, 255)
        )

        val flatFurnishingOptions = listOf(
            "Dining Table", "Washing Machine", "Sofa", "Stove", "Microwave",
            "Fridge", "Water Purifier", "Gas Pipeline", "Bed", "TV", "Study Table", "Cupboard", "Geyser"
        )
        val selectedFlatFurnishingOptions = remember {
            mutableStateMapOf<String, Boolean>().apply {
                flatFurnishingOptions.forEach { put(it, false) }
            }
        }


        // Display flat furnishing options in rows of 3
        flatFurnishingOptions.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // Distribute evenly across the row
                            .height(80.dp) // Ensure consistent height
                            .width(100.dp) // Ensure consistent width
                            .clickable {
                                selectedFlatFurnishingOptions[option] =
                                    !(selectedFlatFurnishingOptions[option] ?: false)
                            }
                            .border(
                                width = 2.dp,
                                color = if (selectedFlatFurnishingOptions[option] == true) Color.Blue else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = if (selectedFlatFurnishingOptions[option] == true) Color.Blue.copy(
                                    alpha = 0.2f
                                ) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = if (selectedFlatFurnishingOptions[option] == true) Color.Blue else Color.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Society amenities
        Text(
            text = "Society Amenities",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(75, 81, 83, 255)
        )

        val societyAmenitiesOptions = listOf(
            "Lift", "CCTV", "GYM", "Garden", "Swimming Pool",
            "Gated Community", "Regular Water Supply", "Power Backup", "Pet Allowed"
        )
        val societyAmenitiesSelectedOptions = remember {
            mutableStateMapOf<String, Boolean>().apply {
                societyAmenitiesOptions.forEach { put(it, false) }
            }
        }

        // Display society amenities options in rows of 3
        societyAmenitiesOptions.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // Distribute evenly across the row
                            .height(100.dp)
                            .clickable {
                                societyAmenitiesSelectedOptions[option] =
                                    !(societyAmenitiesSelectedOptions[option] ?: false)
                            }
                            .border(
                                width = 2.dp,
                                color = if (societyAmenitiesSelectedOptions[option] == true) Color.Blue else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = if (societyAmenitiesSelectedOptions[option] == true) Color.Blue.copy(
                                    alpha = 0.2f
                                ) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = if (societyAmenitiesSelectedOptions[option] == true) Color.Blue else Color.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("listProperty") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
            ) {
            Text(
                text = "Save",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }


}



@Composable
fun AppNavHost(
    navController: NavController = rememberNavController(),

) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "first") {
//        composable("first") { IndexPage(
//            navController = navController,
//        )
//        }
        composable("owner") { LoginScreen(navController) }
        composable("amenities"){ Amenities(navController)}
        composable("listProperty"){ ListProperty(navController= navController)}
    }
}

@Composable
fun OwnerHomeScreen() {
    val navController = rememberNavController()
    val properties = remember {
        listOf(
            Property("Luxury Apartment", listOf("John Doe", "Jane Smith"), listOf("123-456-7890", "987-654-3210")),
            Property("Cozy PG", listOf("Alice Johnson"), listOf("111-222-3333")),
            Property("Beachside Hostel", listOf("Bob Brown", "Charlie White"), listOf("555-555-5555", "444-444-4444"))
        )
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("listProperty") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Add Property")
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(properties) { property ->
                        PropertyItem(property = property, onInterestedClick = { selectedProperty ->
                            navController.navigate("interested/${selectedProperty.name}")
                        })
                    }
                }
            }
        }
        composable("interested/{propertyName}") { backStackEntry ->
            val propertyName = backStackEntry.arguments?.getString("propertyName") ?: ""
            val property = properties.find { it.name == propertyName }
            if (property != null) {
                InterestedPeopleScreen(property = property, navController = navController)
            }
        }
    }
}

@Composable
fun PropertyItem(
    property: Property,
    onInterestedClick: (Property) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray)
            .padding(16.dp)
    ) {
        Text(
            text = property.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        val interestedCount = property.interestedPeople.size
        Text(
            text = "$interestedCount People Interested",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable {
                onInterestedClick(property)
            }
        )
    }
}

@Composable
fun InterestedPeopleScreen(property: Property, navController: NavHostController) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Interested People for ${property.name}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Show each interested person with their contact info
            property.interestedPeople.forEachIndexed { index, person ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = person,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = property.contactInfo[index],
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigateUp() }) {
                Text("Go Back")
            }
        }
    }
}

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

        val properties = listOf(
            Property("Luxury Apartment", listOf("John Doe", "Jane Smith"), listOf("123-456-7890", "987-654-3210")),
            Property("Cozy PG", listOf("Alice Johnson"), listOf("111-222-3333")),
            Property("Beachside Hostel", listOf("Bob Brown", "Charlie White"), listOf("555-555-5555", "444-444-4444"))
        )

        OwnerHomeScreen()

        //IndexPage()

    }
}

