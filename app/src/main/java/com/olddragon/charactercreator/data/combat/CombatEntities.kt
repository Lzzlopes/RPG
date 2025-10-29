package com.olddragon.charactercreator.data.combat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "combats")
data class CombatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val combatId: String,
    val playerCharacterId: Long,
    val state: String,
    val currentRound: Int,
    val playerSurprised: Boolean,
    val enemySurprised: Boolean,
    val winnerId: String?,
    val startTime: Long,
    val endTime: Long?
)

@Entity(tableName = "combat_combatants")
data class CombatCombatantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val combatId: String,
    val combatantId: String,
    val name: String,
    val isPlayer: Boolean,
    val initialHp: Int,
    val finalHp: Int,
    val ca: Int,
    val bac: Int,
    val bad: Int,
    val isDead: Boolean
)

@Entity(tableName = "combat_actions")
data class CombatActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val combatId: String,
    val roundNumber: Int,
    val actionType: String, // "ATTACK", "MOVEMENT", "DEATH", "AGONIZE", "MORALE"
    val actorId: String,
    val targetId: String?,
    val actionData: String // JSON com detalhes
)
