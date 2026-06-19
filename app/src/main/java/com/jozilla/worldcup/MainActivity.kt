package com.jozilla.worldcup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jozilla.worldcup.ui.screens.CountryListScreen
import com.jozilla.worldcup.ui.screens.KnockoutScreen
import com.jozilla.worldcup.ui.screens.MatchesScreen
import com.jozilla.worldcup.ui.screens.MascotScreen
import com.jozilla.worldcup.ui.theme.*
import com.jozilla.worldcup.viewmodel.WorldCupViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorldCupTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WorldCupAppShell()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupAppShell(viewModel: WorldCupViewModel = viewModel()) {
    val currentScreen = viewModel.currentScreen
    val netConnected = viewModel.netStatusConnected
    val netStatusText = viewModel.netStatusText
    val isRefreshing = viewModel.isRefreshing

    // Breathing pulse animation for live network sync
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Rotating refresh icon animation when syncing
    val rotationTransition = rememberInfiniteTransition(label = "rotation")
    val rotationAngle by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FIFA World Cup 2026",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (netConnected) NeonPink.copy(alpha = pulseAlpha)
                                        else Color.Gray
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = netStatusText,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (netConnected) CyberCyan else SoftGray
                            )
                        }
                    }
                },
                actions = {
                    // Cute small Nong Orbit mascot avatar glowing on the top right header bar
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GlassCard)
                            .border(1.5.dp, NeonPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mascot_orbit),
                            contentDescription = "Mascot Orbit avatar",
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MatteBlack,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MatteBlack,
                tonalElevation = 8.dp
            ) {
                NavigationItem(
                    selected = currentScreen == "schedule",
                    onClick = { viewModel.currentScreen = "schedule" },
                    icon = Icons.Default.SportsSoccer,
                    label = "Schedule"
                )
                NavigationItem(
                    selected = currentScreen == "knockouts",
                    onClick = { viewModel.currentScreen = "knockouts" },
                    icon = Icons.Default.EmojiEvents,
                    label = "Knockouts"
                )
                NavigationItem(
                    selected = currentScreen == "countries",
                    onClick = { viewModel.currentScreen = "countries" },
                    icon = Icons.Default.Public,
                    label = "Countries"
                )
                NavigationItem(
                    selected = currentScreen == "mascot",
                    onClick = { viewModel.currentScreen = "mascot" },
                    icon = Icons.Default.Favorite,
                    label = "Mascot"
                )
            }
        },
        floatingActionButton = {
            if (currentScreen == "schedule") {
                FloatingActionButton(
                    onClick = { viewModel.triggerManualRefresh() },
                    containerColor = NeonPink,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync internet scores",
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (isRefreshing) rotationAngle else 0f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MatteBlack, DeepCharcoal)
                    )
                )
        ) {
            when (currentScreen) {
                "schedule" -> MatchesScreen()
                "knockouts" -> KnockoutScreen()
                "countries" -> CountryListScreen()
                "mascot" -> MascotScreen()
            }
        }
    }
}

@Composable
fun RowScope.NavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) NeonPink else SoftGray
            )
        },
        label = {
            Text(
                text = label,
                color = if (selected) Color.White else SoftGray,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = GlassCardBorder
        )
    )
}
