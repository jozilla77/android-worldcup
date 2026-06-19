package com.jozilla.worldcup.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jozilla.worldcup.model.Goal
import com.jozilla.worldcup.model.Match
import com.jozilla.worldcup.model.Player
import com.jozilla.worldcup.ui.theme.*
import com.jozilla.worldcup.viewmodel.WorldCupViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MatchesScreen(viewModel: WorldCupViewModel = viewModel()) {
    val matches by viewModel.matches.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }
    var expandedMatchId by remember { mutableStateOf<String?>(null) }
    var activeTacticalMatchId by remember { mutableStateOf<String?>(null) }

    // Roster player dialog
    var selectedPlayerForDialog by remember { mutableStateOf<Pair<Player, String>?>(null) }

    val filteredMatches = remember(matches, selectedFilter) {
        val list = when (selectedFilter) {
            "LIVE" -> matches.filter { it.status == "LIVE" }
            "UPCOMING" -> matches.filter { it.status == "UPCOMING" }
            "COMPLETED" -> matches.filter { it.status == "COMPLETED" }
            else -> matches
        }
        list.sortedWith(compareBy<Match> {
            when (it.status) {
                "LIVE" -> 0
                "UPCOMING" -> 1
                else -> 2
            }
        }.thenBy { it.kickoffUtc })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Nav Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(GlassCard, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FilterTab(text = "All", active = selectedFilter == "all") { selectedFilter = "all" }
            FilterTab(text = "Live 🔴", active = selectedFilter == "LIVE") { selectedFilter = "LIVE" }
            FilterTab(text = "Upcoming", active = selectedFilter == "UPCOMING") { selectedFilter = "UPCOMING" }
            FilterTab(text = "Completed", active = selectedFilter == "COMPLETED") { selectedFilter = "COMPLETED" }
        }

        if (filteredMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matches logged under this category.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftGray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMatches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        expanded = expandedMatchId == match.id,
                        onExpandToggle = {
                            expandedMatchId = if (expandedMatchId == match.id) null else match.id
                            activeTacticalMatchId = null // collapse tactical if we switch
                        },
                        activeTactical = activeTacticalMatchId == match.id,
                        onTacticalToggle = {
                            activeTacticalMatchId = if (activeTacticalMatchId == match.id) null else match.id
                        },
                        onPlayerClick = { player, teamName ->
                            selectedPlayerForDialog = Pair(player, teamName)
                        }
                    )
                }
            }
        }
    }

    // Player stats overlay dialog
    selectedPlayerForDialog?.let { (player, teamName) ->
        PlayerStatsDialog(
            player = player,
            teamName = teamName,
            onDismiss = { selectedPlayerForDialog = null }
        )
    }
}

@Composable
fun RowScope.FilterTab(text: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) NeonPink else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (active) Color.White else SoftGray,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
        )
    }
}

