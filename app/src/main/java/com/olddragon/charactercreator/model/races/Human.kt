package com.olddragon.charactercreator.model.races

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.Race

class Human : Race {
    override val name: String = "Humano"
    override val movement: Int = 9
    override val infraVision: Int = 0 // Não possui infravisão
    override val alignment: String = "Qualquer um"
    override val abilities: List<Ability> = listOf(
        Ability("Aprendizado", "Recebe um bônus de 10% sobre toda experiência (XP) recebida."),
        Ability("Adaptabilidade", "Recebe um bônus de +1 em uma única Jogada de Proteção à sua escolha.")
    )
}


