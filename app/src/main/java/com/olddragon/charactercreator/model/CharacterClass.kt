package com.olddragon.charactercreator.model

interface CharacterClass {
    val name: String
    val allowedWeapons: List<String>
    val allowedArmors: List<String>
    val magicItemsRestriction: String
    fun getLevelAbilities(level: Int): List<Ability>
}


