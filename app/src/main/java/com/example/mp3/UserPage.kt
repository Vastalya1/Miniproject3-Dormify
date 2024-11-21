package com.example.mp3

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.model.AutocompletePrediction

@Composable
fun UserPage(viewModel: UserViewModel) {
    val locationQuery by viewModel.locationQuery.collectAsState()
    val autocompleteSuggestions by viewModel.autocompleteSuggestions.collectAsState()
    val budgetRange by viewModel.budgetRange.collectAsState()
    val propertyType by viewModel.propertyType.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = locationQuery,
            onValueChange = {
                viewModel.onLocationQueryChanged(it)
                showSuggestions = it.isNotEmpty()
            },
            label = { Text("Search Location") },
            modifier = Modifier.fillMaxWidth()
        )

        if (showSuggestions) {
            DropdownMenu(
                expanded = showSuggestions,
                onDismissRequest = { showSuggestions = false }
            ) {
                autocompleteSuggestions.forEach { suggestion ->
                    DropdownMenuItem(onClick = {
                        viewModel.onLocationSelected(suggestion.getFullText(null).toString())
                        showSuggestions = false
                    }) {
                        Text(text = suggestion.getFullText(null).toString())
                    }
                }
            }
        }

        // Budget Range Filter
        Text(text = "Budget Range (₹${budgetRange.start.toInt()} - ₹${budgetRange.endInclusive.toInt()})")
        Slider(
            value = budgetRange.start,
            onValueChange = { viewModel.onBudgetRangeChanged(it, budgetRange.endInclusive) },
            valueRange = 0f..100000f
        )
        Slider(
            value = budgetRange.endInclusive,
            onValueChange = { viewModel.onBudgetRangeChanged(budgetRange.start, it) },
            valueRange = 0f..100000f
        )

        // Property Type Filter
        TextButton(onClick = { expanded = !expanded }) {
            Text(text = "Property Type: $propertyType")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("Rental", "PG", "Hostel").forEach { type ->
                DropdownMenuItem(onClick = {
                    viewModel.onPropertyTypeChanged(type)
                    expanded = false
                }) {
                    Text(text = type)
                }
            }
        }
    }
}

