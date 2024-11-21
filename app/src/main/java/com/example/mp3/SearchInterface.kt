package com.example.mp3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchInterface(viewModel: UserViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Location Search
        OutlinedTextField(
            value = viewModel.locationQuery.collectAsState().value,
            onValueChange = { viewModel.onLocationQueryChanged(it) },
            label = { Text("Search Location") },
            modifier = Modifier.fillMaxWidth()
        )

        // Show suggestions
        val suggestions by viewModel.autocompleteSuggestions.collectAsState()
        if (suggestions.isNotEmpty()) {
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

        // Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleFilters() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Filters")
            val isExpanded by viewModel.isFilterExpanded.collectAsState()
            Icon(
                imageVector = if (isExpanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle Filters"
            )
        }

        val isExpanded by viewModel.isFilterExpanded.collectAsState()
        AnimatedVisibility(visible = isExpanded) {
            Column {
                // Budget Slider
                val budget by viewModel.budget.collectAsState()
                Text("Budget: ₹${budget.toInt()}")
                Slider(
                    value = budget,
                    onValueChange = { viewModel.onBudgetChanged(it) },
                    valueRange = 0f..50000f,
                    steps = 50
                )

                // Property Types
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val isPGSelected by viewModel.isPGSelected.collectAsState()
                    val isRentalSelected by viewModel.isRentalSelected.collectAsState()
                    val isHostelSelected by viewModel.isHostelSelected.collectAsState()

                    CheckboxWithLabel("PG", isPGSelected) {
                        viewModel.onPGSelectionChanged(it)
                    }
                    CheckboxWithLabel("Rental", isRentalSelected) {
                        viewModel.onRentalSelectionChanged(it)
                    }
                    CheckboxWithLabel("Hostel", isHostelSelected) {
                        viewModel.onHostelSelectionChanged(it)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(label)
    }
}