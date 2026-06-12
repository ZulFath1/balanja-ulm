package com.example.balanja.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun BalanjaBottomNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(Screen.Home,     Icons.Filled.Home,      Icons.Outlined.Home,      "Beranda"),
        BottomNavItem(Screen.Search,   Icons.Filled.Search,    Icons.Outlined.Search,    "Cari"),
        BottomNavItem(Screen.AddStall, Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline, "Tambah"),
        BottomNavItem(Screen.Profile,  Icons.Filled.Person,    Icons.Outlined.PersonOutline,    "Profil"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
            .height(80.dp)
    ) {
        // The White Background Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 16.dp, 
                    spotColor = Color(0x1A000000), 
                    ambientColor = Color(0x1A000000),
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
        )

        // The Navigation Items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.screen.route
                
                val iconYOffset by animateDpAsState(
                    targetValue = if (selected) (-16).dp else 10.dp,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
                    label = "iconYOffset"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "iconColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon with Animated Circle Background
                        Box(
                            modifier = Modifier
                                .offset(y = iconYOffset)
                                .size(48.dp)
                                .shadow(if (selected) 8.dp else 0.dp, CircleShape)
                                .background(if (selected) androidx.compose.material3.MaterialTheme.colorScheme.surface else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Animated Text
                        AnimatedVisibility(
                            visible = selected,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = item.label,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.offset(y = (-8).dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
