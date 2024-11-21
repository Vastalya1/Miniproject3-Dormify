package com.example.mp3

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import com.google.android.libraries.places.api.model.AutocompletePrediction

@Composable
fun UserPage(viewModel: UserViewModel, context: Context) {
    val locationQuery by viewModel.locationQuery.collectAsState()
    val suggestions by viewModel.autocompleteSuggestions.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isFilterExpanded by viewModel.isFilterExpanded.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val isPGSelected by viewModel.isPGSelected.collectAsState()
    val isRentalSelected by viewModel.isRentalSelected.collectAsState()
    val isHostelSelected by viewModel.isHostelSelected.collectAsState()
    val selectedLocationLatLng by viewModel.selectedLocationLatLng.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Location Search
        OutlinedTextField(
            value = locationQuery,
            onValueChange = { viewModel.onLocationQueryChanged(it) },
            label = { Text("Search Location") },
            modifier = Modifier.fillMaxWidth()
        )

        // Display suggestions
        if (suggestions.isNotEmpty() && selectedLocation == null) {
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                items(suggestions) { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onLocationSelected(suggestion)
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Filters Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleFilters() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filters")
            Icon(
                imageVector = if (isFilterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle Filters"
            )
        }

        AnimatedVisibility(visible = isFilterExpanded) {
            Column {
                // Budget Slider
                Text(
                    text = "Budget: ₹${budget.toInt()}",
                    style = MaterialTheme.typography.body1
                )
                Slider(
                    value = budget,
                    onValueChange = { viewModel.onBudgetChanged(it) },
                    valueRange = 0f..50000f,
                    steps = 50
                )

                // Property Type Checkboxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Checkbox(
                            checked = isPGSelected,
                            onCheckedChange = { viewModel.onPGSelectionChanged(it) }
                        )
                        Text("PG")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Checkbox(
                            checked = isRentalSelected,
                            onCheckedChange = { viewModel.onRentalSelectionChanged(it) }
                        )
                        Text("Rental")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Checkbox(
                            checked = isHostelSelected,
                            onCheckedChange = { viewModel.onHostelSelectionChanged(it) }
                        )
                        Text("Hostel")
                    }
                }
            }
        }

        // Show Map when location is selected
        selectedLocationLatLng?.let { latLng ->
            val intent = Intent(context, MapsActivity::class.java).apply {
                putExtra("latitude", latLng.latitude)
                putExtra("longitude", latLng.longitude)
                putExtra("budget", budget.toInt())
                putExtra("isPG", isPGSelected)
                putExtra("isRental", isRentalSelected)
                putExtra("isHostel", isHostelSelected)
            }
            context.startActivity(intent)
        }
    }
}