package com.olddragon.charactercreator.model.combat

sealed class CombatAction {
    data class Attack(
        val attackerId: String,
        val targetId: String,
        val attackRoll: Int,
        val totalAttack: Int,
        val targetCA: Int,
        val isHit: Boolean,
        val isCritical: Boolean,
        val isFumble: Boolean,
        val damage: Int = 0,
        val damageRoll: String = ""
    ) : CombatAction()
    
    data class Movement(
        val combatantId: String,
        val fromPosition: Int,
        val toPosition: Int,
        val distance: Int
    ) : CombatAction()
    
    data class Death(
        val combatantId: String,
        val combatantName: String
    ) : CombatAction()
    
    data class AgonizeTest(
        val combatantId: String,
        val roll: Int,
        val target: Int,
        val succeeded: Boolean
    ) : CombatAction()
    
    data class MoraleTest(
        val team: String,
        val roll: Int,
        val succeeded: Boolean
    ) : CombatAction()
}
