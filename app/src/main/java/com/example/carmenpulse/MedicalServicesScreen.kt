package com.example.carmenpulse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.ChildFriendly
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PregnantWoman
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carmenpulse.data.models.MedicalServices

data class MedicalServiceItem(
    val id: String,
    val rawId: Long,
    val title: String,
    val category: String,
    val roomNumber: String,
    val operatingHours: String,
    val scheduleDays: String,
    val scheduleTime: String? = null,
    val venue: String,
    val requirements: List<String>,
    val description: String,
    val isInterested: Boolean = false,
    val interestCount: Int = 0,
    val contactInfo: String = "Barangay Carmen Health Center Desk: (088) 858-1234",
)

fun getCategoryIcon(category: String): ImageVector {
    return when {
        category.contains("All", ignoreCase = true) -> Icons.Outlined.Category
        category.contains("Primary", ignoreCase = true) -> Icons.Outlined.HealthAndSafety
        category.contains("Dental", ignoreCase = true) -> Icons.Outlined.MedicalServices
        category.contains("Pediatrics", ignoreCase = true) -> Icons.Outlined.ChildCare
        category.contains("Maternal", ignoreCase = true) -> Icons.Outlined.PregnantWoman
        category.contains("Maintenance", ignoreCase = true) -> Icons.Outlined.Medication
        category.contains("Screening", ignoreCase = true) -> Icons.Outlined.Biotech
        category.contains("Reproductive", ignoreCase = true) -> Icons.Outlined.FamilyRestroom
        else -> Icons.Outlined.LocalHospital
    }
}

fun getServiceIcon(category: String, title: String): ImageVector {
    return when {
        title.contains("Dental", ignoreCase = true) -> Icons.Outlined.MedicalServices
        title.contains("Consultation", ignoreCase = true) -> Icons.Outlined.HealthAndSafety
        title.contains("Immunization", ignoreCase = true) -> Icons.Outlined.Vaccines
        title.contains("Prenatal", ignoreCase = true) -> Icons.Outlined.PregnantWoman
        title.contains("Birthing", ignoreCase = true) -> Icons.Outlined.ChildFriendly
        title.contains("Family Planning", ignoreCase = true) -> Icons.Outlined.FamilyRestroom
        title.contains("Medicine", ignoreCase = true) -> Icons.Outlined.Medication
        title.contains("TB", ignoreCase = true) || title.contains("Screening", ignoreCase = true) -> Icons.Outlined.Biotech
        else -> Icons.Outlined.LocalHospital
    }
}

private fun formatTimeWithoutSeconds(timeStr: String?): String? {
    if (timeStr.isNullOrBlank()) return null

    val timeWithSecondsRegex = Regex("""\b(\d{1,2}):(\d{2}):\d{2}\b""")
    val formatted = timeWithSecondsRegex.replace(timeStr) { match ->
        val hour = match.groupValues[1].padStart(2, '0')
        val minute = match.groupValues[2]
        "$hour:$minute"
    }

    val shortTimeRegex = Regex("""\b(\d):(\d{2})\b""")
    return shortTimeRegex.replace(formatted) { match ->
        val hour = match.groupValues[1].padStart(2, '0')
        val minute = match.groupValues[2]
        "$hour:$minute"
    }
}

