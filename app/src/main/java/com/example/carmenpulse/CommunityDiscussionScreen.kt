package com.example.carmenpulse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carmenpulse.data.models.Comment
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDiscussionScreen(
    initialServiceId: Long? = null,
    onBack: () -> Unit = {},
    viewModel: CommunityDiscussionViewModel = viewModel()
) {
    val services by viewModel.services.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var activeServiceId by remember(initialServiceId) {
        mutableStateOf<Long?>(initialServiceId)
    }
    var commentInput by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var isInitialLoad by remember { mutableStateOf(true) }

    LaunchedEffect(services, initialServiceId) {
        if (initialServiceId != null && services.any { it.service_id == initialServiceId }) {
            activeServiceId = initialServiceId
        } else if (activeServiceId == null && services.isNotEmpty()) {
            activeServiceId = services.first().service_id
        }
    }

    LaunchedEffect(activeServiceId) {
        isInitialLoad = true
        activeServiceId?.let { viewModel.loadComments(it) }
    }

    // Auto scroll to bottom when opening thread or when new comment is posted (Messenger style)
    LaunchedEffect(comments) {
        if (comments.isNotEmpty()) {
            val targetIndex = (comments.size + 3).coerceAtLeast(0)
            if (isInitialLoad) {
                listState.scrollToItem(targetIndex)
                isInitialLoad = false
            } else {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    // Detect when user scrolls up to top to load older messages
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(isAtTop) {
        if (isAtTop && !isLoading && comments.isNotEmpty()) {
            activeServiceId?.let { viewModel.loadMoreComments(it) }
        }
    }

    val activeService = services.find { it.service_id == activeServiceId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Community Hub Forum",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Medical Services Discussion Threads",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = {
                            Text(
                                "Ask a question about this medical service...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    IconButton(
                        onClick = {
                            val serviceId = activeServiceId
                            if (serviceId != null && commentInput.isNotBlank()) {
                                viewModel.postComment(serviceId, commentInput)
                                commentInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Post Comment",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
                    Text(
                        text = "Select Medical Service Q&A Thread:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = activeService?.service_name ?: "Select a Medical Service",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            services.forEach { service ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = service.service_name,
                                            fontWeight = if (service.service_id == activeServiceId) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        activeServiceId = service.service_id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                if (activeService != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "Medical Service Thread",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    Text(
                                        text = activeService.status ?: "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = activeService.service_name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                if (!activeService.description.isNullOrBlank()) {
                                    Text(
                                        text = activeService.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "📍 ${activeService.location_venue}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "📅 ${activeService.schedule_date}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Questions asked in this thread are answered by Barangay Health Workers (BHW).",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Resident Questions & BHW Official Answers (${comments.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (comments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "No questions yet for this service. Be the first to ask!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(comments) { comment ->
                        CommentBubble(comment = comment)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CommentBubble(comment: Comment) {
    val roleId = comment.users?.role_id
    val isBHW = roleId == 1L || comment.user_id == 16L || comment.user_id == 30L || comment.user_id == 1L || (roleId != null && roleId != 2L)
    val authorName = if (isBHW) "BHW Official" else "Anonymous"
    val roleLabel = if (isBHW) "BHW" else "Resident"

    // BHW (role_id = 1 or user_id = 30) comments get dark green body area, bright green outline & clear text
    val bhwGreenBg = Color(0xFF1B4D2E)
    val bhwGreenBorder = Color(0xFF4CAF50)
    val bhwGreenText = Color(0xFFF1F8E9)
    val bhwSubText = Color(0xFFC8E6C9)

    val bubbleColor = if (isBHW) bhwGreenBg else MaterialTheme.colorScheme.surface
    val border = if (isBHW) {
        BorderStroke(2.dp, bhwGreenBorder)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBHW) 3.dp else 1.dp),
        colors = CardDefaults.cardColors(containerColor = bubbleColor),
        border = border
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = authorName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isBHW) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (isBHW) {
                        Surface(shape = RoundedCornerShape(6.dp), color = bhwGreenBorder) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Official BHW",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = roleLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Text(
                                text = roleLabel,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = parseAndFormatDate(comment.created_at),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isBHW) bhwSubText else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBHW) FontWeight.Medium else FontWeight.Normal,
                color = if (isBHW) bhwGreenText else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun parseAndFormatDate(rawDateStr: String?): String {
    if (rawDateStr.isNullOrBlank()) return ""
    val pattern = "MMM dd, yyyy • h:mm a"
    val outputFormatter = DateTimeFormatter.ofPattern(pattern)

    return try {
        val parsedOffsetDateTime = try {
            OffsetDateTime.parse(rawDateStr)
        } catch (e: Exception) {
            null
        }

        if (parsedOffsetDateTime != null) {
            val localTime = parsedOffsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            return localTime.format(outputFormatter)
        }

        val normalized = rawDateStr.replace(" ", "T")

        val parsedLocalDateTime = try {
            LocalDateTime.parse(normalized)
        } catch (e: Exception) {
            null
        }

        if (parsedLocalDateTime != null) {
            return parsedLocalDateTime.format(outputFormatter)
        }

        val parsedZonedDateTime = try {
            ZonedDateTime.parse(rawDateStr)
        } catch (e: Exception) {
            null
        }

        if (parsedZonedDateTime != null) {
            return parsedZonedDateTime.format(outputFormatter)
        }

        rawDateStr
    } catch (e: Exception) {
        rawDateStr ?: ""
    }
}