@Composable
fun MatchCard(
    match: Match,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    activeTactical: Boolean,
    onTacticalToggle: () -> Unit,
    onPlayerClick: (Player, String) -> Unit
) {
    // Convert UTC kickoff to device local readable timezone
    val localTimeText = remember(match.kickoffUtc) {
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(match.kickoffUtc)
            if (date != null) {
                val localFormat = SimpleDateFormat("HH:mm (MMM d)", Locale.getDefault())
                localFormat.timeZone = TimeZone.getDefault()
                localFormat.format(date)
            } else "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (match.status == "LIVE") NeonPink.copy(alpha = 0.8f) else GlassCardBorder,
                RoundedCornerShape(16.dp)
            )
            .shadow(
                elevation = if (match.status == "LIVE") 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = NeonPink
            ),
        colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onExpandToggle() }
                .padding(12.dp)
        ) {
            // Card Header (Status & Stadium Time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge
                val badgeColor = when (match.status) {
                    "LIVE" -> NeonPink
                    "COMPLETED" -> Color(0xFF155724)
                    else -> GlassCardBorder
                }
                val labelText = when (match.status) {
                    "LIVE" -> if (match.halfTime) "Halftime" else "${match.playTime}'"
                    "COMPLETED" -> "Completed"
                    else -> "Upcoming"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = labelText.uppercase(Locale.ROOT),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                    )
                }

                Text(
                    text = "🏟️ Kickoff: ${match.originalTime}",
                    color = SoftGray,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Score Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team flag and name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(
                        text = match.flagHome,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = match.teamHome,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        maxLines = 1
                    )
                }

                // Scores Box
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.8f)
                ) {
                    if (match.status != "UPCOMING") {
                        Text(
                            text = match.scoreHome.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (match.status == "LIVE") NeonPink else Color.White
                        )
                        Text(
                            text = " : ",
                            fontSize = 20.sp,
                            color = SoftGray
                        )
                        Text(
                            text = match.scoreAway.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (match.status == "LIVE") NeonPink else Color.White
                        )
                    } else {
                        Text(
                            text = "VS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                }

                // Away Team flag and name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(
                        text = match.teamAway,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = match.flagAway,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time zones row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassCardBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏰ Local user time:",
                    color = SoftGray,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                )
                Text(
                    text = localTimeText,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                )
            }

            // Expanded content drawer
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable(enabled = false) {} // block click through
                ) {
                    Divider(color = GlassCardBorder, thickness = 1.dp)

                    // Scoring goals and Cards event list
                    Spacer(modifier = Modifier.height(12.dp))
                    MatchEventsList(match)

                    // Tactical Pitch view trigger (only if starting roster XI exists)
                    if (match.rosterHome.isNotEmpty() && match.rosterAway.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onTacticalToggle() },
                            colors = ButtonDefaults.buttonColors(containerColor = GlassCardBorder),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (activeTactical) "📋 Hide Lineups Pitch" else "📋 View Tactical 3D Field",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                            )
                        }

                        // Pitch Perspective Container
                        AnimatedVisibility(
                            visible = activeTactical,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .graphicsLayer {
                                        // Lean the field to simulate 3D tactical boards!
                                        rotationX = 35f
                                        cameraDistance = 12f * density
                                    }
                            ) {
                                TacticalPitchOverlay(match, onPlayerClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchEventsList(match: Match) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassCard, RoundedCornerShape(12.dp))
            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "MATCH STATS & LOGS",
                color = SoftGray,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (match.goals.isEmpty() && match.cards.isEmpty()) {
                Text(
                    text = "No goals or bookings currently logged in this game.",
                    color = SoftGray,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                // Goals List
                match.goals.forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚽", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${goal.scorer} (${if (goal.team == "home") match.teamHome else match.teamAway})",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${goal.minute}'",
                            color = CyberCyan,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                    }
                }

                // Cards list (yellow/red cards)
                match.cards.forEach { card ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (card.type == "red") "🟥" else "🟨", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${card.player} (${if (card.team == "home") match.teamHome else match.teamAway})",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${card.minute}'",
                            color = CyberCyan,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}

// 3D-perspective-styled Tactical Pitch overlay canvas drawing starting XI positions
@Composable
fun TacticalPitchOverlay(match: Match, onPlayerClick: (Player, String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(FieldGreen)
    ) {
        // Draw Field marking lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val solidLinePaint = Stroke(width = 2.dp.toPx())

            // Midfield line
            drawLine(
                color = Color.White.copy(alpha = 0.6f),
                start = Offset(0f, height / 2),
                end = Offset(width, height / 2),
                strokeWidth = 2.dp.toPx()
            )

            // Center Circle
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 35.dp.toPx(),
                center = Offset(width / 2, height / 2),
                style = solidLinePaint
            )

            // Center Spot
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 4.dp.toPx(),
                center = Offset(width / 2, height / 2)
            )

            // Penalty Areas
            // Home penalty area (bottom)
            drawRect(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(width * 0.2f, height * 0.75f),
                size = Size(width * 0.6f, height * 0.25f),
                style = solidLinePaint
            )
            // Away penalty area (top)
            drawRect(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(width * 0.2f, 0f),
                size = Size(width * 0.6f, height * 0.25f),
                style = solidLinePaint
            )
        }

        // Home Players
        match.rosterHome.forEach { p ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alignPlayer(p.x, p.y)
                    .clickable { onPlayerClick(p, match.teamHome) }
            ) {
                PlayerJerseyPin(player = p, homeColor = true)
            }
        }

        // Away Players
        match.rosterAway.forEach { p ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alignPlayer(p.x, p.y)
                    .clickable { onPlayerClick(p, match.teamAway) }
            ) {
                PlayerJerseyPin(player = p, homeColor = false)
            }
        }
    }
}

@Composable
fun PlayerJerseyPin(player: Player, homeColor: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (homeColor) NeonPink else CyberCyan)
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = player.number.toString(),
                color = if (homeColor) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .background(MatteBlack.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.name.split(" ").last(),
                color = Color.White,
                fontSize = 8.sp,
                maxLines = 1
            )
        }
    }
}

// Custom alignment modifier to position player nodes on our soccer field relative coordinates
fun Modifier.alignPlayer(x: Double, y: Double): Modifier {
    return this.then(
        object : androidx.compose.ui.layout.ParentDataModifier {
            override fun androidx.compose.ui.unit.Density.modifyParentData(parentData: Any?): Any {
                return Alignment.TopStart
            }
        }
    ).layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            val posX = ((x / 100.0) * constraints.maxWidth) - (placeable.width / 2)
            val posY = ((y / 100.0) * constraints.maxHeight) - (placeable.height / 2)
            placeable.placeRelative(posX.toInt(), posY.toInt())
        }
    }
}

@Composable
fun PlayerStatsDialog(player: Player, teamName: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            border = BorderStroke(1.dp, NeonPink),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚽ SQUAD ROSTER CARD",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(NeonPink.copy(alpha = 0.1f))
                        .border(2.dp, NeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👕 #${player.number}", fontSize = 18.sp, color = NeonPink)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "${player.position} • $teamName",
                    color = SoftGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PlayerStatBox(value = player.rating.toString(), label = "Rating")
                    PlayerStatBox(value = if (player.position in listOf("ST", "CF", "LW", "RW")) "1" else "0", label = "Goals")
                    PlayerStatBox(value = if (player.rating > 7.5) "1" else "0", label = "Assists")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Close Profile", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PlayerStatBox(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(GlassCard, RoundedCornerShape(10.dp))
            .border(1.dp, GlassCardBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
            .width(60.dp)
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = SoftGray
        )
    }
}
