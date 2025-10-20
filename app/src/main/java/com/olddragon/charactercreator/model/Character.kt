package com.olddragon.charactercreator.model

class Character(
    val name: String,
    val attributes: Map<String, Int>,
    val race: Race,
    val characterClass: CharacterClass,
    val hp: Int,
    val ca: Int,
    val ba: Int,
    val jp: Int,
    val movement: Int,
    val languages: List<String>,
    val alignment: String
) {
    fun displayCharacter() {
        println("\n--- Ficha do Personagem ---")
        println("Nome: $name")
        println("Raça: ${race.name}")
        println("Classe: ${characterClass.name}")
        println("\nAtributos:")
        attributes.forEach { (attrName, attrValue) ->
            println("  $attrName: $attrValue")
        }
        println("\nCaracterísticas:")
        println("  Pontos de Vida (PV): $hp")
        println("  Classe de Armadura (CA): $ca")
        println("  Base de Ataque (BA): $ba")
        println("  Jogada de Proteção (JP): $jp")
        println("  Movimento (MV): $movement metros")
        println("  Idiomas: ${languages.joinToString()}")
        println("  Alinhamento: $alignment")

        println("\nHabilidades Raciais:")
        race.abilities.forEach { ability ->
            println("  - ${ability.name}: ${ability.description}")
        }

        println("\nHabilidades de Classe (Nível 1):")
        characterClass.getLevelAbilities(1).forEach { ability ->
            println("  - ${ability.name}: ${ability.description}")
        }

        println("\nRestrições de Classe:")
        println("  Armas Permitidas: ${characterClass.allowedWeapons.joinToString()}")
        println("  Armaduras Permitidas: ${characterClass.allowedArmors.joinToString()}")
        println("  Restrição de Itens Mágicos: ${characterClass.magicItemsRestriction}")
    }
}


