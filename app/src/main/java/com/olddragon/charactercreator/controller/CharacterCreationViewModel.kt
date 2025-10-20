package com.olddragon.charactercreator.controller

import androidx.compose.runtime.mutableStateOf
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.olddragon.charactercreator.data.CharacterDao
import com.olddragon.charactercreator.data.CharacterDatabase
import com.olddragon.charactercreator.data.CharacterEntity
import com.olddragon.charactercreator.data.AttributeEntity
import com.olddragon.charactercreator.data.RaceEntity
import com.olddragon.charactercreator.data.CharacterClassEntity
import kotlinx.coroutines.launch
import com.olddragon.charactercreator.model.Character
import com.olddragon.charactercreator.model.CharacterClass
import com.olddragon.charactercreator.model.Race
import com.olddragon.charactercreator.model.attributes.AdventurerAttributeStrategy
import com.olddragon.charactercreator.model.attributes.AttributeGenerationStrategy
import com.olddragon.charactercreator.model.attributes.ClassicAttributeStrategy
import com.olddragon.charactercreator.model.attributes.HeroicAttributeStrategy
import com.olddragon.charactercreator.model.classes.Cleric
import com.olddragon.charactercreator.model.classes.Thief
import com.olddragon.charactercreator.model.classes.Warrior
import com.olddragon.charactercreator.model.races.Dwarf
import com.olddragon.charactercreator.model.races.Elf
import com.olddragon.charactercreator.model.races.Halfling
import com.olddragon.charactercreator.model.races.Human

class CharacterCreationViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDao: CharacterDao

    init {
        val db = Room.databaseBuilder(
            application,
            CharacterDatabase::class.java,
            "character-database"
        ).build()
        characterDao = db.characterDao()
    }

    // UI State
    val characterName = mutableStateOf("")
    val selectedAttributeStrategy = mutableStateOf<AttributeGenerationStrategy>(ClassicAttributeStrategy())
    val generatedAttributeRolls = mutableStateOf<Map<String, Int>>(emptyMap())
    val assignedAttributes = mutableStateOf<MutableMap<String, Int>>(mutableMapOf(
        "Força" to 0,
        "Destreza" to 0,
        "Constituição" to 0,
        "Inteligência" to 0,
        "Sabedoria" to 0,
        "Carisma" to 0
    ))
    val selectedRace = mutableStateOf<Race>(Human())
    val selectedClass = mutableStateOf<CharacterClass>(Warrior())
    val createdCharacter = mutableStateOf<Character?>(null)

    // Available options
    val attributeStrategies = listOf(
        "Clássica" to ClassicAttributeStrategy(),
        "Heróica" to HeroicAttributeStrategy(),
        "Aventureiro" to AdventurerAttributeStrategy()
    )

    val races = listOf(
        "Humano" to Human(),
        "Elfo" to Elf(),
        "Anão" to Dwarf(),
        "Halfling" to Halfling()
    )

    val classes = listOf(
        "Guerreiro" to Warrior(),
        "Clérigo" to Cleric(),
        "Ladrão" to Thief()
    )

    fun onAttributeStrategySelected(strategy: AttributeGenerationStrategy) {
        selectedAttributeStrategy.value = strategy
        generatedAttributeRolls.value = emptyMap() // Clear previous rolls
        assignedAttributes.value = mutableMapOf(
            "Força" to 0,
            "Destreza" to 0,
            "Constituição" to 0,
            "Inteligência" to 0,
            "Sabedoria" to 0,
            "Carisma" to 0
        ) // Reset assigned attributes
    }

    fun generateAttributes() {
        val generated = selectedAttributeStrategy.value.generateAttributes()
        generatedAttributeRolls.value = generated

        // For Classic strategy, directly assign attributes
        if (selectedAttributeStrategy.value is ClassicAttributeStrategy) {
            assignedAttributes.value = generated.toMutableMap()
        }
    }

    fun assignAttribute(attributeName: String, value: Int) {
        val currentAssigned = assignedAttributes.value.toMutableMap()
        currentAssigned[attributeName] = value
        assignedAttributes.value = currentAssigned
    }

    fun onRaceSelected(race: Race) {
        selectedRace.value = race
    }

    fun onClassSelected(characterClass: CharacterClass) {
        selectedClass.value = characterClass
    }

    fun createCharacter() {
        viewModelScope.launch {
            // Simplified values for HP, CA, BA, JP, Languages, Alignment for demonstration
            val hp = 10
            val ca = 10
            val ba = 1
            val jp = 10
            val movement = selectedRace.value.movement
            val languages = listOf("Comum", selectedRace.value.name).joinToString(",")
            val alignment = selectedRace.value.alignment

            // Insert Race and Class first to get their IDs
            val raceEntity = RaceEntity(
                name = selectedRace.value.name,
                movement = selectedRace.value.movement,
                infraVision = selectedRace.value.infraVision,
                alignment = selectedRace.value.alignment
            )
            val raceId = characterDao.insertRace(raceEntity)

            val classEntity = CharacterClassEntity(
                name = selectedClass.value.name,
                allowedWeapons = selectedClass.value.allowedWeapons.joinToString(","),
                allowedArmors = selectedClass.value.allowedArmors.joinToString(","),
                magicItemsRestriction = selectedClass.value.magicItemsRestriction
            )
            val classId = characterDao.insertCharacterClass(classEntity)

            val characterEntity = CharacterEntity(
                name = characterName.value,
                raceId = raceId,
                classId = classId,
                hp = hp,
                ca = ca,
                ba = ba,
                jp = jp,
                movement = movement,
                languages = languages,
                alignment = alignment
            )
            val characterId = characterDao.insertCharacter(characterEntity)

            assignedAttributes.value.forEach { (attrName, attrValue) ->
                characterDao.insertAttribute(AttributeEntity(characterId = characterId, name = attrName, value = attrValue))
            }

            // Retrieve the created character with details for display
            createdCharacter.value = characterDao.getCharacterWithDetails(characterId)?.let { details ->
                com.olddragon.charactercreator.model.Character(
                    name = details.character.name,
                    attributes = details.attributes.associate { it.name to it.value },
                    race = selectedRace.value, // Use original model object for abilities
                    characterClass = selectedClass.value, // Use original model object for abilities
                    hp = details.character.hp,
                    ca = details.character.ca,
                    ba = details.character.ba,
                    jp = details.character.jp,
                    movement = details.character.movement,
                    languages = details.character.languages.split(","),
                    alignment = details.character.alignment
                )
            }
        }
    }
}

