package com.example.carmenpulse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
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
    val category: String,
    val icon: String,
    val roomNumber: String,
    val operatingHours: String,
    val scheduleDays: String,
    val venue: String,
    val requirements: List<String>,
    val description: String,
    val procedureSteps: List<String>,
    val contactInfo: String = "Barangay Carmen Health Center Desk: (088) 858-1234",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalServicesScreen() {
    var selectedService by remember { mutableStateOf<MedicalServiceItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf(
        "All",
        "Primary Care",
        "Dental Care",
        "Pediatrics",
        "Maternal Health",
        "Maintenance Medicine",
        "Disease Screening",
    )

    val carmenMedicalServicesList = remember {
        listOf(
            MedicalServiceItem(
                id = "1",
                title = "Dental Care & Oral Hygiene",
                category = "Dental Care",
                icon = "🦷",
                roomNumber = "Room 103 - Dental Clinic",
                scheduleDays = "Mon, Wed, Fri",
                operatingHours = "8:00 AM - 4:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Valid Government or Barangay ID",
                    "Barangay Carmen Resident Certificate / Pass",
                    "Member Data Record (MDR) or PhilHealth ID (if available)"
                ),
                description = "Offers permanent dental services including tooth extraction, routine dental checkups, cleaning, oral hygiene consultation, and preventive fluoride treatments for children.",
                procedureSteps = listOf(
                    "Proceed to BHW Triage booth outside the main entrance for queue number.",
                    "Present valid ID and resident clearance at registration window.",
                    "Proceed to Room 103 (Dental Clinic) and await dental assistant call."
                )
            ),
            MedicalServiceItem(
                id = "2",
                title = "General Consultation & Checkup",
                category = "Primary Care",
                icon = "🩺",
                roomNumber = "Room 101 - Main Physician Clinic",
                scheduleDays = "Every Monday to Friday",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Member Data Record (MDR) or Valid Government ID",
                    "Previous Medical Records or Maintenance Prescription (if applicable)",
                    "Barangay Carmen Resident ID or Clearance"
                ),
                description = "Comprehensive health consultations with municipal physicians, routine physical examinations, diagnosis, prescription issuance, and specialist referrals.",
                procedureSteps = listOf(
                    "Complete triage check at main health center entrance with BHW.",
                    "Present MDR card or ID at Room 101 registration counter.",
                    "Get vital signs checked (blood pressure, weight, temperature) by health worker.",
                    "Consult physician in Room 101 for diagnosis and prescription."
                )
            ),
            MedicalServiceItem(
                id = "3",
                title = "Infant & Child Immunization",
                category = "Pediatrics",
                icon = "💉",
                roomNumber = "Room 104 - Pediatric & Vaccination Desk",
                scheduleDays = "Every Wednesday",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Child's Immunization Book / Pink Health Card",
                    "Mother or Guardian's Valid ID",
                    "Barangay Resident Clearance"
                ),
                description = "Permanent routine vaccination program for infants and young children (BCG, Hepatitis B, Pentavalent, OPV/IPV, MMR), along with free Vitamin A supplementation.",
                procedureSteps = listOf(
                    "Register infant's immunization card at Room 104 reception window.",
                    "BHW checks child's weight, height, and vaccine schedule history.",
                    "Nurse administers required vaccine and logs next scheduled session date."
                )
            ),
            MedicalServiceItem(
                id = "4",
                title = "Prenatal & Postnatal Care",
                category = "Maternal Health",
                icon = "🤰",
                roomNumber = "Room 102 - Maternal & Midwife Station",
                scheduleDays = "Every Monday & Tuesday",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Mother & Child Health Book (Pink Book)",
                    "Member Data Record (MDR) / PhilHealth Details",
                    "Valid Government ID"
                ),
                description = "Dedicated prenatal checkups, fetal growth monitoring, blood pressure screening, maternal tetanus vaccination, and postnatal care for mothers.",
                procedureSteps = listOf(
                    "Queue at Maternal Care Desk (Room 102) intake desk.",
                    "Initial blood pressure check and weight recording by BHW.",
                    "Consultation with midwife or physician for fetal monitoring."
                )
            ),
            MedicalServiceItem(
                id = "5",
                title = "MCP Birthing Facility (Normal Deliveries)",
                category = "Maternal Health",
                icon = "👶",
                roomNumber = "MCP Birthing Wing - Right Wing",
                scheduleDays = "Daily (24/7 Operations)",
                operatingHours = "24 Hours / 7 Days a Week",
                venue = "MCP Birthing Facility (Right side of Health Center)",
                requirements = listOf(
                    "Complete Prenatal Pink Book (minimum 4 checkups)",
                    "Member Data Record (MDR) / PhilHealth Details",
                    "Mother & Father Valid Government IDs",
                    "Prepared Newborn Delivery Kit & Baby Clothes"
                ),
                description = "Round-the-clock Maternity Care Package (MCP) birthing facility providing safe, professional normal spontaneous deliveries for resident mothers.",
                procedureSteps = listOf(
                    "Direct admission at 24/7 MCP Birthing Wing intake desk.",
                    "Duty midwife evaluation and emergency obstetric triage.",
                    "Admission to birthing suite and continuous labor monitoring."
                )
            ),
            MedicalServiceItem(
                id = "6",
                title = "Family Planning & Reproductive Health",
                category = "Reproductive Health",
                icon = "👨‍👩‍👧",
                roomNumber = "Room 105 - Family Planning Office",
                scheduleDays = "Every Thursday",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Valid ID",
                    "Family Planning Client Passbook (for returning patients)"
                ),
                description = "Professional counseling and distribution of family planning methods including pills, DMPA injectables, sub-dermal implants, IUDs, and condoms.",
                procedureSteps = listOf(
                    "Private intake and registration in Room 105.",
                    "One-on-one counseling with trained family planning counselor.",
                    "Administration or supply distribution of selected method."
                )
            ),
            MedicalServiceItem(
                id = "7",
                title = "Senior Citizen NCD Medicine Distribution",
                category = "Maintenance Medicine",
                icon = "💊",
                roomNumber = "Room 106 - Pharmacy & Distribution Unit",
                scheduleDays = "Every 4th Thursday of the Month",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Senior Citizen ID Card",
                    "Updated Doctor's Prescription for maintenance drugs",
                    "Senior Health Passbook / Booklet"
                ),
                description = "Monthly supply distribution of maintenance medications for Senior Citizens diagnosed with Hypertension, Diabetes, or High Cholesterol.",
                procedureSteps = listOf(
                    "Present Senior Citizen ID and prescription at Room 106 window.",
                    "Pharmacist verifies doctor's prescription against resident record.",
                    "Sign logbook and receive monthly medicine allocation."
                )
            ),
            MedicalServiceItem(
                id = "8",
                title = "Non-Senior NCD Maintenance Medicine",
                category = "Maintenance Medicine",
                icon = "🏥",
                roomNumber = "Room 106 - Pharmacy & Distribution Unit",
                scheduleDays = "Every 5th Thursday of the Month",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Valid Government ID",
                    "Official Physician Prescription",
                    "Barangay Health Center Index Card"
                ),
                description = "Monthly distribution of essential chronic disease maintenance medications for registered non-senior adult patients.",
                procedureSteps = listOf(
                    "Present patient index card and prescription at Room 106.",
                    "Medication counseling with health center pharmacist.",
                    "Receive assigned medication package."
                )
            ),
            MedicalServiceItem(
                id = "9",
                title = "National Tuberculosis Program (NTP) & PICT Screening",
                category = "Disease Screening",
                icon = "🔬",
                roomNumber = "Room 107 - Screening Laboratory",
                scheduleDays = "Every Thursday",
                operatingHours = "8:00 AM - 5:00 PM",
                venue = "Barangay Carmen Health Center",
                requirements = listOf(
                    "Doctor Referral or Sputum Specimen",
                    "Valid Government ID"
                ),
                description = "Free GeneXpert sputum testing for TB detection, DOTS treatment program management, and voluntary Provider-Initiated Counseling & Testing (PICT).",
                procedureSteps = listOf(
                    "Submit referral or report to Room 107 intake window.",
                    "Confidential screening intake and specimen collection.",
                    "Results release and counseling orientation."
                )
            )
        )
    }

    // Handle back gesture/button when viewing details
    if (selectedService != null) {
        BackHandler {
            selectedService = null
        }
    }

    val filteredServices = carmenMedicalServicesList.filter { service ->
        val matchesCategory = (selectedCategory == "All") || service.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                service.title.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true) ||
                service.roomNumber.contains(searchQuery, ignoreCase = true) ||
                service.requirements.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedService != null) {
                        Text(
                            text = selectedService!!.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    } else {
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
                                Text(
                                    text = "Barangay Carmen, CDO",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (selectedService != null) {
                        IconButton(onClick = { selectedService = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Directory"
                            )
                        }
                    }
                },
                actions = {
                    if (selectedService == null) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedService != null) {
                ServiceDetailView(
                    service = selectedService!!
                ) {
                    selectedService = null
                }
            } else {
                ServiceDirectoryListView(
                    services = filteredServices,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    onServiceClick = { selectedService = it }
                )
            }
        }
    }
}

