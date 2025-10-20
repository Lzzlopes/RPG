package com.olddragon.charactercreator.model.attributes

interface AttributeGenerationStrategy {
    fun generateAttributes(): Map<String, Int>
}


