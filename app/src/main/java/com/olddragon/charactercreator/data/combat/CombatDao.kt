package com.olddragon.charactercreator.data.combat

import androidx.room.*

@Dao
interface CombatDao {
    
    @Insert
    suspend fun insertCombat(combat: CombatEntity): Long
    
    @Insert
    suspend fun insertCombatant(combatant: CombatCombatantEntity)
    
    @Insert
    suspend fun insertAction(action: CombatActionEntity)
    
    @Update
    suspend fun updateCombat(combat: CombatEntity)
    
    @Query("SELECT * FROM combats WHERE id = :id")
    suspend fun getCombatById(id: Long): CombatEntity?
    
    @Query("SELECT * FROM combats WHERE playerCharacterId = :characterId ORDER BY startTime DESC")
    suspend fun getCombatsByCharacter(characterId: Long): List<CombatEntity>
    
    @Query("SELECT * FROM combats ORDER BY startTime DESC LIMIT 10")
    suspend fun getRecentCombats(): List<CombatEntity>
    
    @Query("SELECT * FROM combat_combatants WHERE combatId = :combatId")
    suspend fun getCombatantsByCombatId(combatId: String): List<CombatCombatantEntity>
    
    @Query("SELECT * FROM combat_actions WHERE combatId = :combatId ORDER BY roundNumber, id")
    suspend fun getActionsByCombatId(combatId: String): List<CombatActionEntity>
    
    @Query("DELETE FROM combats WHERE id = :id")
    suspend fun deleteCombat(id: Long)
    
    @Transaction
    @Query("SELECT * FROM combats WHERE id = :id")
    suspend fun getCombatWithDetails(id: Long): CombatWithDetails?
}

data class CombatWithDetails(
    @Embedded val combat: CombatEntity,
    @Relation(
        parentColumn = "combatId",
        entityColumn = "combatId"
    ) val combatants: List<CombatCombatantEntity>,
    @Relation(
        parentColumn = "combatId",
        entityColumn = "combatId"
    ) val actions: List<CombatActionEntity>
)
