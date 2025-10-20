package com.olddragon.charactercreator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "races")
data class RaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val movement: Int,
    val infraVision: Int,
    val alignment: String
)


