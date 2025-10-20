package com.olddragon.attributes

import kotlin.random.Random

object DiceRoller {
    fun roll3d6(): Int {
        return Random.nextInt(1, 7) + Random.nextInt(1, 7) + Random.nextInt(1, 7)
    }

    fun roll4d6DropLowest(): Int {
        val rolls = mutableListOf<Int>()
        repeat(4) {
            rolls.add(Random.nextInt(1, 7))
        }
        rolls.sort()
        return rolls[1] + rolls[2] + rolls[3]
    }
}


