package com.olddragon.charactercreator.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.olddragon.charactercreator.navigation.Screen
import com.olddragon.charactercreator.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BlackBullsBlack,
                        BlackBullsDarkGray,
                        BlackBullsBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 60.dp)
            ) {
                Text(
                    text = "⚔️",
                    fontSize = 80.sp,
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "OLD DRAGON",
                    style = MaterialTheme.typography.displayMedium,
                    color = BlackBullsGold,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
                
                Text(
                    text = "Character Creator",
                    style = MaterialTheme.typography.titleLarge,
                    color = BlackBullsRed,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "\"Surpass your limits!\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlackBullsSilver,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            // Menu Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    text = "Criar Personagem",
                    icon = Icons.Default.Add,
                    onClick = { navController.navigate(Screen.CreateCharacter.route) },
                    primaryColor = BlackBullsRed
                )
                
                MenuButton(
                    text = "Meus Personagens",
                    icon = Icons.Default.List,
                    onClick = { navController.navigate(Screen.CharacterList.route) },
                    primaryColor = BlackBullsGold
                )
            }

            // Footer
            Text(
                text = "Black Bulls Squad",
                style = MaterialTheme.typography.bodySmall,
                color = BlackBullsSilver.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    primaryColor: androidx.compose.ui.graphics.Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = primaryColor,
            contentColor = BlackBullsWhite
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
