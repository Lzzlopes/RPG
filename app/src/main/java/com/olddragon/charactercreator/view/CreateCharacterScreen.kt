package com.olddragon.charactercreator.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.olddragon.charactercreator.controller.CharacterCreationViewModel
import com.olddragon.charactercreator.model.attributes.ClassicAttributeStrategy
import com.olddragon.charactercreator.navigation.Screen
import com.olddragon.charactercreator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCharacterScreen(
    navController: NavController,
    viewModel: CharacterCreationViewModel
) {
    val characterName by viewModel.characterName
    val selectedAttributeStrategy by viewModel.selectedAttributeStrategy
    val generatedAttributeRolls by viewModel.generatedAttributeRolls
    val assignedAttributes by viewModel.assignedAttributes
    val selectedRace by viewModel.selectedRace
    val selectedClass by viewModel.selectedClass
    var currentStep by remember { mutableStateOf(0) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Criar Personagem",
                            fontWeight = FontWeight.Bold,
                            color = BlackBullsGold
                        )
                        Text(
                            getStepTitle(currentStep),
                            fontSize = 12.sp,
                            color = BlackBullsSilver
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 0) {
                            currentStep--
                        } else {
                            viewModel.resetCreationForm()
                            navController.popBackStack()
                        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = (currentStep + 1) / 4f,
                modifier = Modifier.fillMaxWidth(),
                color = BlackBullsRed,
                trackColor = BlackBullsGray
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (currentStep) {
                    0 -> NameStep(viewModel)
                    1 -> AttributesStep(viewModel)
                    2 -> RaceStep(viewModel)
                    3 -> ClassStep(viewModel)
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BlackBullsWhite
                        )
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Anterior")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            currentStep++
                        } else {
                            viewModel.createCharacter { characterId ->
                                showSuccessDialog = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlackBullsRed
                    ),
                    enabled = isStepValid(currentStep, viewModel)
                ) {
                    Text(if (currentStep < 3) "Próximo" else "Criar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        if (currentStep < 3) Icons.Default.ArrowForward else Icons.Default.Check,
                        contentDescription = null
                    )
                }
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✨", fontSize = 48.sp)
                    Text(
                        "Personagem Criado!",
                        color = BlackBullsGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "Seu personagem foi criado com sucesso!",
                    color = BlackBullsWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetCreationForm()
                        navController.navigate(Screen.CharacterList.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlackBullsRed
                    )
                ) {
                    Text("Ver Personagens")
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
private fun NameStep(viewModel: CharacterCreationViewModel) {
    val characterName by viewModel.characterName

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Como deseja chamar seu personagem?",
                style = MaterialTheme.typography.titleLarge,
                color = BlackBullsGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Escolha um nome épico que represente seu herói!",
                style = MaterialTheme.typography.bodyMedium,
                color = BlackBullsSilver
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = characterName,
                onValueChange = { viewModel.characterName.value = it },
                label = { Text("Nome do Personagem") },
                placeholder = { Text("Ex: Asta, Yuno, Noelle...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlackBullsRed,
                    focusedLabelColor = BlackBullsGold,
                    cursorColor = BlackBullsRed,
                    unfocusedTextColor = BlackBullsWhite,
                    focusedTextColor = BlackBullsWhite
                ),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BlackBullsGold)
                }
            )
        }
    }
}

