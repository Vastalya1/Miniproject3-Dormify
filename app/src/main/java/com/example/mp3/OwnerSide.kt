package com.example.mp3

//import com.google.common.reflect.TypeToken
// ... other imports ...

//import com.example.mp3.AmenitiesDataStore.AmenitiesDataStore.saveAmenitiesData
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.DatePicker
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mp3.AmenitiesDataStore.getAmenitiesData
import com.example.mp3.AmenitiesDataStore.saveAmenitiesData
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale

private suspend fun getCoordinatesFromAddress(address: String, context: Context): GeoPoint? {
    Log.d("GeocodingDebug", "Starting geocoding for address: $address")

    return try {
        val geocoder = Geocoder(context, Locale.getDefault())

        // Different implementation based on Android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            var geoPoint: GeoPoint? = null

            geocoder.getFromLocationName(address, 1) { addresses ->
                Log.d("GeocodingDebug", "Addresses found: ${addresses.size}")
                if (addresses.isNotEmpty()) {
                    geoPoint = GeoPoint(addresses[0].latitude, addresses[0].longitude)
                    Log.d("GeocodingDebug", "Location found: lat=${addresses[0].latitude}, lng=${addresses[0].longitude}")
                }
            }

            // Wait a bit for the result
            kotlinx.coroutines.delay(1000)
            geoPoint

        } else {
            // For Android 12 and below
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocationName(address, 1)
            Log.d("GeocodingDebug", "Addresses found: ${addresses?.size}")

            if (!addresses.isNullOrEmpty()) {
                GeoPoint(addresses[0].latitude, addresses[0].longitude).also {
                    Log.d("GeocodingDebug", "Location found: lat=${it.latitude}, lng=${it.longitude}")
                }
            } else {
                Log.e("GeocodingDebug", "No addresses found")
                null
            }
        }
    } catch (e: Exception) {
        Log.e("GeocodingDebug", "Error getting coordinates: ${e.message}", e)
        null
    }
}

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

fun getAmenitiesData(): HashMap<String, List<String>> {
    return AmenitiesDataStore.getAmenitiesData()
}

fun saveAmenitiesData(data: HashMap<String, List<String>>) {
    AmenitiesDataStore.saveAmenitiesData(data)
}

data class Property(
    val id: String = "",
    val propertyType: String = "",
    val bhk: String = "",
    val buildUpArea: String = "",
    val furnishType: String = "",
    val monthlyRent: String = "",
    val availableFrom: String = "",
    val securityDeposit: String = "",
    val address: Map<String, String> = emptyMap(),
    val flatFurnishings: List<String> = emptyList(),
    val societyAmenities: List<String> = emptyList(),
    val ownerId: String = "",
    val ownerEmail: String = ""
)
data class PropertyFormState(
    val id: String = "",  // Document ID
    var propertyType: String = "Flat",
    var bhk: String = "1 RK",
    var buildUpArea: String = "",
    var furnishType: String = "Fully Furnished",
    var monthlyRent: String = "",
    var availableFrom: String = "",
    var securityDeposit: String = "",
    var customSecurityValue: String = "",
    var addressLine1: String = "",
    var addressLine2: String = "",
    var city: String = "",
    var state: String = "",
    var pinCode: String = "",
    var country: String = "",
    var ownerId: String = "",
    var ownerEmail: String = "",
    var amenities: Map<String, List<String>> = emptyMap()
)



@Composable
fun RentalAppNavHost(properties: List<Property>) {
    val navController = rememberNavController()

    val propertyFormState = remember { PropertyFormState() }

    //ak

    NavHost(navController = navController, startDestination = "home") {

        //ak
        composable("ListProperty") {
            ListProperty(navController, propertyFormState)
        }
        composable("Amenities") {
            Amenities(navController, propertyFormState)
        }
        composable("home") {
            RentalAppScreen(
                properties = properties,
                onAddPropertyClick = { navController.navigate("ListProperty") },

            )
        }

        composable("ListProperty") { ListProperty(navController = navController, propertyFormState = propertyFormState) }
        composable("Amenities") { Amenities(navController = navController, propertyFormState = propertyFormState) }
    }
}

