package com.olddragon.charactercreator.model

import kotlin.random.Random

object DiceRoller {
    fun roll3d6(): Int {
        return (1..3).sumOf { Random.nextInt(1, 7) }
    }

    fun roll4d6DropLowest(): Int {
        val rolls = (1..4).map { Random.nextInt(1, 7) }.sorted()
        return rolls.drop(1).sum()
    }
}