@Composable
private fun AttributesStep(viewModel: CharacterCreationViewModel) {
    val selectedAttributeStrategy by viewModel.selectedAttributeStrategy
    val generatedAttributeRolls by viewModel.generatedAttributeRolls
    val assignedAttributes by viewModel.assignedAttributes

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Gere seus Atributos",
                style = MaterialTheme.typography.titleLarge,
                color = BlackBullsGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Estratégia:",
                style = MaterialTheme.typography.titleMedium,
                color = BlackBullsRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(Modifier.selectableGroup()) {
                viewModel.attributeStrategies.forEach { (name, strategy) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = (selectedAttributeStrategy.javaClass == strategy.javaClass),
                                onClick = { viewModel.onAttributeStrategySelected(strategy) },
                                role = Role.RadioButton
                            )
                            .background(
                                if (selectedAttributeStrategy.javaClass == strategy.javaClass)
                                    BlackBullsGray else BlackBullsDarkGray
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedAttributeStrategy.javaClass == strategy.javaClass),
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BlackBullsRed
                            )
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = BlackBullsWhite,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.generateAttributes() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackBullsRed
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rolar Dados")
            }

            if (generatedAttributeRolls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Seus Atributos:",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlackBullsRed
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedAttributeStrategy is ClassicAttributeStrategy) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        assignedAttributes.forEach { (name, value) ->
                            AttributeDisplay(name, value)
                        }
                    }
                } else {
                    Text(
                        "Valores: ${generatedAttributeRolls.values.joinToString(", ")}",
                        color = BlackBullsGold,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val attributeNames = listOf("Força", "Destreza", "Constituição", "Inteligência", "Sabedoria", "Carisma")
                    attributeNames.forEach { attrName ->
                        var text by remember { mutableStateOf(assignedAttributes[attrName]?.toString()?.takeIf { it != "0" } ?: "") }
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                text = it
                                val value = it.toIntOrNull()
                                if (value != null) {
                                    viewModel.assignAttribute(attrName, value)
                                }
                            },
                            label = { Text(attrName) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlackBullsRed,
                                focusedLabelColor = BlackBullsGold,
                                cursorColor = BlackBullsRed
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AttributeDisplay(name: String, value: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BlackBullsGray, BlackBullsDarkGray)
                )
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = BlackBullsWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value.toString(),
            color = BlackBullsGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RaceStep(viewModel: CharacterCreationViewModel) {
    val selectedRace by viewModel.selectedRace

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Escolha sua Raça",
                style = MaterialTheme.typography.titleLarge,
                color = BlackBullsGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cada raça possui habilidades únicas",
                style = MaterialTheme.typography.bodyMedium,
                color = BlackBullsSilver
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                viewModel.races.forEach { (name, race) ->
                    RaceCard(
                        name = name,
                        race = race,
                        isSelected = selectedRace.name == race.name,
                        onClick = { viewModel.onRaceSelected(race) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceCard(
    name: String,
    race: com.olddragon.charactercreator.model.Race,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BlackBullsGray else BlackBullsDarkGray
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlackBullsRed) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BlackBullsRed
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = if (isSelected) BlackBullsGold else BlackBullsWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MV: ${race.movement}m | Alinhamento: ${race.alignment}",
                    color = BlackBullsSilver,
                    fontSize = 12.sp
                )
            }

            Text(
                text = getRaceEmoji(name),
                fontSize = 32.sp
            )
        }
    }
}

@Composable
private fun ClassStep(viewModel: CharacterCreationViewModel) {
    val selectedClass by viewModel.selectedClass

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Escolha sua Classe",
                style = MaterialTheme.typography.titleLarge,
                color = BlackBullsGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Defina o estilo de combate do seu personagem",
                style = MaterialTheme.typography.bodyMedium,
                color = BlackBullsSilver
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                viewModel.classes.forEach { (name, characterClass) ->
                    ClassCard(
                        name = name,
                        characterClass = characterClass,
                        isSelected = selectedClass.name == characterClass.name,
                        onClick = { viewModel.onClassSelected(characterClass) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassCard(
    name: String,
    characterClass: com.olddragon.charactercreator.model.CharacterClass,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BlackBullsGray else BlackBullsDarkGray
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlackBullsRed) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BlackBullsRed
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = if (isSelected) BlackBullsGold else BlackBullsWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Armas: ${characterClass.allowedWeapons.joinToString(", ")}",
                    color = BlackBullsSilver,
                    fontSize = 12.sp
                )
            }

            Text(
                text = getClassEmoji(name),
                fontSize = 32.sp
            )
        }
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        0 -> "Passo 1: Nome"
        1 -> "Passo 2: Atributos"
        2 -> "Passo 3: Raça"
        3 -> "Passo 4: Classe"
        else -> ""
    }
}

private fun isStepValid(step: Int, viewModel: CharacterCreationViewModel): Boolean {
    return when (step) {
        0 -> viewModel.characterName.value.isNotBlank()
        1 -> viewModel.generatedAttributeRolls.value.isNotEmpty()
        2 -> true
        3 -> true
        else -> false
    }
}

private fun getRaceEmoji(race: String): String {
    return when (race) {
        "Humano" -> "👤"
        "Elfo" -> "🧝"
        "Anão" -> "🪓"
        "Halfling" -> "🌿"
        else -> "❓"
    }
}

private fun getClassEmoji(className: String): String {
    return when (className) {
        "Guerreiro" -> "⚔️"
        "Clérigo" -> "✝️"
        "Ladrão" -> "🗡️"
        else -> "❓"
    }
}
