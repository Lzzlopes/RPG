package com.olddragon.charactercreator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "abilities")
data class AbilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long, // Can be raceId or classId
    val ownerType: String, // "race" or "class"
    val name: String,
    val description: String
)