@Composable
fun RentalAppScreen(
    properties: List<Property>,
    onAddPropertyClick: () -> Unit,
) {
    var ownerProperties by remember { mutableStateOf<List<PropertyFormState>>(emptyList()) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Fetch properties for current owner
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            ownerProperties = db.collection("properties")
                .document(currentUser.uid)
                .collection("ownerProperties")
                .get()
                .await() // Use await to get the result
                .documents
                .map { doc ->
                    PropertyFormState(
                        id = doc.id,
                        propertyType = doc.getString("propertyType") ?: "",
                        bhk = doc.getString("bhk") ?: "",
                        buildUpArea = doc.getString("buildUpArea") ?: "",
                        furnishType = doc.getString("furnishType") ?: "",
                        monthlyRent = doc.getString("monthlyRent") ?: "",
                        availableFrom = doc.getString("availableFrom") ?: "",
                        securityDeposit = doc.getString("securityDeposit") ?: "",
                        addressLine1 = (doc.get("address") as? Map<*, *>)?.get("addressLine1") as? String ?: "",
                        addressLine2 = (doc.get("address") as? Map<*, *>)?.get("addressLine2") as? String ?: "",
                        city = (doc.get("address") as? Map<*, *>)?.get("city") as? String ?: "",
                        state = (doc.get("address") as? Map<*, *>)?.get("state") as? String ?: "",
                        pinCode = (doc.get("address") as? Map<*, *>)?.get("pinCode") as? String ?: "",
                        country = (doc.get("address") as? Map<*, *>)?.get("country") as? String ?: "",
                        amenities = doc.get("amenities") as? Map<String, List<String>> ?: emptyMap()
                    )
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentUser == null) {
            Text("Please sign in to view your properties")
            return@Column
        }
        Button(
            onClick = onAddPropertyClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add Property")
        }

        // Display Owner Properties
        DisplayOwnerProperties()
    }
}

@Composable
fun DisplayOwnerProperties() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var properties by remember { mutableStateOf<List<PropertyFormState>>(emptyList()) }

    // Function to fetch properties from Firestore
    suspend fun fetchProperties() {
        if (currentUser != null) {
            properties = db.collection("properties")
                .document(currentUser.uid)
                .collection("ownerProperties")
                .get()
                .await() // Await the result
                .documents
                .map { doc ->
                    PropertyFormState(
                        id = doc.id,
                        propertyType = doc.getString("propertyType") ?: "",
                        bhk = doc.getString("bhk") ?: "",
                        buildUpArea = doc.getString("buildUpArea") ?: "",
                        furnishType = doc.getString("furnishType") ?: "",
                        monthlyRent = doc.getString("monthlyRent") ?: "",
                        availableFrom = doc.getString("availableFrom") ?: "",
                        securityDeposit = doc.getString("securityDeposit") ?: "",
                        addressLine1 = (doc.get("address") as? Map<*, *>)?.get("addressLine1") as? String ?: "",
                        addressLine2 = (doc.get("address") as? Map<*, *>)?.get("addressLine2") as? String ?: "",
                        city = (doc.get("address") as? Map<*, *>)?.get("city") as? String ?: "",
                        state = (doc.get("address") as? Map<*, *>)?.get("state") as? String ?: "",
                        pinCode = (doc.get("address") as? Map<*, *>)?.get("pinCode") as? String ?: "",
                        country = (doc.get("address") as? Map<*, *>)?.get("country") as? String ?: "",
                        ownerId = currentUser.uid,
                        ownerEmail = currentUser.email ?: "",
                        amenities = doc.get("amenities") as? Map<String, List<String>> ?: emptyMap() // Fetch amenities
                    )
                }
        }
    }

    // Fetch properties on initial composition
    LaunchedEffect(Unit) {
        fetchProperties()
    }

    // Perform deletion when propertyIdToDelete changes
    var propertyIdToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(propertyIdToDelete) {
        propertyIdToDelete?.let { propertyId ->
            if (currentUser != null) {
                try {
                    db.collection("properties")
                        .document(currentUser.uid)
                        .collection("ownerProperties")
                        .document(propertyId)
                        .delete()
                        .await() // Await the deletion
                    Log.d("Firebase", "Property deleted with ID: $propertyId")
                    // Fetch updated properties after deletion
                    fetchProperties()
                } catch (e: Exception) {
                    Log.e("Firebase", "Error deleting property", e)
                }
            }
            // Reset the propertyIdToDelete after deletion
            propertyIdToDelete = null
        }
    }

    Column {
        Text("Your Properties", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(properties) { property ->
                PropertyItem(property) { propertyId ->
                    // Directly delete the property without confirmation
                    propertyIdToDelete = propertyId
                }
            }
        }
    }
}

