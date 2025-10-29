package com.olddragon.charactercreator.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CharacterList : Screen("character_list")
    object CreateCharacter : Screen("create_character")
    object EditCharacter : Screen("edit_character/{characterId}") {
        fun createRoute(characterId: Long) = "edit_character/$characterId"
    }
    object CharacterDetail : Screen("character_detail/{characterId}") {
        fun createRoute(characterId: Long) = "character_detail/$characterId"
    }
    object Combat : Screen("combat/{characterId}") {
        fun createRoute(characterId: Long) = "combat/$characterId"
    }
    object CombatHistory : Screen("combat_history/{characterId}") {
        fun createRoute(characterId: Long) = "combat_history/$characterId"
    }
}
