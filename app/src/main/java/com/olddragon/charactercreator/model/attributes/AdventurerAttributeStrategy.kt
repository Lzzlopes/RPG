package com.olddragon.charactercreator.model.attributes

import com.olddragon.charactercreator.model.DiceRoller

class AdventurerAttributeStrategy : AttributeGenerationStrategy {
    override fun generateAttributes(): Map<String, Int> {
        val rolls = mutableListOf<Int>()
        repeat(6) {
            rolls.add(DiceRoller.roll3d6())
        }
        // Os atributos serão distribuídos livremente pelo usuário, então retornamos apenas os valores.
        // A lógica de atribuição a Força, Destreza, etc., será feita em um nível superior.
        return mapOf(
            "roll1" to rolls[0],
            "roll2" to rolls[1],
            "roll3" to rolls[2],
            "roll4" to rolls[3],
            "roll5" to rolls[4],
            "roll6" to rolls[5]
        )
    }
}


