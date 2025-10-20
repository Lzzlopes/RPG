package com.olddragon.charactercreator.model.races

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.Race

class Halfling : Race {
    override val name: String = "Halfling"
    override val movement: Int = 6
    override val infraVision: Int = 0 // Não possui infravisão
    override val alignment: String = "Neutro"
    override val abilities: List<Ability> = listOf(
        Ability("Furtivos", "Pode se esconder com uma chance de 1-2 em 1d6. Se Ladrão, +1 no talento Furtividade."),
        Ability("Destemidos", "Recebe um bônus de +1 em qualquer teste de JPS."),
        Ability("Bons de Mira", "Qualquer ataque à distância com arma de arremesso é considerado um ataque fácil."),
        Ability("Pequenos", "Ataques de criaturas grandes ou maiores são difíceis de acertar um halfling."),
        Ability("Restrições", "Só pode usar armaduras de couro e armas pequenas ou médias. Arma média é usada como duas mãos.")
    )
}


