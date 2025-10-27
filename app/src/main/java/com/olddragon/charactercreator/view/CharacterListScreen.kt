package com.olddragon.charactercreator.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.olddragon.charactercreator.controller.CharacterCreationViewModel
import com.olddragon.charactercreator.model.Character
import com.olddragon.charactercreator.navigation.Screen
import com.olddragon.charactercreator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    navController: NavController,
    viewModel: CharacterCreationViewModel
) {
    val characterList by viewModel.characterList.collectAsStateWithLifecycle()
    var characterToDelete by remember { mutableStateOf<Character?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAllCharacters()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meus Personagens",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.resetCreationForm()
                    navController.navigate(Screen.CreateCharacter.route) 
                },
                containerColor = BlackBullsRed,
                contentColor = BlackBullsWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Personagem")
            }
        },
        containerColor = BlackBullsDarkGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (characterList.isEmpty()) {
                EmptyCharacterList(
                    onCreateClick = { 
                        viewModel.resetCreationForm()
                        navController.navigate(Screen.CreateCharacter.route) 
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(characterList) { character ->
                        CharacterCard(
                            character = character,
                            onClick = { /* Navigate to detail */ },
                            onDeleteClick = { characterToDelete = character }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    characterToDelete?.let { character ->
        AlertDialog(
            onDismissRequest = { characterToDelete = null },
            title = {
                Text(
                    "Deletar Personagem",
                    color = BlackBullsGold,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Tem certeza que deseja deletar ${character.name}? Esta ação não pode ser desfeita.",
                    color = BlackBullsWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCharacter(character.id)
                        characterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    )
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { characterToDelete = null }
                ) {
                    Text("Cancelar", color = BlackBullsWhite)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
private fun EmptyCharacterList(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🐂",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Nenhum Personagem",
            style = MaterialTheme.typography.headlineMedium,
            color = BlackBullsGold,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Crie seu primeiro personagem e comece sua jornada!",
            style = MaterialTheme.typography.bodyLarge,
            color = BlackBullsSilver,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlackBullsRed
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Criar Personagem", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
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
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = BlackBullsGold,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClassBadge(character.characterClass.name)
                        RaceBadge(character.race.name)
                    }
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = ErrorRed
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = BlackBullsGray, thickness = 1.dp)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("HP", character.hp.toString(), BlackBullsRed)
                StatItem("CA", character.ca.toString(), BlackBullsGold)
                StatItem("BA", "+${character.ba}", SuccessGreen)
                StatItem("MV", "${character.movement}m", AccentPurple)
            }
        }
    }
}

@Composable
private fun ClassBadge(className: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BlackBullsRed, BlackBullsRed.copy(alpha = 0.7f))
                )
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = className,
            color = BlackBullsWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RaceBadge(raceName: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BlackBullsGray)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = raceName,
            color = BlackBullsSilver,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
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
