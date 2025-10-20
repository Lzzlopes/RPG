package com.olddragon.charactercreator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_classes")
data class CharacterClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val allowedWeapons: String, // Stored as a comma-separated string
    val allowedArmors: String, // Stored as a comma-separated string
    val magicItemsRestriction: String
)


