package com.jozilla.worldcup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jozilla.worldcup.model.Match
import com.jozilla.worldcup.ui.theme.*
import com.jozilla.worldcup.viewmodel.WorldCupViewModel

@Composable
fun KnockoutScreen(viewModel: WorldCupViewModel = viewModel()) {
    val matches by viewModel.matches.collectAsState()
    var selectedRoundTab by remember { mutableStateOf(0) }
    val tabs = listOf("Round of 16", "Quarterfinals", "Semifinals", "Grand Final")

    // Filter matches corresponding to knockout stages
    val filteredKnockouts = remember(matches, selectedRoundTab) {
        matches.filter { match ->
            when (selectedRoundTab) {
                0 -> match.id.contains("r16")
                1 -> match.id.contains("quarter")
                2 -> match.id.contains("semi")
                else -> match.id.contains("final")
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab row for Bracket Stages
        ScrollableTabRow(
            selectedTabIndex = selectedRoundTab,
            containerColor = MatteBlack,
            contentColor = NeonPink,
            edgePadding = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedRoundTab == index,
                    onClick = { selectedRoundTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            color = if (selectedRoundTab == index) NeonPink else SoftGray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredKnockouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏆 Brackets Pending Group End",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Once groups finish, qualifying teams are automatically placed here.",
                        color = SoftGray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredKnockouts, key = { it.id }) { match ->
                    var expanded by remember { mutableStateOf(false) }
                    MatchCard(
                        match = match,
                        expanded = expanded,
                        onExpandToggle = { expanded = !expanded },
                        activeTactical = false,
                        onTacticalToggle = {},
                        onPlayerClick = { _, _ -> }
                    )
                }
            }
        }
    }
}
