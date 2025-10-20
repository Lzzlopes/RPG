package com.olddragon.charactercreator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CharacterDao {
    @Insert
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Insert
    suspend fun insertAttribute(attribute: AttributeEntity)

    @Insert
    suspend fun insertRace(race: RaceEntity): Long

    @Insert
    suspend fun insertCharacterClass(characterClass: CharacterClassEntity): Long

    @Transaction
    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacterWithDetails(characterId: Long): CharacterWithDetails?

    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :characterId")
    suspend fun deleteCharacter(characterId: Long)
}


