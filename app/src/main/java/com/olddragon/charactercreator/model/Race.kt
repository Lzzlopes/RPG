package com.olddragon.charactercreator.model

interface Race {
    val name: String
    val movement: Int
    val infraVision: Int
    val alignment: String
    val abilities: List<Ability>
}


