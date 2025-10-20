package com.olddragon.charactercreator.model.attributes

import com.olddragon.charactercreator.model.DiceRoller

class ClassicAttributeStrategy : AttributeGenerationStrategy {
    override fun generateAttributes(): Map<String, Int> {
        val attributes = mutableMapOf<String, Int>()
        attributes["Força"] = DiceRoller.roll3d6()
        attributes["Destreza"] = DiceRoller.roll3d6()
        attributes["Constituição"] = DiceRoller.roll3d6()
        attributes["Inteligência"] = DiceRoller.roll3d6()
        attributes["Sabedoria"] = DiceRoller.roll3d6()
        attributes["Carisma"] = DiceRoller.roll3d6()
        return attributes
    }
}