@Composable
fun ServiceDirectoryListView(
    services: List<MedicalServiceItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onServiceClick: (MedicalServiceItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Permanent Directory Header Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🏥", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Facility Services Directory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Permanent reference guide for operating hours, room locations, and required IDs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search treatment, room, or ID...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (services.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No medical services found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try adjusting your search terms or category filter.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(services) { service ->
                    DirectoryServiceCard(
                        service = service,
                        onClick = { onServiceClick(service) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun DirectoryServiceCard(
    service: MedicalServiceItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = service.icon, fontSize = 22.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = service.category,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Room",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = service.roomNumber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Hours",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = service.scheduleDays,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action hint button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 ${service.requirements.size} Required IDs / Documents",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "View Details →",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceDetailView(
    service: MedicalServiceItem,
    onBackClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Hero Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = service.icon, fontSize = 28.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = service.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Ref Specs: Room Number, Operating Hours, Venue
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Facility Reference & Hours",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    DetailRefRow(
                        icon = "📍",
                        label = "Room / Station Location",
                        value = service.roomNumber,
                        isHighlighted = true
                    )

                    DetailRefRow(
                        icon = "⏰",
                        label = "Operating Hours",
                        value = "${service.scheduleDays}\n${service.operatingHours}",
                        isHighlighted = false
                    )

                    DetailRefRow(
                        icon = "🏥",
                        label = "Facility Venue",
                        value = service.venue,
                        isHighlighted = false
                    )
                }
            }
        }

        // Requirements & Documents Needed (Static Reference Checklist)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📋", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Requirements & Documents Needed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Please bring original documents or clear photocopies when visiting the health center:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    service.requirements.forEach { req ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✅", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = req,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Note",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Note: First-time patients will be registered at the desk and issued a Health Center Patient Index Card.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Triage & Patient Walk-In Procedure
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🚶‍♂️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Walk-In & Triage Procedure",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    service.procedureSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = (index + 1).toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Help Desk & Contact Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Inquiries & Health Center Help Desk",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = service.contactInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Back Button
        item {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Back to Services Directory Guide",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun DetailRefRow(
    icon: String,
    label: String,
    value: String,
    isHighlighted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
