package com.jozilla.worldcup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jozilla.worldcup.model.Country
import com.jozilla.worldcup.model.PlayerStats
import com.jozilla.worldcup.ui.theme.*
import com.jozilla.worldcup.viewmodel.WorldCupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(viewModel: WorldCupViewModel = viewModel()) {
    val countries by viewModel.countries.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var selectedCountryForSheet by remember { mutableStateOf<Country?>(null) }

    val filteredCountries = remember(countries, searchInput) {
        if (searchInput.trim().isEmpty()) countries
        else countries.filter { it.name.contains(searchInput, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Search bar
        OutlinedTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            placeholder = { Text(text = "Search Countries...", color = SoftGray) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", tint = SoftGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPink,
                unfocusedBorderColor = GlassCardBorder,
                focusedContainerColor = GlassCard,
                unfocusedContainerColor = GlassCard,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Countries list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCountries, key = { it.name }) { country ->
                CountryItemRow(country = country) {
                    selectedCountryForSheet = country
                }
            }
        }
    }

    // Bottom Sheet for Player Statistics Roster details
    selectedCountryForSheet?.let { country ->
        ModalBottomSheet(
            onDismissRequest = { selectedCountryForSheet = null },
            containerColor = DeepCharcoal,
            dragHandle = { BottomSheetDefaults.DragHandle(color = SoftGray) }
        ) {
            CountrySquadRosterStatsSheet(country = country)
        }
    }
}

@Composable
fun CountryItemRow(country: Country, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = GlassCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = country.flag, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = country.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${country.group} • Rank #${country.rank}",
                        color = SoftGray,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp)
                    )
                }
            }

            // Quick Stats Block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickCountryStat(label = "GP", value = country.played.toString())
                QuickCountryStat(label = "PTS", value = country.points.toString(), highlighted = true)
            }
        }
    }
}

@Composable
fun QuickCountryStat(label: String, value: String, highlighted: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, color = SoftGray)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) NeonPink else Color.White
        )
    }
}

@Composable
fun CountrySquadRosterStatsSheet(country: Country) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Sheet Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = country.flag, fontSize = 36.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${country.name.uppercase()} SQUAD SQUAD STATS",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Detailed cumulative player tracking for FIFA World Cup 2026",
                    color = CyberCyan,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GlassCard, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#", modifier = Modifier.weight(0.3f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "Player", modifier = Modifier.weight(1.3f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "Pos", modifier = Modifier.weight(0.4f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "Min", modifier = Modifier.weight(0.4f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            Text(text = "G⚽", modifier = Modifier.weight(0.4f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            Text(text = "A🅰️", modifier = Modifier.weight(0.4f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            Text(text = "🟨", modifier = Modifier.weight(0.3f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            Text(text = "🟥", modifier = Modifier.weight(0.3f), color = SoftGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (country.roster.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Detailed rosters details pending kickoff log.", color = SoftGray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(country.roster, key = { it.name }) { player ->
                    PlayerStatsTableRow(player = player)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PlayerStatsTableRow(player: PlayerStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, GlassCardBorder.copy(alpha = 0.05f)), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = player.number.toString(), modifier = Modifier.weight(0.3f), color = SoftGray, fontSize = 12.sp)
        Text(text = player.name, modifier = Modifier.weight(1.3f), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(text = player.position, modifier = Modifier.weight(0.4f), color = CyberCyan, fontSize = 11.sp)
        Text(text = player.minutesPlayed.toString(), modifier = Modifier.weight(0.4f), color = Color.White, fontSize = 12.sp, textAlign = TextAlign.End)
        Text(text = player.goals.toString(), modifier = Modifier.weight(0.4f), color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        Text(text = player.assists.toString(), modifier = Modifier.weight(0.4f), color = Color.White, fontSize = 12.sp, textAlign = TextAlign.End)
        Text(text = player.yellowCards.toString(), modifier = Modifier.weight(0.3f), color = CardYellow, fontSize = 12.sp, textAlign = TextAlign.End)
        Text(text = player.redCards.toString(), modifier = Modifier.weight(0.3f), color = CardRed, fontSize = 12.sp, textAlign = TextAlign.End)
    }
}
