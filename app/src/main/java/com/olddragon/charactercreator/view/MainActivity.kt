package com.olddragon.charactercreator.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.olddragon.charactercreator.controller.CharacterCreationViewModel
import com.olddragon.charactercreator.controller.CombatViewModel
import com.olddragon.charactercreator.navigation.Screen
import com.olddragon.charactercreator.ui.theme.BlackBullsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlackBullsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val characterViewModel = ViewModelProvider(
                        this,
                        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    ).get(CharacterCreationViewModel::class.java)
                    
                    val combatViewModel = ViewModelProvider(
                        this,
                        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    ).get(CombatViewModel::class.java)
                    
                    MainNavigation(characterViewModel, combatViewModel)
                }
            }
        }
    }
}

@Composable
fun MainNavigation(
    characterViewModel: CharacterCreationViewModel,
    combatViewModel: CombatViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.CharacterList.route) {
            CharacterListScreen(navController, characterViewModel)
        }

        composable(Screen.CreateCharacter.route) {
            CreateCharacterScreen(navController, characterViewModel)
        }
        
        composable(
            route = Screen.Combat.route,
            arguments = listOf(
                navArgument("characterId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getLong("characterId") ?: 0L
            CombatScreen(
                navController = navController,
                characterId = characterId,
                characterViewModel = characterViewModel,
                combatViewModel = combatViewModel
            )
        }
        
        composable(
            route = Screen.CombatHistory.route,
            arguments = listOf(
                navArgument("characterId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getLong("characterId") ?: 0L
            CombatHistoryScreen(
                navController = navController,
                characterId = characterId,
                combatViewModel = combatViewModel
            )
        }
    }
}
