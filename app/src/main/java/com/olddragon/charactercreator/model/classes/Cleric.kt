package com.olddragon.charactercreator.model.classes

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.CharacterClass

class Cleric : CharacterClass {
    override val name: String = "Clérigo"
    override val allowedWeapons: List<String> = listOf("Armas impactantes")
    override val allowedArmors: List<String> = listOf("Todas")
    override val magicItemsRestriction: String = "Capaz de usar todos os tipos de itens mágicos desde que sejam ordeiros."

    override fun getLevelAbilities(level: Int): List<Ability> {
        val abilities = mutableListOf<Ability>()
        when (level) {
            1 -> {
                abilities.add(Ability("Magias Divinas", "Capaz de lançar magias divinas diariamente."))
                abilities.add(Ability("Afastar Mortos-Vivos", "Uma vez ao dia, pode afastar mortos-vivos a até 18 metros."))
                abilities.add(Ability("Cura Milagrosa", "Pode trocar uma magia memorizada por Curar Ferimentos de 1º círculo."))
            }
            3 -> {
                abilities.add(Ability("Afastar Mortos-Vivos (Evoluído)", "Testes de moral para resistir a Afastar Mortos-Vivos somam +1 ao resultado dos dados."))
            }
            6 -> {
                abilities.add(Ability("Cura Milagrosa (Evoluída)", "Pode trocar uma magia memorizada por Curar Ferimentos de 3º círculo."))
            }
            10 -> {
                abilities.add(Ability("Cura Milagrosa (Total)", "Pode trocar uma magia memorizada por Curar Ferimentos de 5º círculo."))
            }
        }
        return abilities
    }
}