@Composable
fun PropertyItem(property: PropertyFormState, onDelete: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray)
            .padding(16.dp)
    ) {
        Text(text = "Property ID: ${property.id}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Type: ${property.propertyType}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "BHK: ${property.bhk}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Build Up Area: ${property.buildUpArea}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Furnish Type: ${property.furnishType}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Monthly Rent: ${property.monthlyRent}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Available From: ${property.availableFrom}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Security Deposit: ${property.securityDeposit}", style = MaterialTheme.typography.bodyLarge)

        // Combine address components into a single address string
        val fullAddress = buildString {
            append(property.addressLine1)
            if (property.addressLine2.isNotEmpty()) {
                append(", ${property.addressLine2}")
            }
            append(", ${property.city}, ${property.state}, ${property.pinCode}, ${property.country}")
        }

        Text(text = "Address: $fullAddress", style = MaterialTheme.typography.bodyLarge)

        // Display amenities
        Text(text = "Amenities:", style = MaterialTheme.typography.bodyLarge)
        property.amenities.forEach { (key, value) ->
            Text(text = "$key: ${value.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
        }

        // Delete Button
        Button(
            onClick = { onDelete(property.id) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Delete Property")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}





data class Features(
    val propertyType: String,
    val bhk: String,
    val buildUpArea: String,
    val furnishType: String,
    val monthlyRent: String,
    val availableFrom: String,
    val securityDeposit: String,
    val address: String
)


@Composable
fun ListProperty(navController: NavController, propertyFormState: PropertyFormState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val focusManager = LocalFocusManager.current

    var addressLine1 by remember { mutableStateOf(propertyFormState.addressLine1) }
    var addressLine2 by remember { mutableStateOf(propertyFormState.addressLine2) }
    var city by remember { mutableStateOf(propertyFormState.city) }
    var state by remember { mutableStateOf(propertyFormState.state) }
    var pinCode by remember { mutableStateOf(propertyFormState.pinCode) }
    var country by remember { mutableStateOf(propertyFormState.country) }

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

        val propertyType = remember { mutableStateOf(propertyFormState.propertyType) }
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

        val bhk = remember { mutableStateOf(propertyFormState.bhk) }
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
        var buildUpArea by remember { mutableStateOf(propertyFormState.buildUpArea) }

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

        val furnishType = remember { mutableStateOf(propertyFormState.furnishType) }
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
        var monthlyRent by remember { mutableStateOf(propertyFormState.monthlyRent) }
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
        var securityDeposit by remember { mutableStateOf(propertyFormState.securityDeposit) }
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
        // State variables for address inputs
        var addressLine1 by remember { mutableStateOf("") }
        var addressLine2 by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var state by remember { mutableStateOf("") }
        var pinCode by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("") }

        // Focus manager to control focus
        val focusManager = LocalFocusManager.current

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Address",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        // Address input fields
        OutlinedTextField(
            value = addressLine1,
            onValueChange = { addressLine1 = it },
            label = { Text("Address Line 1") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Set the action to "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
                }
            )
        )

        OutlinedTextField(
            value = addressLine2,
            onValueChange = { addressLine2 = it },
            label = { Text("Address Line 2") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Set the action to "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
                }
            )
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Set the action to "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
                }
            )
        )

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Set the action to "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
                }
            )
        )

        OutlinedTextField(
            value = pinCode,
            onValueChange = { pinCode = it },
            label = { Text("Pin Code") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next // Set the action to "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
                }
            )
        )

        // Text field for country input
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done // Set the action to "Done" for the last field
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // Dismiss the keyboard when done
                }
            )
        )
        LaunchedEffect(propertyType.value) {
            propertyFormState.propertyType = propertyType.value
        }
        LaunchedEffect(bhk.value) {
            propertyFormState.bhk = bhk.value
        }
        LaunchedEffect(buildUpArea) {
            propertyFormState.buildUpArea = buildUpArea
        }
        LaunchedEffect(furnishType.value) {
            propertyFormState.furnishType = furnishType.value
        }
        LaunchedEffect(monthlyRent) {
            propertyFormState.monthlyRent = monthlyRent
        }
        LaunchedEffect(selectedDate) {
            propertyFormState.availableFrom = selectedDate
        }
        LaunchedEffect(securityDeposit) {
            propertyFormState.securityDeposit = securityDeposit
        }
