package com.olddragon.charactercreator.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.olddragon.charactercreator.controller.CharacterCreationViewModel
import com.olddragon.charactercreator.controller.CombatViewModel
import com.olddragon.charactercreator.model.combat.CombatDifficulty
import com.olddragon.charactercreator.navigation.Screen
import com.olddragon.charactercreator.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatScreen(
    navController: NavController,
    characterId: Long,
    characterViewModel: CharacterCreationViewModel,
    combatViewModel: CombatViewModel = viewModel()
) {
    val characterList by characterViewModel.characterList.collectAsStateWithLifecycle()
    val character = remember(characterList, characterId) {
        characterList.firstOrNull { it.id == characterId }
    }

    val combat by combatViewModel.activeCombat.collectAsStateWithLifecycle()
    val combatLog by combatViewModel.combatLog.collectAsStateWithLifecycle()
    val isProcessing by combatViewModel.isProcessing.collectAsStateWithLifecycle()

    var selectedDifficulty by remember { mutableStateOf(CombatDifficulty.MEDIUM) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll do log
    LaunchedEffect(combatLog.size) {
        if (combatLog.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(combatLog.size - 1)
            }
        }
    }

    // Verificar fim do combate
    LaunchedEffect(combat?.isFinished()) {
        if (combat?.isFinished() == true) {
            showResultDialog = true
        }
    }

    DisposableEffect(Unit) {
        combatViewModel.bindCombatService(navController.context)
        onDispose {
            combatViewModel.unbindCombatService(navController.context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Arena de Combate",
                        fontWeight = FontWeight.Bold,
                        color = BlackBullsGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        combatViewModel.clearCombat()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = BlackBullsWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.CombatHistory.createRoute(characterId))
                    }) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Histórico",
                            tint = BlackBullsGold
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
        if (character == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Personagem não encontrado", color = BlackBullsWhite)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Cria uma variável local para permitir o smart cast
            val currentCombat = combat

            // Status dos combatentes
            if (currentCombat != null) {
                CombatantsStatusSection(currentCombat)
            } else {
                NoCombatSection(
                    character = character,
                    onStartCombat = { showDifficultyDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Log de combate
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Log de Combate",
                            style = MaterialTheme.typography.titleMedium,
                            color = BlackBullsRed,
                            fontWeight = FontWeight.Bold
                        )

                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = BlackBullsGold,
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Divider(color = BlackBullsGray, thickness = 1.dp)

                    if (combatLog.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aguardando início do combate...",
                                color = BlackBullsSilver,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(combatLog) { message ->
                                CombatLogMessage(message)
                            }
                        }
                    }
                }
            }

            // Controles de combate
            if (currentCombat != null && !currentCombat.isFinished()) {
                CombatControls(
                    isProcessing = isProcessing,
                    combat = currentCombat,
                    onAutoCombat = {
                        combatViewModel.runAutoCombat(delayBetweenRounds = 1500L)
                    },
                    onNextRound = {
                        combatViewModel.processNextRound()
                    },
                    onStopCombat = {
                        combatViewModel.clearCombat()
                    }
                )
            }
        }

        // Dialog de seleção de dificuldade
        if (showDifficultyDialog) {
            DifficultySelectionDialog(
                onDismiss = { showDifficultyDialog = false },
                onSelect = { difficulty ->
                    selectedDifficulty = difficulty
                    showDifficultyDialog = false
                    combatViewModel.startNewCombat(character, difficulty)
                }
            )
        }

        // Dialog de resultado - Usamos uma variável local segura para evitar o erro de smart cast
        if (showResultDialog) {
            val finishedCombat = combat
            if (finishedCombat != null) {
                CombatResultDialog(
                    combat = finishedCombat,
                    onDismiss = {
                        showResultDialog = false
                        combatViewModel.clearCombat()
                    },
                    onNewCombat = {
                        showResultDialog = false
                        combatViewModel.clearCombat()
                        showDifficultyDialog = true
                    },
                    onViewHistory = {
                        showResultDialog = false
                        navController.navigate(Screen.CombatHistory.createRoute(characterId))
                    }
                )
            }
        }
    }
}

