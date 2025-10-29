package com.olddragon.charactercreator.model.combat

data class CombatRound(
    val roundNumber: Int,
    val actions: List<CombatAction> = emptyList(),
    val combatantsState: Map<String, CombatantSnapshot> = emptyMap()
)

data class CombatantSnapshot(
    val id: String,
    val name: String,
    val currentHp: Int,
    val maxHp: Int,
    val isDead: Boolean,
    val isDying: Boolean,
    val position: Int
)
