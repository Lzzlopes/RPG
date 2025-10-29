package com.olddragon.charactercreator.model.combat

data class Combatant(
    val id: String,
    val name: String,
    val isPlayer: Boolean,
    var currentHp: Int,
    val maxHp: Int,
    val ca: Int, // Classe de Armadura
    val bac: Int, // Base de Ataque Corpo a Corpo
    val bad: Int, // Base de Ataque à Distância
    val jpc: Int, // Jogada de Proteção Constituição
    val jps: Int, // Jogada de Proteção Sabedoria
    val dexterity: Int,
    val wisdom: Int,
    val strength: Int,
    val weaponDamage: String, // Ex: "1d8"
    val weaponName: String,
    val movement: Int,
    var initiative: Int = 0,
    var initiativeSucceeded: Boolean = false,
    var isDead: Boolean = false,
    var isDying: Boolean = false,
    var hasActedThisRound: Boolean = false,
    var currentPosition: Int = 0 // Posição no campo de batalha (metros)
) {
    fun isAlive(): Boolean = !isDead && currentHp > 0
    
    fun canAct(): Boolean = isAlive() && !isDying && !hasActedThisRound
    
    fun takeDamage(damage: Int) {
        currentHp -= damage
        if (currentHp <= 0) {
            isDying = true
        }
    }
    
    fun heal(amount: Int) {
        currentHp = minOf(currentHp + amount, maxHp)
        if (currentHp > 0) {
            isDying = false
        }
    }
    
    fun resetForNewRound() {
        hasActedThisRound = false
    }
    
    fun getInitiativeAttribute(): Int = maxOf(dexterity, wisdom)
}
