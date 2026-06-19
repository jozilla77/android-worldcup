package com.jozilla.worldcup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jozilla.worldcup.R
import com.jozilla.worldcup.ui.theme.*
import com.jozilla.worldcup.viewmodel.WorldCupViewModel

@Composable
fun MascotScreen(viewModel: WorldCupViewModel = viewModel()) {
    val matches by viewModel.matches.collectAsState()

    // Calculate tournament stats live from synchronized matches
    val totalGoals = remember(matches) { matches.sumOf { it.goals.size } }
    val totalCards = remember(matches) { matches.sumOf { it.cards.size } }
    val completedCount = remember(matches) { matches.count { it.status == "COMPLETED" } }
    val liveCount = remember(matches) { matches.count { it.status == "LIVE" } }

    var bubbleText by remember { mutableStateOf("") }

    // Set initial speech bubble quote dynamically based on live feed status
    LaunchedEffect(matches) {
        if (bubbleText.isEmpty()) {
            bubbleText = when {
                liveCount > 0 -> "💬 \"OMG! There is an active LIVE match right now! Go check out the Schedule and follow the action in real-time! ⚽🔥\""
                completedCount > 0 -> "💬 \"What a fantastic day of matches! Tap on completed games in the Schedule to see goals, minute logs, and cards! 🏆✨\""
                else -> "💬 \"Welcome! I'm Nong Orbit! I will be tracking every goal and card of the 2026 World Cup live from the internet! Let's get started! 💖\""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Mascot Orbit display with soft glowing ring
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPink.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
                .border(2.dp, NeonPink, CircleShape)
                .shadow(4.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mascot_orbit),
                contentDescription = "Mascot Orbit cheering",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Mascot Intro Speech Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = GlassCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NONG ORBIT • HOST MASCOT",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = NeonPink,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "WORLD CUP 2026 LIVE TRACKER GUIDE",
                    fontSize = 10.sp,
                    color = SoftGray,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = bubbleText,
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mascot Cheer Board Interactive Buttons
        Text(
            text = "TAP NONG ORBIT CHEERS",
            fontSize = 10.sp,
            color = SoftGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    bubbleText = "💬 \"OMG! GOOOOOOAL!!! Cheering with all my heart! The crowd is absolutely electric! ⚽💖🎉\""
                },
                colors = ButtonDefaults.buttonColors(containerColor = GlassCardBorder),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(text = "Goal Cheer ⚽", fontSize = 11.sp, color = Color.White)
            }

            Button(
                onClick = {
                    bubbleText = "💬 \"Remember: Keep it clean, play hard, and play fair! No red cards today, please! 🤝⭐\""
                },
                colors = ButtonDefaults.buttonColors(containerColor = GlassCardBorder),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(text = "Fair Play 🤝", fontSize = 11.sp, color = Color.White)
            }

            Button(
                onClick = {
                    bubbleText = "💬 \"Who do you think will lift the golden FIFA World Cup Trophy in 2026? I can't wait! 😍🏆\""
                },
                colors = ButtonDefaults.buttonColors(containerColor = GlassCardBorder),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(text = "Trophy Dream 🏆", fontSize = 11.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Tournament Insights Grid
        Text(
            text = "TOURNAMENT LIVE INSIGHTS",
            fontSize = 10.sp,
            color = SoftGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TournamentStatsGridItem(
                value = totalGoals.toString(),
                label = "Total Goals",
                modifier = Modifier.weight(1f)
            )
            TournamentStatsGridItem(
                value = totalCards.toString(),
                label = "Total Cards",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TournamentStatsGridItem(
                value = completedCount.toString(),
                label = "Completed",
                modifier = Modifier.weight(1f)
            )
            TournamentStatsGridItem(
                value = liveCount.toString(),
                label = "Ongoing Live",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TournamentStatsGridItem(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = GlassCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                fontSize = 8.sp,
                color = SoftGray,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
