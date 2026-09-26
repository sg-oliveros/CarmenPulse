package com.example.carmenpulse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

//represents an individual comment or message inside a forum thread
data class ForumComment(
    val id: String,
    val author: String,
    val role: String,
    val isOfficialStaff: Boolean, //true if posted by a BHW health worker
    val message: String,
    val timestamp: String
)

//represents a discussion thread tied to a specific health advisory or announcement
data class ForumThread(
    val advisoryId: String,
    val title: String,
    val category: String,
    val date: String,
    val snippet: String,
    val comments: MutableList<ForumComment>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDiscussionScreen(
    initialAdvisory: Advisory? = null,
    currentUser: UserData? = null,
    onBack: () -> Unit = {}
) {
    // Linked announcement forum threads store | mock database of threads
    val threadsMap = remember {
        mutableStateMapOf(
            "1" to ForumThread(
                advisoryId = "1",
                title = "Dengue Prevention & Clean-Up Drive",
                category = "Health Drive",
                date = "June 15, 2026",
                snippet = "Free misting, larvicide distribution, and barangay clean-up drive scheduled across Purok 1 to 4 starting 7:00 AM.",
                comments = mutableStateListOf(
                    ForumComment(
                        id = "101",
                        author = "Carlo Garcia",
                        role = "Resident",
                        isOfficialStaff = false,
                        message = "Will the misting team cover inner alleys in Purok 3?",
                        timestamp = "2 hours ago"
                    ),
                    ForumComment(
                        id = "102",
                        author = "BHW Sanitation Team",
                        role = "BHW / Health Staff",
                        isOfficialStaff = true,
                        message = "Magandang araw! Yes, the misting team will move street-by-street starting 7:00 AM. Please keep windows open and cover exposed food.",
                        timestamp = "1 hour ago"
                    )
                )
            ),
            "2" to ForumThread(
                advisoryId = "2",
                title = "Free Dental Mission & Medical Consultation",
                category = "Dental Mission",
                date = "June 18, 2026",
                snippet = "Free dental checkup, tooth extraction, and routine consultation at Barangay Carmen Health Center, Room 103.",
                comments = mutableStateListOf(
                    ForumComment(
                        id = "201",
                        author = "Juan Dela Cruz",
                        role = "Resident",
                        isOfficialStaff = false,
                        message = "What time should I line up for Saturday's dental mission?",
                        timestamp = "3 hours ago"
                    ),
                    ForumComment(
                        id = "202",
                        author = "Nurse Maria",
                        role = "BHW Health Staff",
                        isOfficialStaff = true,
                        message = "Maayong buntag! Triage and line-up start at 7:30 AM outside Room 103 (Dental Clinic). Please bring a valid ID and resident clearance.",
                        timestamp = "2 hours ago"
                    ),
                    ForumComment(
                        id = "203",
                        author = "Elena Santos",
                        role = "Resident",
                        isOfficialStaff = false,
                        message = "Are children allowed for tooth extraction or checkup?",
                        timestamp = "1 hour ago"
                    ),
                    ForumComment(
                        id = "204",
                        author = "Dr. Ramos",
                        role = "Municipal Physician",
                        isOfficialStaff = true,
                        message = "Yes, pediatric dental checkups and fluoride applications are available for children aged 5 and above.",
                        timestamp = "30 mins ago"
                    )
                )
            ),
            "3" to ForumThread(
                advisoryId = "3",
                title = "Infant Vaccination & Free Vitamin Distribution",
                category = "Pediatrics",
                date = "June 20, 2026",
                snippet = "Scheduled routine infant vaccinations and Vitamin A supplementation. Bring your child's Immunization Book.",
                comments = mutableStateListOf(
                    ForumComment(
                        id = "301",
                        author = "Maria Flores",
                        role = "Resident",
                        isOfficialStaff = false,
                        message = "What if my baby missed the 3rd dose of Pentavalent vaccine?",
                        timestamp = "Yesterday"
                    ),
                    ForumComment(
                        id = "302",
                        author = "Midwife Rosa",
                        role = "BHW / Pediatrics",
                        isOfficialStaff = true,
                        message = "You can get catch-up doses during this Wednesday session in Room 104. Just bring your baby's Pink Immunization Book.",
                        timestamp = "Yesterday"
                    )
                )
            ),
            "4" to ForumThread(
                advisoryId = "4",
                title = "Barangay Health Center Community Forum",
                category = "General Q&A",
                date = "Ongoing",
                snippet = "Open community discussion hub for general health inquiries, operating hours questions, and resident feedback.",
                comments = mutableStateListOf(
                    ForumComment(
                        id = "401",
                        author = "Roberto Cruz",
                        role = "Resident",
                        isOfficialStaff = false,
                        message = "What are the operating hours of the Health Center Pharmacy for senior medicines?",
                        timestamp = "4 hours ago"
                    ),
                    ForumComment(
                        id = "402",
                        author = "Pharmacy Desk Staff",
                        role = "BHW / Health Staff",
                        isOfficialStaff = true,
                        message = "Senior NCD maintenance medicine distribution takes place every 4th Thursday of the month from 8:00 AM to 5:00 PM in Room 106.",
                        timestamp = "3 hours ago"
                    )
                )
            )
        )
    }

    // Active thread state | tracks with announcement tab the user clicked on
    var activeThreadId by remember(initialAdvisory) {
        mutableStateOf(initialAdvisory?.id ?: "2")
    }

    //holds whatever text the user is typing into the comment box
    var commentInput by remember { mutableStateOf("") }

    //this grabs the exact thread object corresponding to the currently selected tab
    val activeThread = threadsMap[activeThreadId] ?: threadsMap["2"]!!

    Scaffold( //header section with title and back nav button
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
                            text = "Linked Announcement Discussion Threads",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home Feed"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        //text field input and send button for posting new comments
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //text input field for typing quations
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = {
                            Text(
                                "Ask a question about this announcement...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    //send button action
                    IconButton(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                val authorName = currentUser?.name ?: "You (Resident)"
                                val residentPurok = currentUser?.purok ?: "Purok 1"

                                //adds the newly typed comment directly into the active thread
                                activeThread.comments.add(
                                    ForumComment(
                                        id = System.currentTimeMillis().toString(),
                                        author = authorName,
                                        role = "Resident",
                                        isOfficialStaff = false,
                                        message = commentInput.trim(),
                                        timestamp = "Just now"
                                    )
                                )
                                commentInput = "" //clears out the input field after it is sent
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
            // horizontal filter chips to switch between announcements
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
                    Text(
                        text = "Select Announcement Q&A Thread:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(threadsMap.values.toList()) { thread ->
                            FilterChip(
                                selected = activeThreadId == thread.advisoryId,
                                onClick = { activeThreadId = thread.advisoryId },
                                label = {
                                    Text(
                                        text = "💬 ${thread.title}",
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            //this displays announcement summary card and all comment bubbles
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // shows details of the selected topic
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
                                        text = "📢 Linked Announcement Thread",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Text(
                                    text = activeThread.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = activeThread.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = activeThread.snippet,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

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

                item {
                    Text(
                        text = "Resident Questions & BHW Official Answers (${activeThread.comments.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                //loops trough each comment in the active thread
                items(activeThread.comments) { comment ->
                    CommentBubble(comment = comment)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CommentBubble(comment: ForumComment) {
    val isBHW = comment.isOfficialStaff

    //BHW staff cards get a tinted container; residents have a white card
    val bubbleColor = if (isBHW) MaterialTheme.colorScheme.secondaryContainer else Color.White
    val border = if (isBHW) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBHW) 2.dp else 1.dp),
        colors = CardDefaults.cardColors(containerColor = bubbleColor),
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isBHW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    //show green verifiedd badge for BHW staff, or simple purok tag for residents
                    if (isBHW) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Official",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = comment.role,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${comment.role}",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = comment.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
