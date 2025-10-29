package com.olddragon.charactercreator.model.combat

enum class CombatState {
    NOT_STARTED,
    SURPRISE_CHECK,
    INITIATIVE_ROLL,
    IN_PROGRESS,
    ROUND_END,
    FINISHED
}

data class Combat(
    val id: String,
    val playerCharacterId: Long,
    var state: CombatState = CombatState.NOT_STARTED,
    val combatants: MutableList<Combatant> = mutableListOf(),
    val rounds: MutableList<CombatRound> = mutableListOf(),
    var currentRound: Int = 0,
    var playerSurprised: Boolean = false,
    var enemySurprised: Boolean = false,
    var winnerId: String? = null,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null
) {
    fun isFinished(): Boolean = state == CombatState.FINISHED
    
    fun getPlayer(): Combatant? = combatants.firstOrNull { it.isPlayer }
    
    fun getEnemies(): List<Combatant> = combatants.filter { !it.isPlayer }
    
    fun getAliveCombatants(): List<Combatant> = combatants.filter { it.isAlive() }
    
    fun getAliveEnemies(): List<Combatant> = combatants.filter { !it.isPlayer && it.isAlive() }
    
    fun getAliveAllies(): List<Combatant> = combatants.filter { it.isPlayer && it.isAlive() }
    
    fun addRound(round: CombatRound) {
        rounds.add(round)
        currentRound = rounds.size
    }
    
    fun getCurrentSnapshot(): Map<String, CombatantSnapshot> {
        return combatants.associate { combatant ->
            combatant.id to CombatantSnapshot(
                id = combatant.id,
                name = combatant.name,
                currentHp = combatant.currentHp,
                maxHp = combatant.maxHp,
                isDead = combatant.isDead,
                isDying = combatant.isDying,
                position = combatant.currentPosition
            )
        }
    }
}
