package com.example.mp3

import android.app.DatePickerDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.Calendar

data class Property(
    val name: String,
    val interestedPeople: List<String>,
    val contactInfo: List<String> // Add contact info for interested people
)



@Composable
fun RentalAppNavHost(properties: List<Property>) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            RentalAppScreen(
                properties = properties,
                onAddPropertyClick = { navController.navigate("ListProperty") },
                onInterestedClick = { property ->
                    navController.navigate("interested/${property.name}")
                }
            )
        }
        composable("interested/{propertyName}") { backStackEntry ->
            val propertyName = backStackEntry.arguments?.getString("propertyName") ?: ""
            val property = properties.find { it.name == propertyName }
            if (property != null) {
                InterestedPeopleScreen(property = property, navController = navController)
            }
        }
        composable("ListProperty") { ListProperty(navController) }
    }
}

@Composable
fun RentalAppScreen(
    properties: List<Property>,
    onAddPropertyClick: () -> Unit,
    onInterestedClick: (Property) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onAddPropertyClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Add Property")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(properties) { property ->
                PropertyItem(property = property, onInterestedClick = onInterestedClick)
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

@Composable
fun ListProperty(navController: NavController){
    val focusManager = LocalFocusManager.current
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
