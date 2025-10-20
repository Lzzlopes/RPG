package com.olddragon.charactercreator.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val raceId: Long,
    val classId: Long,
    val hp: Int,
    val ca: Int,
    val ba: Int,
    val jp: Int,
    val movement: Int,
    val languages: String, // Stored as a comma-separated string
    val alignment: String
)

data class CharacterWithDetails(
    @Embedded val character: CharacterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "characterId"
    ) val attributes: List<AttributeEntity>,
    @Relation(
        parentColumn = "raceId",
        entityColumn = "id"
    ) val race: RaceEntity,
    @Relation(
        parentColumn = "classId",
        entityColumn = "id"
    ) val characterClass: CharacterClassEntity
)


