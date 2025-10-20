package com.olddragon.charactercreator.model.classes

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.CharacterClass

class Thief : CharacterClass {
    override val name: String = "Ladrão"
    override val allowedWeapons: List<String> = listOf("Armas leves e médias")
    override val allowedArmors: List<String> = listOf("Armaduras leves (couro)")
    override val magicItemsRestriction: String = "Pode usar itens mágicos que não sejam restritos a outras classes."

    override fun getLevelAbilities(level: Int): List<Ability> {
        val abilities = mutableListOf<Ability>()
        when (level) {
            1 -> {
                abilities.add(Ability("Furtividade", "Habilidade de se mover sem ser detectado."))
                abilities.add(Ability("Abrir Fechaduras", "Habilidade de abrir fechaduras complexas."))
                abilities.add(Ability("Desarmar Armadilhas", "Habilidade de desarmar armadilhas."))
            }
            2 -> {
                abilities.add(Ability("Ataque Furtivo", "Causa dano extra quando ataca um inimigo desprevenido."))
            }
            // Adicionar mais habilidades conforme os níveis, se necessário, com base no livro.
        }
        return abilities
    }
}


