package com.olddragon.charactercreator.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CharacterEntity::class, AttributeEntity::class, RaceEntity::class, CharacterClassEntity::class, AbilityEntity::class], version = 1, exportSchema = false)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}