@Composable
private fun NoCombatSection(
    character: com.olddragon.charactercreator.model.Character,
    onStartCombat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "⚔️",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                character.name,
                style = MaterialTheme.typography.headlineSmall,
                color = BlackBullsGold,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${character.characterClass.name} ${character.race.name}",
                color = BlackBullsSilver,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip("HP", character.hp.toString(), BlackBullsRed)
                StatChip("CA", character.ca.toString(), BlackBullsGold)
                StatChip("BA", "+${character.ba}", SuccessGreen)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartCombat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackBullsRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Combate", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CombatantsStatusSection(combat: com.olddragon.charactercreator.model.combat.Combat) {
    val player = combat.getPlayer()
    val enemies = combat.getEnemies()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Rodada ${combat.currentRound}",
                style = MaterialTheme.typography.titleMedium,
                color = BlackBullsRed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Jogador
            if (player != null) {
                CombatantStatusCard(player, isPlayer = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // VS Indicator
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "⚔️ VS ⚔️",
                    fontSize = 24.sp,
                    color = BlackBullsGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inimigos
            enemies.forEach { enemy ->
                CombatantStatusCard(enemy, isPlayer = false)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CombatantStatusCard(
    combatant: com.olddragon.charactercreator.model.combat.Combatant,
    isPlayer: Boolean
) {
    val hpPercentage = (combatant.currentHp.toFloat() / combatant.maxHp).coerceIn(0f, 1f)
    val hpColor = when {
        hpPercentage > 0.6f -> SuccessGreen
        hpPercentage > 0.3f -> AccentOrange
        else -> ErrorRed
    }

    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = if (combatant.isDying) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(if (combatant.isDying) scale else 1f),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayer) BlackBullsGray else BlackBullsDarkGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (isPlayer) "🛡️" else "👹",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            combatant.name,
                            color = if (isPlayer) BlackBullsGold else BlackBullsWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (combatant.isDying) {
                            Text(
                                "💀 AGONIZANDO",
                                color = ErrorRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    "${combatant.currentHp}/${combatant.maxHp}",
                    color = hpColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // HP Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BlackBullsBlack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(hpPercentage)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(hpColor, hpColor.copy(alpha = 0.7f))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge("CA ${combatant.ca}")
                StatBadge("BA +${combatant.bac}")
                StatBadge("${combatant.weaponName}")
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            fontSize = 12.sp,
            color = BlackBullsSilver
        )
    }
}

@Composable
private fun StatBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(BlackBullsBlack)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            fontSize = 11.sp,
            color = BlackBullsSilver
        )
    }
}

@Composable
private fun CombatLogMessage(message: String) {
    val color = when {
        message.contains("CRÍTICO") || message.contains("💥") -> BlackBullsRed
        message.contains("ACERTOU") || message.contains("✓") -> SuccessGreen
        message.contains("ERROU") || message.contains("✗") -> BlackBullsSilver
        message.contains("RODADA") || message.contains("⚔️") -> BlackBullsGold
        message.contains("FINALIZADO") || message.contains("🏆") -> BlackBullsGold
        message.contains("MORRENDO") || message.contains("💀") -> ErrorRed
        else -> BlackBullsWhite
    }

    Text(
        text = message,
        color = color,
        fontSize = 13.sp,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        lineHeight = 18.sp
    )
}

@Composable
private fun CombatControls(
    isProcessing: Boolean,
    combat: com.olddragon.charactercreator.model.combat.Combat?,
    onAutoCombat: () -> Unit,
    onNextRound: () -> Unit,
    onStopCombat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAutoCombat,
                enabled = !isProcessing && combat != null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackBullsRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Auto")
            }

            Button(
                onClick = onNextRound,
                enabled = !isProcessing && combat != null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackBullsGold
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Próxima")
            }

            IconButton(
                onClick = onStopCombat,
                enabled = !isProcessing
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = "Parar",
                    tint = ErrorRed
                )
            }
        }
    }
}

@Composable
private fun DifficultySelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (CombatDifficulty) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚔️", fontSize = 48.sp)
                Text(
                    "Escolha a Dificuldade",
                    color = BlackBullsGold,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DifficultyOption(
                    name = "Fácil",
                    description = "Goblin, Esqueleto",
                    icon = "🟢",
                    onClick = { onSelect(CombatDifficulty.EASY) }
                )

                DifficultyOption(
                    name = "Médio",
                    description = "Orc, Bandido, Lobo",
                    icon = "🟡",
                    onClick = { onSelect(CombatDifficulty.MEDIUM) }
                )

                DifficultyOption(
                    name = "Difícil",
                    description = "Gnoll, Zumbi",
                    icon = "🔴",
                    onClick = { onSelect(CombatDifficulty.HARD) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = BlackBullsWhite)
            }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun DifficultyOption(
    name: String,
    description: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackBullsGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    name,
                    color = BlackBullsGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    description,
                    color = BlackBullsSilver,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CombatResultDialog(
    combat: com.olddragon.charactercreator.model.combat.Combat,
    onDismiss: () -> Unit,
    onNewCombat: () -> Unit,
    onViewHistory: () -> Unit
) {
    val player = combat.getPlayer()
    val isVictory = combat.winnerId == player?.id

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (isVictory) "🏆" else "💀",
                    fontSize = 64.sp
                )
                Text(
                    if (isVictory) "VITÓRIA!" else "DERROTA",
                    color = if (isVictory) BlackBullsGold else ErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Rodadas: ${combat.currentRound}",
                    color = BlackBullsWhite,
                    fontSize = 16.sp
                )

                if (player != null) {
                    Text(
                        "HP Final: ${player.currentHp}/${player.maxHp}",
                        color = if (player.currentHp > 0) SuccessGreen else ErrorRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNewCombat,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackBullsRed
                )
            ) {
                Text("Novo Combate")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onViewHistory) {
                    Text("Histórico", color = BlackBullsGold)
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = BlackBullsWhite)
                }
            }
        },
        containerColor = CardBackground
    )
}