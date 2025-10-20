package com.olddragon.charactercreator.model.classes

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.CharacterClass

class Warrior : CharacterClass {
    override val name: String = "Guerreiro"
    override val allowedWeapons: List<String> = listOf("Todas")
    override val allowedArmors: List<String> = listOf("Todas")
    override val magicItemsRestriction: String = "Não pode usar cajados, varinhas e pergaminhos mágicos com exceção dos pergaminhos de proteção."

    override fun getLevelAbilities(level: Int): List<Ability> {
        val abilities = mutableListOf<Ability>()
        when (level) {
            1 -> {
                abilities.add(Ability("Aparar", "Pode sacrificar escudo ou arma para absorver dano."))
                abilities.add(Ability("Maestria em Arma", "Bônus de +1 no dano de uma arma à escolha."))
            }
            3 -> {
                abilities.add(Ability("Maestria em Arma (Evoluída)", "Evolui maestria para uma segunda arma e bônus de +2 no dano de ambas."))
            }
            6 -> {
                abilities.add(Ability("Ataque Extra", "Adquire um segundo ataque, corpo a corpo ou à distância."))
            }
            10 -> {
                abilities.add(Ability("Maestria em Arma (Total)", "Evolui maestria para todas as armas semelhantes às que já possui, bônus de +3 no dano."))
            }
        }
        return abilities
    }
}


