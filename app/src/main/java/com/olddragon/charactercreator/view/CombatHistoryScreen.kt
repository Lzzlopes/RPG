package com.olddragon.charactercreator.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.olddragon.charactercreator.controller.CombatViewModel
import com.olddragon.charactercreator.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatHistoryScreen(
    navController: NavController,
    characterId: Long,
    combatViewModel: CombatViewModel
) {
    val recentCombats by combatViewModel.recentCombats.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        combatViewModel.loadRecentCombats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Histórico de Combates",
                        fontWeight = FontWeight.Bold,
                        color = BlackBullsGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = BlackBullsWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlackBullsBlack
                )
            )
        },
        containerColor = BlackBullsDarkGray
    ) { paddingValues ->
        if (recentCombats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("⚔️", fontSize = 64.sp)
                    Text(
                        "Nenhum combate registrado",
                        color = BlackBullsSilver,
                        fontSize = 18.sp
                    )
                    Text(
                        "Inicie um combate para ver o histórico",
                        color = BlackBullsSilver.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentCombats) { combatSummary ->
                    CombatHistoryCard(combatSummary)
                }
            }
        }
    }
}

@Composable
private fun CombatHistoryCard(
    combatSummary: com.olddragon.charactercreator.controller.CombatSummary
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val date = dateFormat.format(Date(combatSummary.date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (combatSummary.playerWon) "🏆" else "💀",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                if (combatSummary.playerWon) "VITÓRIA" else "DERROTA",
                                color = if (combatSummary.playerWon) BlackBullsGold else ErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                date,
                                color = BlackBullsSilver,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } // <-- Fechamento do Row principal (o original estava aqui)

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = BlackBullsGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        combatSummary.playerName,
                        color = BlackBullsWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Jogador",
                        color = BlackBullsSilver,
                        fontSize = 12.sp
                    )
                }

                Text(
                    "⚔️ VS ⚔️",
                    fontSize = 20.sp,
                    color = BlackBullsRed
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        combatSummary.enemyName,
                        color = BlackBullsWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Inimigo",
                        color = BlackBullsSilver,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BlackBullsGray, BlackBullsDarkGray)
                        )
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Rodadas",
                        value = combatSummary.rounds.toString(),
                        icon = "🔄"
                    )
                }
            }
        } // <-- Fechamento da Column dentro do Card
    } // <-- Fechamento do Card
} // <-- Fechamento do CombatHistoryCard

@Composable
fun StatItem(label: String, value: String, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Column {
            Text(
                value,
                color = BlackBullsGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                label,
                color = BlackBullsSilver,
                fontSize = 12.sp
            )
        }
    }
}