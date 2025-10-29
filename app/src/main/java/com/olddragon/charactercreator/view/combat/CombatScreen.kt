package com.olddragon.charactercreator.view.combat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.olddragon.charactercreator.controller.CombatViewModel
import com.olddragon.charactercreator.model.combat.*
import com.olddragon.charactercreator.navigation.Screen
import com.olddragon.charactercreator.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatScreen(
    navController: NavController,
    characterId: Long,
    viewModel: CombatViewModel,
    characterViewModel: com.olddragon.charactercreator.controller.CharacterCreationViewModel
) {
    val combat by viewModel.activeCombat.collectAsStateWithLifecycle()
    val log by viewModel.combatLog.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    
    var selectedDifficulty by remember { mutableStateOf(CombatDifficulty.MEDIUM) }
    var showDifficultyDialog by remember { mutableStateOf(true) }
    var combatStarted by remember { mutableStateOf(false) }
    
    val characters by characterViewModel.characterList.collectAsStateWithLifecycle()
    val selectedCharacter = remember(characters, characterId) {
        characters.firstOrNull { it.id == characterId }
    }
    
    LaunchedEffect(Unit) {
        characterViewModel.loadAllCharacters()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCombat()
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
                        viewModel.clearCombat()
                        navController.popBackStack() 
                    }) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (combat == null || !combatStarted) {
                // Tela de preparação
                PreCombatScreen(
                    character = selectedCharacter,
                    selectedDifficulty = selectedDifficulty,
                    onDifficultyChange = { selectedDifficulty = it },
                    onStartCombat = {
                        if (selectedCharacter != null) {
                            combatStarted = true
                            viewModel.startNewCombat(selectedCharacter, selectedDifficulty)
                            // Delay de 1 segundo antes de iniciar auto combate
                            kotlinx.coroutines.GlobalScope.launch {
                                kotlinx.coroutines.delay(1000)
                                viewModel.runAutoCombat(delayBetweenRounds = 2000L)
                            }
                        }
                    }
                )
            } else {
                // Tela de combate ativo
                ActiveCombatScreen(
                    combat = combat!!,
                    log = log,
                    isProcessing = isProcessing,
                    onNextRound = { viewModel.processNextRound() },
                    onReturnHome = {
                        viewModel.clearCombat()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PreCombatScreen(
    character: com.olddragon.charactercreator.model.Character?,
    selectedDifficulty: CombatDifficulty,
    onDifficultyChange: (CombatDifficulty) -> Unit,
    onStartCombat: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Character info
        if (character != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚔️",
                        fontSize = 60.sp,
                        modifier = Modifier.scale(scale)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = BlackBullsGold,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${character.characterClass.name} ${character.race.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackBullsSilver
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBox("HP", character.hp.toString(), BlackBullsRed)
                        StatBox("CA", character.ca.toString(), BlackBullsGold)
                        StatBox("BA", "+${character.ba}", SuccessGreen)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Difficulty selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Escolha a Dificuldade",
                    style = MaterialTheme.typography.titleLarge,
                    color = BlackBullsGold,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DifficultyButton(
                    text = "Fácil",
                    description = "Inimigos fracos (Goblins)",
                    emoji = "😊",
                    isSelected = selectedDifficulty == CombatDifficulty.EASY,
                    onClick = { onDifficultyChange(CombatDifficulty.EASY) },
                    color = SuccessGreen
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                DifficultyButton(
                    text = "Médio",
                    description = "Desafio equilibrado (Orcs, Bandidos)",
                    emoji = "😐",
                    isSelected = selectedDifficulty == CombatDifficulty.MEDIUM,
                    onClick = { onDifficultyChange(CombatDifficulty.MEDIUM) },
                    color = BlackBullsGold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                DifficultyButton(
                    text = "Difícil",
                    description = "Inimigos poderosos (Gnolls, Zumbis)",
                    emoji = "😈",
                    isSelected = selectedDifficulty == CombatDifficulty.HARD,
                    onClick = { onDifficultyChange(CombatDifficulty.HARD) },
                    color = BlackBullsRed
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Start button
        Button(
            onClick = onStartCombat,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlackBullsRed
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = character != null
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "INICIAR COMBATE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = BlackBullsSilver
        )
    }
}

@Composable
private fun DifficultyButton(
    text: String,
    description: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else BlackBullsDarkGray
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) color else BlackBullsWhite
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = BlackBullsSilver
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ActiveCombatScreen(
    combat: Combat,
    log: List<String>,
    isProcessing: Boolean,
    onNextRound: () -> Unit,
    onReturnHome: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto scroll to bottom when log updates
    LaunchedEffect(log.size) {
        if (log.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(log.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Combatants status
        CombatantsDisplay(combat)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Combat log
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(BlackBullsRed, BlackBullsRed.copy(alpha = 0.7f))
                            )
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = BlackBullsWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Log de Combate",
                        color = BlackBullsWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BlackBullsWhite,
                            strokeWidth = 2.dp
                        )
                    }
                }
                
                // Log content
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(log) { message ->
                        CombatLogMessage(message)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Controls
        if (combat.isFinished()) {
            CombatFinishedControls(combat, onReturnHome)
        } else {
            CombatActiveControls(isProcessing, onNextRound)
        }
    }
}

@Composable
private fun CombatantsDisplay(combat: Combat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player
        combat.getPlayer()?.let { player ->
            CombatantCard(
                combatant = player,
                isPlayer = true,
                modifier = Modifier.weight(1f)
            )
        }
        
        // VS
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(BlackBullsRed, BlackBullsRed.copy(alpha = 0.5f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "VS",
                color = BlackBullsWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        }
        
        // Enemy
        combat.getAliveEnemies().firstOrNull()?.let { enemy ->
            CombatantCard(
                combatant = enemy,
                isPlayer = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CombatantCard(
    combatant: Combatant,
    isPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    val hpPercentage = combatant.currentHp.toFloat() / combatant.maxHp.toFloat()
    val hpColor = when {
        hpPercentage > 0.6f -> SuccessGreen
        hpPercentage > 0.3f -> BlackBullsGold
        else -> ErrorRed
    }
    
    val animatedHp by animateFloatAsState(
        targetValue = hpPercentage,
        animationSpec = tween(durationMillis = 500),
        label = "hp"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (combatant.isDead) BlackBullsDarkGray.copy(alpha = 0.5f) else CardBackground
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isPlayer) 
            androidx.compose.foundation.BorderStroke(2.dp, BlackBullsGold) 
        else 
            androidx.compose.foundation.BorderStroke(2.dp, BlackBullsRed)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isPlayer) "🛡️" else "⚔️",
                fontSize = 32.sp
            )
            
            Text(
                text = combatant.name,
                fontWeight = FontWeight.Bold,
                color = if (isPlayer) BlackBullsGold else BlackBullsRed,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // HP Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "HP",
                        fontSize = 10.sp,
                        color = BlackBullsSilver
                    )
                    Text(
                        "${combatant.currentHp}/${combatant.maxHp}",
                        fontSize = 10.sp,
                        color = hpColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BlackBullsGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedHp.coerceAtLeast(0f))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(hpColor, hpColor.copy(alpha = 0.7f))
                                )
                            )
                    )
                }
            }
            
            if (combatant.isDying) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "💀 MORRENDO",
                    fontSize = 10.sp,
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (combatant.isDead) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "☠️ MORTO",
                    fontSize = 10.sp,
                    color = BlackBullsSilver,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CombatLogMessage(message: String) {
    val (icon, color) = when {
        message.contains("CRÍTICO") -> "💥" to BlackBullsRed
        message.contains("ACERTOU") -> "✓" to SuccessGreen
        message.contains("ERROU") -> "✗" to BlackBullsSilver
        message.contains("MORREU") || message.contains("MORRENDO") -> "💀" to ErrorRed
        message.contains("Rodada") || message.contains("RODADA") -> "⚔️" to BlackBullsGold
        message.contains("FINALIZADO") || message.contains("Vencedor") -> "🏆" to BlackBullsGold
        message.contains("Iniciativa") -> "🎲" to AccentPurple
        message.contains("Move-se") -> "🏃" to AccentOrange
        message.startsWith("═") -> "" to BlackBullsGray
        else -> "" to BlackBullsWhite
    }
    
    Row(verticalAlignment = Alignment.Top) {
        if (icon.isNotEmpty()) {
            Text(
                text = icon,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = message,
            color = color,
            fontSize = 13.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun CombatFinishedControls(combat: Combat, onReturnHome: () -> Unit) {
    val winner = combat.combatants.firstOrNull { it.id == combat.winnerId }
    val playerWon = winner?.isPlayer == true
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (playerWon) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (playerWon) "🏆" else "💀",
                fontSize = 64.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (playerWon) "VITÓRIA!" else "DERROTA",
                style = MaterialTheme.typography.headlineLarge,
                color = if (playerWon) SuccessGreen else ErrorRed,
                fontWeight = FontWeight.ExtraBold
            )
            
            Text(
                text = "Vencedor: ${winner?.name ?: "Desconhecido"}",
                color = BlackBullsSilver,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackBullsRed
                )
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voltar ao Início")
            }
        }
    }
}

@Composable
private fun CombatActiveControls(isProcessing: Boolean, onNextRound: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onNextRound,
            enabled = !isProcessing,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlackBullsRed
            )
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = BlackBullsWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Próxima Rodada")
        }
    }
}