private fun MedicalServices.toUiModel(
    interestedIds: Set<Long>,
    countsMap: Map<Long, Int>
): MedicalServiceItem {
    val parsedRequirements = requirements
        ?.split("\n")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    val statusFromDb = when {
        status.isNullOrBlank() -> "Upcoming"
        else -> status.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    val formattedTime = formatTimeWithoutSeconds(schedule_time)

    return MedicalServiceItem(
        id = service_id.toString(),
        rawId = service_id,
        title = service_name,
        category = statusFromDb,
        roomNumber = location_venue,
        operatingHours = formattedTime ?: "Not specified",
        scheduleDays = schedule_date,
        scheduleTime = formattedTime,
        venue = location_venue,
        requirements = parsedRequirements,
        description = description ?: "No description available.",
        isInterested = interestedIds.contains(service_id),
        interestCount = countsMap[service_id] ?: 0
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalServicesScreen(
    viewModel: MedicalServicesViewModel = viewModel(),
    onNavigateToForum: (Long) -> Unit = {}
) {

    val context = LocalContext.current
    val services by viewModel.services.collectAsState()
    val interestedIds by viewModel.interestedServiceIds.collectAsState()
    val interestCounts by viewModel.interestCounts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val canLoadMore by viewModel.canLoadMore.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    val notifications by viewModel.notifications.collectAsState()
    val isLoadingNotifications by viewModel.isLoadingNotifications.collectAsState()
    val hasUnread by viewModel.hasUnread.collectAsState()

    var showNotificationsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchNotifications(context)
    }

    var selectedService by remember {
        mutableStateOf<MedicalServiceItem?>(null)
    }

    val statuses = listOf(
        "All",
        "Upcoming",
        "Ongoing",
        "Completed",
        "Cancelled"
    )

    val medicalServiceItems = remember(services, interestedIds, interestCounts) {
        services.map {
            it.toUiModel(interestedIds, interestCounts)
        }
    }

    if (selectedService != null) {
        val currentSelected = medicalServiceItems.find { it.id == selectedService!!.id } ?: selectedService!!
        BackHandler {
            selectedService = null
        }
        selectedService = currentSelected
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "CareConnect Logo",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CareConnect",
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
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.fetchNotifications(context)
                            viewModel.markNotificationsAsRead(context)
                            showNotificationsSheet = true
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (hasUnread) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    service = selectedService!!,
                    onInterestToggle = { viewModel.toggleInterest(selectedService!!.rawId) },
                    onNavigateToForum = { onNavigateToForum(selectedService!!.rawId) }
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = { viewModel.fetchServices() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
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
                                Icon(
                                    imageVector = Icons.Filled.LocalHospital,
                                    contentDescription = "Facility Directory",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Facility Services Directory",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Services Directory Guide for the residents.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        var typedSearchQuery by remember(searchQuery) { mutableStateOf(searchQuery) }

                        OutlinedTextField(
                            value = typedSearchQuery,
                            onValueChange = { typedSearchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search treatment or venue", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                IconButton(onClick = { viewModel.onSearchQuerySubmit(typedSearchQuery) }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search button")
                                }
                            },
                            trailingIcon = {
                                if (typedSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        typedSearchQuery = ""
                                        viewModel.onSearchQuerySubmit("")
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                viewModel.onSearchQuerySubmit(typedSearchQuery)
                            }),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(statuses) { status ->
                                FilterChip(
                                    selected = selectedStatus == status,
                                    onClick = { viewModel.onStatusSelected(status) },
                                    label = { Text(status, style = MaterialTheme.typography.labelMedium) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = getCategoryIcon(status),
                                            contentDescription = status,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (medicalServiceItems.isEmpty() && !isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ManageSearch,
                                        contentDescription = "Search Empty",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No medical services found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Try adjusting your search terms or status filter.",
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
                                itemsIndexed(medicalServiceItems) { index, service ->
                                    if (index >= medicalServiceItems.size - 1 && canLoadMore && !isLoadingMore) {
                                        LaunchedEffect(index) {
                                            viewModel.loadMoreServices()
                                        }
                                    }

                                    DirectoryServiceCard(
                                        service = service,
                                        onClick = { selectedService = service },
                                        onInterestToggle = { viewModel.toggleInterest(service.rawId) },
                                        onNavigateToForum = { onNavigateToForum(service.rawId) }
                                    )
                                }

                                if (isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = "Loading more services...",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                } else if (canLoadMore) {
                                    item {
                                        OutlinedButton(
                                            onClick = { viewModel.loadMoreServices() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Load More Services")
                                        }
                                    }
                                }

                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }
                    }
                }
            }
        }

        if (showNotificationsSheet) {
            NotificationsBottomSheet(
                notifications = notifications,
                isLoading = isLoadingNotifications,
                onDismiss = { showNotificationsSheet = false }
            )
        }
    }
}

@Composable
fun DirectoryServiceCard(
    service: MedicalServiceItem,
    onClick: () -> Unit,
    onInterestToggle: () -> Unit,
    onNavigateToForum: () -> Unit
) {
    val serviceIcon = getServiceIcon(service.category, service.title)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
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
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = serviceIcon,
                            contentDescription = service.title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                        val hasDate = service.scheduleDays.isNotBlank()
                        val hasTime = !service.scheduleTime.isNullOrBlank()

                        if (hasDate || hasTime) {
                            val scheduleText = buildString {
                                if (hasDate) append(service.scheduleDays)
                                if (hasDate && hasTime) append(" • ")
                                if (hasTime) append(service.scheduleTime)
                            }
                            Text(
                                text = scheduleText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onInterestToggle,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (service.isInterested) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                    ),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (service.isInterested) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = "Interest",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (service.isInterested) "Interested (${service.interestCount})" else "Mark Interested (${service.interestCount})",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                IconButton(
                    onClick = onNavigateToForum,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = "Forum Q&A",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceDetailView(
    service: MedicalServiceItem,
    onInterestToggle: () -> Unit,
    onNavigateToForum: () -> Unit
) {
    val serviceIcon = getServiceIcon(service.category, service.title)
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                Icon(
                                    imageVector = serviceIcon,
                                    contentDescription = service.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
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
                        text = "Facility Reference & Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    DetailRefRow(
                        icon = Icons.Outlined.MeetingRoom,
                        label = "Room / Station Location",
                        value = service.roomNumber,
                        isHighlighted = true
                    )

                    if (service.scheduleDays.isNotBlank()) {
                        DetailRefRow(
                            icon = Icons.Outlined.CalendarToday,
                            label = "Schedule Date",
                            value = service.scheduleDays,
                            isHighlighted = false
                        )
                    }

                    DetailRefRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Schedule Time",
                        value = service.scheduleTime ?: "Not specified",
                        isHighlighted = false
                    )

                    DetailRefRow(
                        icon = Icons.Outlined.LocalHospital,
                        label = "Facility Venue",
                        value = service.venue,
                        isHighlighted = false
                    )
                }
            }
        }

        if (service.requirements.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                                contentDescription = "Requirements",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
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
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Checked",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
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
                    }
                }
            }
        }

        item {
            Button(
                onClick = onInterestToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (service.isInterested) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                    contentColor = if (service.isInterested) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(26.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = if (service.isInterested) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Mark Interested",
                    modifier = Modifier.size(20.dp),
                    tint = if (service.isInterested) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (service.isInterested) "Interested (${service.interestCount})" else "Mark Interested (${service.interestCount})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        item {
            Button(
                onClick = onNavigateToForum,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = "Service Discussion Forum",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Go to Service Forum",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun DetailRefRow(
    icon: ImageVector,
    label: String,
    value: String,
    isHighlighted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
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
