package com.olddragon.charactercreator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.olddragon.charactercreator.data.combat.*

@Database(
    entities = [
        CharacterEntity::class,
        AttributeEntity::class,
        RaceEntity::class,
        CharacterClassEntity::class,
        AbilityEntity::class,
        CombatEntity::class,
        CombatCombatantEntity::class,
        CombatActionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun combatDao(): CombatDao
}
