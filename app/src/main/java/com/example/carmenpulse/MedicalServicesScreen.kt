package com.example.carmenpulse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MedicalServiceItem(
    val id: String,
    val title: String,
    val schedule: String,
    val time: String,
    val venue: String,
    val status: String,
    val requirements: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalServicesScreen() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Upcoming", "Ongoing")

    //based on the previous interview from brgy. carmen staff
    val carmenMedicalServicesList = remember {
        listOf(
            MedicalServiceItem(
                id = "1",
                title = "General Consultation & Prenatal Care",
                schedule = "Every Monday",
                time = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                status = "Upcoming",
                requirements = "Member Data Record (MDR) / Valid ID",
                description = "Includes midwife services, general consultations, and routine prenatal checkups. Patients go through initial triage with BHWs outside first."
            ),
            MedicalServiceItem(
                id = "2",
                title = "Prenatal Checkup & Monitoring",
                schedule = "Every Tuesday",
                time = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                status = "Upcoming",
                requirements = "Member Data Record (MDR)",
                description = "Dedicated prenatal care and monitoring for expectant mothers, scheduled according to trimester guidelines."
            ),
            MedicalServiceItem(
                id = "3",
                title = "Infant Immunization Program",
                schedule = "Every Wednesday",
                time = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                status = "Upcoming",
                requirements = "Baby's Immunization Card",
                description = "Routine vaccination and distribution of free vitamins for babies as needed during consultation."
            ),
            MedicalServiceItem(
                id = "4",
                title = "Family Planning & NTP / PICT Screening",
                schedule = "Every Thursday",
                time = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                status = "Upcoming",
                requirements = "Valid ID or Prescription",
                description = "Includes catch-up prenatal visits, family planning consultations, and National Tuberculosis Program (NTP) or HIV screening (PICT)."
            ),
            MedicalServiceItem(
                id = "5",
                title = "Senior Citizen NCD Medicine Distribution",
                schedule = "Every 4th Thursday of the Month",
                time = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                status = "Upcoming",
                requirements = "Prescription & Senior ID",
                description = "Monthly distribution of maintenance medicine for non-communicable disease (NCD) senior citizen patients."
            ),
            MedicalServiceItem(
                id = "6",
                title = "Non-Senior NCD Medicine Distribution",
                schedule = "Every 5th Thursday of the Month",
                time = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                status = "Upcoming",
                requirements = "Prescription & Medical Record",
                description = "Monthly allocation of maintenance medicines for registered non-senior NCD patients."
            ),
            MedicalServiceItem(
                id = "7",
                title = "MCP Birthing Facility (Normal Deliveries)",
                schedule = "Daily",
                time = "24/7 Operations",
                venue = "MCP Birthing Facility (Right side of Health Center)",
                status = "Ongoing",
                requirements = "Prenatal records / PhilHealth details",
                description = "Round-the-clock maternity care package (MCP) services catering specifically to normal deliveries for residents and assigned beneficiaries."
            )
        )
    }

    val filteredServices = carmenMedicalServicesList.filter {
        when (selectedFilter) {
            "Upcoming" -> it.status.equals("Upcoming", ignoreCase = true)
            "Ongoing" -> it.status.equals("Ongoing", ignoreCase = true)
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CP",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CarmenPulse",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "Barangay Carmen, CDO", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Service Welcome Banner Card (Matching Home Style)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Barangay Health Center",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Access professional medical consultations and scheduled programs for all residents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                filters.forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = { 
                            Text(
                                text = filter,
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredServices) { service ->
                    ServiceCard(service = service)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun ServiceCard(service: MedicalServiceItem) {
    val isOngoing = service.status.equals("Ongoing", ignoreCase = true)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isOngoing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = if (isOngoing) "🟢 ONGOING" else "📅 UPCOMING",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOngoing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(12.dp))

            ServiceDetailItem(icon = "📅", label = "Schedule", value = service.schedule)
            ServiceDetailItem(icon = "⏰", label = "Time", value = service.time)
            ServiceDetailItem(icon = "📍", label = "Venue", value = service.venue)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "📋", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Requirements",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = service.requirements,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceDetailItem(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
