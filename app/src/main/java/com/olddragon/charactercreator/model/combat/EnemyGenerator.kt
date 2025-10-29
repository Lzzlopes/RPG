package com.olddragon.charactercreator.model.combat

import kotlin.random.Random

object EnemyGenerator {
    
    private val enemyTemplates = listOf(
        EnemyTemplate(
            name = "Goblin",
            hpRange = 4..8,
            ca = 12,
            bac = 2,
            bad = 2,
            jpc = 10,
            jps = 8,
            dexterity = 14,
            wisdom = 8,
            strength = 10,
            weaponDamage = "1d6",
            weaponName = "Adaga",
            movement = 9
        ),
        EnemyTemplate(
            name = "Orc",
            hpRange = 8..12,
            ca = 14,
            bac = 3,
            bad = 2,
            jpc = 12,
            jps = 9,
            dexterity = 10,
            wisdom = 9,
            strength = 14,
            weaponDamage = "1d8+1",
            weaponName = "Machado",
            movement = 9
        ),
        EnemyTemplate(
            name = "Esqueleto",
            hpRange = 6..10,
            ca = 13,
            bac = 2,
            bad = 2,
            jpc = 11,
            jps = 10,
            dexterity = 12,
            wisdom = 10,
            strength = 12,
            weaponDamage = "1d6",
            weaponName = "Espada Curta",
            movement = 9
        ),
        EnemyTemplate(
            name = "Bandido",
            hpRange = 7..11,
            ca = 13,
            bac = 3,
            bad = 3,
            jpc = 10,
            jps = 10,
            dexterity = 12,
            wisdom = 10,
            strength = 12,
            weaponDamage = "1d8",
            weaponName = "Espada Longa",
            movement = 9
        ),
        EnemyTemplate(
            name = "Gnoll",
            hpRange = 10..14,
            ca = 15,
            bac = 4,
            bad = 2,
            jpc = 13,
            jps = 10,
            dexterity = 10,
            wisdom = 10,
            strength = 14,
            weaponDamage = "1d10",
            weaponName = "Lança",
            movement = 9
        ),
        EnemyTemplate(
            name = "Lobo",
            hpRange = 8..12,
            ca = 14,
            bac = 3,
            bad = 0,
            jpc = 11,
            jps = 12,
            dexterity = 15,
            wisdom = 12,
            strength = 13,
            weaponDamage = "1d6+1",
            weaponName = "Mordida",
            movement = 15
        ),
        EnemyTemplate(
            name = "Zumbi",
            hpRange = 12..16,
            ca = 12,
            bac = 3,
            bad = 0,
            jpc = 14,
            jps = 8,
            dexterity = 8,
            wisdom = 8,
            strength = 14,
            weaponDamage = "1d8",
            weaponName = "Golpe",
            movement = 6
        )
    )
    
    fun generateRandomEnemy(id: String = "enemy_${Random.nextInt(1000, 9999)}"): Combatant {
        val template = enemyTemplates.random()
        val hp = Random.nextInt(template.hpRange.first, template.hpRange.last + 1)
        
        return Combatant(
            id = id,
            name = template.name,
            isPlayer = false,
            currentHp = hp,
            maxHp = hp,
            ca = template.ca,
            bac = template.bac,
            bad = template.bad,
            jpc = template.jpc,
            jps = template.jps,
            dexterity = template.dexterity,
            wisdom = template.wisdom,
            strength = template.strength,
            weaponDamage = template.weaponDamage,
            weaponName = template.weaponName,
            movement = template.movement,
            currentPosition = 9 // Inimigos começam a 9 metros
        )
    }
    
    fun generateMultipleEnemies(count: Int): List<Combatant> {
        return (1..count).map { index ->
            generateRandomEnemy("enemy_$index")
        }
    }
    
    fun generateEnemyByDifficulty(difficulty: CombatDifficulty): Combatant {
        val filteredTemplates = when (difficulty) {
            CombatDifficulty.EASY -> enemyTemplates.filter { it.hpRange.last <= 10 }
            CombatDifficulty.MEDIUM -> enemyTemplates.filter { it.hpRange.first >= 7 && it.hpRange.last <= 14 }
            CombatDifficulty.HARD -> enemyTemplates.filter { it.hpRange.first >= 10 }
        }
        
        val template = filteredTemplates.random()
        val hp = Random.nextInt(template.hpRange.first, template.hpRange.last + 1)
        
        return Combatant(
            id = "enemy_${Random.nextInt(1000, 9999)}",
            name = template.name,
            isPlayer = false,
            currentHp = hp,
            maxHp = hp,
            ca = template.ca,
            bac = template.bac,
            bad = template.bad,
            jpc = template.jpc,
            jps = template.jps,
            dexterity = template.dexterity,
            wisdom = template.wisdom,
            strength = template.strength,
            weaponDamage = template.weaponDamage,
            weaponName = template.weaponName,
            movement = template.movement,
            currentPosition = 9
        )
    }
}

data class EnemyTemplate(
    val name: String,
    val hpRange: IntRange,
    val ca: Int,
    val bac: Int,
    val bad: Int,
    val jpc: Int,
    val jps: Int,
    val dexterity: Int,
    val wisdom: Int,
    val strength: Int,
    val weaponDamage: String,
    val weaponName: String,
    val movement: Int
)

enum class CombatDifficulty {
    EASY, MEDIUM, HARD
}
