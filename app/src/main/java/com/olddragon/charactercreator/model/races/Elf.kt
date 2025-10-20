package com.olddragon.charactercreator.model.races

import com.olddragon.charactercreator.model.Ability
import com.olddragon.charactercreator.model.Race

class Elf : Race {
    override val name: String = "Elfo"
    override val movement: Int = 9
    override val infraVision: Int = 18
    override val alignment: String = "Neutro"
    override val abilities: List<Ability> = listOf(
        Ability("Percepção Natural", "Pode detectar portas secretas com 1 em 1d6 (1-2 em 1d6 se procurando) a até 6 metros."),
        Ability("Graciosos", "Recebe um bônus de +1 em qualquer teste de JPD."),
        Ability("Arma Racial", "Recebe um bônus de +1 nos danos causados em ataques à distância com arcos."),
        Ability("Imunidades", "Imune a efeitos ou magias que envolvam sono e paralisia causada por Ghoul.")
    )
}