//        LaunchedEffect(customValue) {
//            propertyFormState.customSecurityValue = customValue
//        }
        val addr = addressLine1 + addressLine2 + city + state + pinCode + country

//        Button(onClick = {
//            Log.d("PropertyDebug", """
//        Address Line 1: ${propertyFormState.addressLine1}
//        Address Line 2: ${propertyFormState.addressLine2}
//        City: ${propertyFormState.city}
//        State: ${propertyFormState.state}
//        Pin Code: ${propertyFormState.pinCode}
//        Country: ${propertyFormState.country}
//    """.trimIndent())
//            if (currentUser != null) {
//                scope.launch {
//                    val db = FirebaseFirestore.getInstance()
//                    val amenitiesData = getAmenitiesData()
//
//                    // Create full address for geocoding
//                    val fullAddress = buildString {
//                        append(propertyFormState.addressLine1)
//                        if (propertyFormState.addressLine2.isNotEmpty()) {
//                            append(", ${propertyFormState.addressLine2}")
//                        }
//                        append(", $city")
//                        append(", $state")
//                        append(", $pinCode")
//                        append(", $country")
//                    }
//
//                    Log.d("Address", "Full address: $fullAddress")
//
//                    // Get coordinates
//                    val geoPoint = getCoordinatesFromAddress(fullAddress, context)
//                    Log.d("Geocoding", "GeoPoint result: $geoPoint")
//
//                    val propertyData = hashMapOf(
//                        "propertyType" to propertyFormState.propertyType,
//                        "bhk" to propertyFormState.bhk,
//                        "buildUpArea" to propertyFormState.buildUpArea,
//                        "furnishType" to propertyFormState.furnishType,
//                        "monthlyRent" to propertyFormState.monthlyRent,
//                        "availableFrom" to propertyFormState.availableFrom,
//                        "securityDeposit" to propertyFormState.securityDeposit,
//                        "address" to mapOf(
//                            "addressLine1" to propertyFormState.addressLine1,
//                            "addressLine2" to propertyFormState.addressLine2,
//                            "city" to propertyFormState.city,
//                            "state" to propertyFormState.state,
//                            "pinCode" to propertyFormState.pinCode,
//                            "country" to propertyFormState.country,
//                            "location" to geoPoint  // Add GeoPoint here
//                        ),
//                        "amenities" to amenitiesData,
//                        "timestamp" to com.google.firebase.Timestamp.now()
//                    )
//
//                    // Save to Firestore
//                    db.collection("properties")
//                        .document(currentUser.uid)
//                        .collection("ownerProperties")
//                        .add(propertyData)
//                        .addOnSuccessListener { documentReference ->
//                            Log.d("Firebase", "Property added with ID: ${documentReference.id}")
//                            navController.navigate("home")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.w("Firebase", "Error adding property", e)
//                        }
//                }
//            }
//        }) {
//            Text("Add Property")
//        }
        Button(
            onClick = {
                if (currentUser != null) {
                    scope.launch {
                        val fullAddress = buildString {
                            append(addressLine1)
                            if (addressLine2.isNotEmpty()) {
                                append(", $addressLine2")
                            }
                            append(", $city")
                            append(", $state")
                            append(", $pinCode")
                            append(", $country")
                        }

                        val geoPoint = getCoordinatesFromAddress(fullAddress, context)

                        
                        val propertyData = hashMapOf(
                            "propertyType" to propertyFormState.propertyType,
                            "bhk" to propertyFormState.bhk,
                            "buildUpArea" to propertyFormState.buildUpArea,
                            "furnishType" to propertyFormState.furnishType,
                            "monthlyRent" to propertyFormState.monthlyRent,
                            "availableFrom" to propertyFormState.availableFrom,
                            "securityDeposit" to propertyFormState.securityDeposit,
                            "address" to mapOf(
                                "addressLine1" to addressLine1,
                                "addressLine2" to addressLine2,
                                "city" to city,
                                "state" to state,
                                "pinCode" to pinCode,
                                "country" to country,
                                "location" to geoPoint  // Only store the formatted string
                            ),
                            "amenities" to getAmenitiesData(),
                            "timestamp" to com.google.firebase.Timestamp.now(),
                            "ownerId" to currentUser.uid,
                            "ownerEmail" to (currentUser.email ?: "")
                        )

                        // Save to Firestore
                        FirebaseFirestore.getInstance()
                            .collection("properties")
                            .document(currentUser.uid)
                            .collection("ownerProperties")
                            .add(propertyData)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Firebase", "Property added with ID: ${documentReference.id}")
                                navController.navigate("home")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Error adding property", e)
                            }
                    }
                }
            }
        ) {
            Text("Save Property")
        }

    }

}

