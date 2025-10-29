package com.olddragon.charactercreator.model.combat

import kotlin.random.Random

class CombatEngine {
    
    /**
     * Rola 1d20
     */
    fun rollD20(): Int = Random.nextInt(1, 21)
    
    /**
     * Rola 1d6
     */
    fun rollD6(): Int = Random.nextInt(1, 7)
    
    /**
     * Rola dados baseado na string (ex: "1d8+2", "2d6", "1d10")
     */
    fun rollDamage(damageString: String): Pair<Int, String> {
        val parts = damageString.replace(" ", "").split("+", "-")
        val dicePartParts = parts[0].split("d")
        
        val numDice = dicePartParts[0].toIntOrNull() ?: 1
        val diceSize = dicePartParts[1].toIntOrNull() ?: 6
        
        var total = 0
        val rolls = mutableListOf<Int>()
        
        repeat(numDice) {
            val roll = Random.nextInt(1, diceSize + 1)
            rolls.add(roll)
            total += roll
        }
        
        // Adicionar modificador se houver
        if (parts.size > 1) {
            val modifier = parts[1].toIntOrNull() ?: 0
            total += if (damageString.contains("-")) -modifier else modifier
        }
        
        val rollDescription = rolls.joinToString("+") + 
            if (parts.size > 1) " ${if (damageString.contains("-")) "-" else "+"}${parts[1]}" else ""
        
        return Pair(maxOf(1, total), rollDescription)
    }
    
    /**
     * Verifica surpresa (1-2 em 1d6)
     */
    fun checkSurprise(stealthyApproach: Boolean = false): Boolean {
        val roll = rollD6()
        return if (stealthyApproach) roll <= 3 else roll <= 2
    }
    
    /**
     * Testa iniciativa
     */
    fun rollInitiative(combatant: Combatant): Boolean {
        val roll = rollD20()
        val target = combatant.getInitiativeAttribute()
        combatant.initiative = roll
        combatant.initiativeSucceeded = roll <= target
        return combatant.initiativeSucceeded
    }
    
    /**
     * Determina ordem de ação baseada na iniciativa
     */
    fun determineActionOrder(combatants: List<Combatant>): List<Combatant> {
        val succeeded = combatants.filter { it.initiativeSucceeded }.sortedByDescending { it.initiative }
        val failed = combatants.filter { !it.initiativeSucceeded }.sortedByDescending { it.initiative }
        
        return succeeded + failed
    }
    
    /**
     * Realiza um ataque
     */
    fun performAttack(
        attacker: Combatant,
        target: Combatant,
        isMeleeAttack: Boolean = true
    ): CombatAction.Attack {
        val attackRoll = rollD20()
        val ba = if (isMeleeAttack) attacker.bac else attacker.bad
        val totalAttack = attackRoll + ba
        
        val isCritical = attackRoll == 20
        val isFumble = attackRoll == 1
        val isHit = isCritical || (totalAttack >= target.ca && !isFumble)
        
        var damage = 0
        var damageRollStr = ""
        
        if (isHit) {
            val (rawDamage, rollStr) = rollDamage(attacker.weaponDamage)
            val strengthMod = if (isMeleeAttack) calculateModifier(attacker.strength) else 0
            
            damage = if (isCritical) {
                // Crítico: dobra o dano do dado, adiciona modificador depois
                (rawDamage * 2) + strengthMod
            } else {
                rawDamage + strengthMod
            }
            
            damage = maxOf(1, damage) // Dano mínimo é 1
            damageRollStr = if (isCritical) "($rollStr)×2+$strengthMod" else "$rollStr+$strengthMod"
            
            target.takeDamage(damage)
        }
        
        return CombatAction.Attack(
            attackerId = attacker.id,
            targetId = target.id,
            attackRoll = attackRoll,
            totalAttack = totalAttack,
            targetCA = target.ca,
            isHit = isHit,
            isCritical = isCritical,
            isFumble = isFumble,
            damage = damage,
            damageRoll = damageRollStr
        )
    }
    
    /**
     * Calcula modificador de atributo
     */
    private fun calculateModifier(attribute: Int): Int {
        return when {
            attribute <= 3 -> -3
            attribute <= 5 -> -2
            attribute <= 8 -> -1
            attribute <= 12 -> 0
            attribute <= 15 -> 1
            attribute <= 17 -> 2
            else -> 3
        }
    }
    
    /**
     * Realiza movimento
     */
    fun performMovement(combatant: Combatant, distance: Int): CombatAction.Movement {
        val oldPosition = combatant.currentPosition
        combatant.currentPosition += distance
        
        return CombatAction.Movement(
            combatantId = combatant.id,
            fromPosition = oldPosition,
            toPosition = combatant.currentPosition,
            distance = kotlin.math.abs(distance)
        )
    }
    
    /**
     * Teste de agonizar
     */
    fun performAgonizeTest(combatant: Combatant): CombatAction.AgonizeTest {
        val roll = rollD20()
        val target = maxOf(combatant.jpc, combatant.jps)
        val succeeded = roll <= target
        
        if (!succeeded) {
            combatant.isDead = true
        } else {
            // Sucesso: estabiliza mas continua inconsciente
            combatant.isDying = false
        }
        
        return CombatAction.AgonizeTest(
            combatantId = combatant.id,
            roll = roll,
            target = target,
            succeeded = succeeded
        )
    }
    
    /**
     * Teste de moral
     */
    fun performMoraleTest(team: List<Combatant>): CombatAction.MoraleTest {
        val roll = rollD20()
        // Moral passa se roll <= 10 (simplified)
        val succeeded = roll <= 10
        
        return CombatAction.MoraleTest(
            team = if (team.firstOrNull()?.isPlayer == true) "Players" else "Enemies",
            roll = roll,
            succeeded = succeeded
        )
    }
    
    /**
     * Verifica se o combate terminou
     */
    fun checkCombatEnd(combat: Combat): Boolean {
        val aliveAllies = combat.getAliveAllies()
        val aliveEnemies = combat.getAliveEnemies()
        
        if (aliveAllies.isEmpty()) {
            combat.winnerId = aliveEnemies.firstOrNull()?.id
            combat.state = CombatState.FINISHED
            combat.endTime = System.currentTimeMillis()
            return true
        }
        
        if (aliveEnemies.isEmpty()) {
            combat.winnerId = aliveAllies.firstOrNull()?.id
            combat.state = CombatState.FINISHED
            combat.endTime = System.currentTimeMillis()
            return true
        }
        
        return false
    }
    
    /**
     * IA simples para inimigos
     */
    fun decideEnemyAction(enemy: Combatant, allies: List<Combatant>, enemies: List<Combatant>): String {
        // IA simples: sempre ataca o alvo com menos HP que estiver vivo
        val target = allies.filter { it.isAlive() }.minByOrNull { it.currentHp }
        return target?.id ?: ""
    }
}
