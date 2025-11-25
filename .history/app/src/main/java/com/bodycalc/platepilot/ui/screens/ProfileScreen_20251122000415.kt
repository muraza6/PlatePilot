package com.bodycalc.platepilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bodycalc.platepilot.ui.theme.PastelBlue
import com.bodycalc.platepilot.ui.theme.PastelGreen
import com.bodycalc.platepilot.ui.theme.PastelPink
import com.bodycalc.platepilot.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val name by viewModel.name.collectAsState()
    val calorieGoal by viewModel.calorieGoal.collectAsState()
    val proteinGoal by viewModel.proteinGoal.collectAsState()
    val fitnessGoal by viewModel.fitnessGoal.collectAsState()
    val dietaryPreference by viewModel.dietaryPreference.collectAsState()
    
    var nameInput by remember { mutableStateOf(name) }
    var calorieInput by remember { mutableStateOf(calorieGoal.toString()) }
    var proteinInput by remember { mutableStateOf(proteinGoal.toString()) }
    var selectedGoal by remember { mutableStateOf(fitnessGoal) }
    var selectedPreference by remember { mutableStateOf(dietaryPreference) }
    var showSavedMessage by remember { mutableStateOf(false) }
    
    LaunchedEffect(name) { nameInput = name }
    LaunchedEffect(calorieGoal) { calorieInput = calorieGoal.toString() }
    LaunchedEffect(proteinGoal) { proteinInput = proteinGoal.toString() }
    LaunchedEffect(fitnessGoal) { selectedGoal = fitnessGoal }
    LaunchedEffect(dietaryPreference) { selectedPreference = dietaryPreference }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    style = MaterialTheme.typography.displayLarge
                )
            }
            
            // Name input
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { 
                            nameInput = it
                            viewModel.updateName(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your name") },
                        singleLine = true
                    )
                }
            }
            
            // Calorie goal
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PastelPink
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Calorie Goal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calorieInput,
                        onValueChange = { 
                            calorieInput = it
                            it.toIntOrNull()?.let { calories ->
                                viewModel.updateCalorieGoal(calories)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("2000") },
                        suffix = { Text("calories") },
                        singleLine = true
                    )
                }
            }
            
            // Protein goal
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PastelGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Protein Goal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = proteinInput,
                        onValueChange = { 
                            proteinInput = it
                            it.toIntOrNull()?.let { protein ->
                                viewModel.updateProteinGoal(protein)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("150") },
                        suffix = { Text("grams") },
                        singleLine = true
                    )
                }
            }
            
            // Fitness goal
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PastelBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fitness Goal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val goals = listOf("Weight Loss", "Maintenance", "Muscle Gain")
                    goals.forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedGoal == goal,
                                onClick = {
                                    selectedGoal = goal
                                    viewModel.updateFitnessGoal(goal)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = goal)
                        }
                    }
                }
            }
            
            // Dietary preferences
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dietary Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val preferences = listOf("Vegetarian", "Vegan", "Keto", "Paleo", "Gluten-Free")
                    preferences.forEach { pref ->
                        var checked by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { checked = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = pref)
                        }
                    }
                }
            }
            
            // Save button
            Button(
                onClick = { 
                    viewModel.saveProfile()
                    showSavedMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelGreen
                )
            ) {
                Text(
                    text = "Save Profile",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1A1A1A)
                )
            }
            
            // Success message
            if (showSavedMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showSavedMessage = false
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PastelGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "✓ Profile saved successfully!",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
