package com.olddragon.charactercreator.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olddragon.charactercreator.controller.CharacterCreationViewModel
import com.olddragon.charactercreator.model.attributes.ClassicAttributeStrategy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(viewModel: CharacterCreationViewModel = viewModel()) {
    // Correctly observe State objects from the ViewModel
    val characterName by viewModel.characterName
    val selectedAttributeStrategy by viewModel.selectedAttributeStrategy
    val generatedAttributeRolls by viewModel.generatedAttributeRolls
    val assignedAttributes by viewModel.assignedAttributes
    val selectedRace by viewModel.selectedRace
    val selectedClass by viewModel.selectedClass
    val createdCharacter by viewModel.createdCharacter

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ) {
            Text("Criador de Personagens Old Dragon", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = characterName,
                onValueChange = { viewModel.characterName.value = it },
                label = { Text("Nome do Personagem") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Attribute Generation Strategy
            Text("Estratégia de Atributos:", style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup()) {
                viewModel.attributeStrategies.forEach { (name, strategy) ->
                    Row(Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (selectedAttributeStrategy.javaClass == strategy.javaClass),
                            onClick = { viewModel.onAttributeStrategySelected(strategy) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedAttributeStrategy.javaClass == strategy.javaClass),
                            onClick = null // null recommended for accessibility with selectable
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
            Button(onClick = { viewModel.generateAttributes() }, modifier = Modifier.fillMaxWidth()) {
                Text("Gerar Atributos")
            }
            Spacer(Modifier.height(16.dp))

            // Display and Assign Attributes
            if (generatedAttributeRolls.isNotEmpty()) {
                Text("Atributos Gerados:", style = MaterialTheme.typography.titleMedium)
                if (selectedAttributeStrategy is ClassicAttributeStrategy) {
                    Column {
                        assignedAttributes.forEach { (name, value) ->
                            Text("$name: $value")
                        }
                    }
                } else {
                    // For Heroic and Adventurer, allow assignment
                    Text("Valores para distribuir: ${generatedAttributeRolls.values.joinToString()}")
                    val attributeNames = listOf("Força", "Destreza", "Constituição", "Inteligência", "Sabedoria", "Carisma")
                    attributeNames.forEach { attrName ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$attrName:", modifier = Modifier.width(100.dp))
                            Spacer(Modifier.width(8.dp))
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Race Selection
            Text("Raça:", style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup()) {
                viewModel.races.forEach { (name, race) ->
                    Row(Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (selectedRace.name == race.name),
                            onClick = { viewModel.onRaceSelected(race) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedRace.name == race.name),
                            onClick = null
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // Class Selection
            Text("Classe:", style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup()) {
                viewModel.classes.forEach { (name, characterClass) ->
                    Row(Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (selectedClass.name == characterClass.name),
                            onClick = { viewModel.onClassSelected(characterClass) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedClass.name == characterClass.name),
                            onClick = null
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Button(onClick = { viewModel.createCharacter() }, modifier = Modifier.fillMaxWidth()) {
                Text("Criar Personagem")
            }
            Spacer(Modifier.height(16.dp))

            // Display Created Character
            createdCharacter?.let { character ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ficha do Personagem", style = MaterialTheme.typography.titleLarge)
                        Text("Nome: ${character.name}")
                        Text("Raça: ${character.race.name}")
                        Text("Classe: ${character.characterClass.name}")
                        Text("\nAtributos:")
                        character.attributes.forEach { (name, value) ->
                            Text("  $name: $value")
                        }
                        Text("\nCaracterísticas:")
                        Text("  PV: ${character.hp}")
                        Text("  CA: ${character.ca}")
                        Text("  BA: ${character.ba}")
                        Text("  JP: ${character.jp}")
                        Text("  Movimento: ${character.movement} metros")
                        Text("  Idiomas: ${character.languages.joinToString()}")
                        Text("  Alinhamento: ${character.alignment}")

                        Text("\nHabilidades Raciais:")
                        character.race.abilities.forEach { ability ->
                            Text("  - ${ability.name}: ${ability.description}")
                        }

                        Text("\nHabilidades de Classe (Nível 1):")
                        character.characterClass.getLevelAbilities(1).forEach { ability ->
                            Text("  - ${ability.name}: ${ability.description}")
                        }

                        Text("\nRestrições de Classe:")
                        Text("  Armas Permitidas: ${character.characterClass.allowedWeapons.joinToString()}")
                        Text("  Armaduras Permitidas: ${character.characterClass.allowedArmors.joinToString()}")
                        Text("  Restrição de Itens Mágicos: ${character.characterClass.magicItemsRestriction}")
                    }
                }
            }
        }
    }
}