@Composable
fun Amenities(navController: NavController, propertyFormState: PropertyFormState) {
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
            onClick = {
                // Filter selected amenities
                val selectedFlatFurnishings = selectedFlatFurnishingOptions
                    .filter { it.value }
                    .keys
                    .toList()

                val selectedSocietyAmenities = societyAmenitiesSelectedOptions
                    .filter { it.value }
                    .keys
                    .toList()

                // Store the selections in SharedPreferences or similar storage
                val amenitiesData = hashMapOf(
                    "flatFurnishings" to selectedFlatFurnishings,
                    "societyAmenities" to selectedSocietyAmenities
                )

                // Store in temporary storage (you'll need to implement this)
                saveAmenitiesData(amenitiesData)
                navController.navigate("listProperty") },
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
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this property?") },
        confirmButton = {
            Button(onClick = {
                onConfirm() // Call the confirm function
                onDismiss() // Dismiss the dialog
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

//@Composable
//fun Address(navController: NavController, bhk: String) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState()),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // State variables for address inputs
//        var addressLine1 by remember { mutableStateOf("") }
//        var addressLine2 by remember { mutableStateOf("") }
//        var city by remember { mutableStateOf("") }
//        var state by remember { mutableStateOf("") }
//        var pinCode by remember { mutableStateOf("") }
//        var country by remember { mutableStateOf("") }
//
//        // Focus manager to control focus
//        val focusManager = LocalFocusManager.current
//
//        Text(text = "Address",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//        )
//        // Address input fields
//        OutlinedTextField(
//            value = addressLine1,
//            onValueChange = { addressLine1 = it },
//            label = { Text("Address Line 1") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Next // Set the action to "Next"
//            ),
//            keyboardActions = KeyboardActions(
//                onNext = {
//                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
//                }
//            )
//        )
//
//        OutlinedTextField(
//            value = addressLine2,
//            onValueChange = { addressLine2 = it },
//            label = { Text("Address Line 2") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Next // Set the action to "Next"
//            ),
//            keyboardActions = KeyboardActions(
//                onNext = {
//                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
//                }
//            )
//        )
//
//        OutlinedTextField(
//            value = city,
//            onValueChange = { city = it },
//            label = { Text("City") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Next // Set the action to "Next"
//            ),
//            keyboardActions = KeyboardActions(
//                onNext = {
//                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
//                }
//            )
//        )
//
//        OutlinedTextField(
//            value = state,
//            onValueChange = { state = it },
//            label = { Text("State") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Next // Set the action to "Next"
//            ),
//            keyboardActions = KeyboardActions(
//                onNext = {
//                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
//                }
//            )
//        )
//
//        OutlinedTextField(
//            value = pinCode,
//            onValueChange = { pinCode = it },
//            label = { Text("Pin Code") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Next // Set the action to "Next"
//            ),
//            keyboardActions = KeyboardActions(
//                onNext = {
//                    focusManager.moveFocus(FocusDirection.Down) // Move focus to the next field
//                }
//            )
//        )
//
//        // Text field for country input
//        OutlinedTextField(
//            value = country,
//            onValueChange = { country = it },
//            label = { Text("Country") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Done // Set the action to "Done" for the last field
//            ),
//            keyboardActions = KeyboardActions(
//                onDone = {
//                    focusManager.clearFocus() // Dismiss the keyboard when done
//                }
//            )
//        )
//
//        val addr = addressLine1 + addressLine2 + city + state + pinCode + country
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun Visuals() {
//    MP3Theme {
//        val navController=rememberNavController()
//
//        //Address(navController)
//        //ListProperty(navController)
//    }
//
//}

