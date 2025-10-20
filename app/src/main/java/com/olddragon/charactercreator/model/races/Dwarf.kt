package com.olddragon.charactercreator.model.races

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.Race

class Dwarf : Race {
    override val name: String = "Anão"
    override val movement: Int = 6
    override val infraVision: Int = 18
    override val alignment: String = "Ordem"
    override val abilities: List<Ability> = listOf(
        Ability("Mineradores", "Recebe um bônus de +1 em testes de JPC e +1 de dano contra criaturas grandes ou maiores."),
        Ability("Vigoroso", "Imune a venenos e doenças."),
        Ability("Armas Grandes", "Pode usar armas grandes com uma mão."),
        Ability("Inimigos", "Recebe um bônus de +1 para acertar e causar dano contra orcs, goblins e gigantes.")
    )
}


