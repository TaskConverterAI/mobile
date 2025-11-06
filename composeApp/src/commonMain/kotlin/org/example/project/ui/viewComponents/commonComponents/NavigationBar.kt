package org.example.project.ui.viewComponents.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

import org.example.project.data.commonData.Destination
import org.example.project.ui.theme.DarkGray

class BottomAppBarBulgeShape(private val radiusPx: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0F, 0F)
            lineTo(size.width / 2 - radiusPx, 0F)
            cubicTo(
                size.width / 2 - radiusPx / 2F, -radiusPx * 1F,
                size.width / 2 + radiusPx / 2F, -radiusPx * 1F,
                size.width / 2 + radiusPx, 0F
            )

            lineTo(size.width, 0F)
            lineTo(size.width, size.height)
            lineTo(0F, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = BottomAppBarBulgeShape(
                    with(LocalDensity.current) { 42.dp.toPx() }
                )
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val halfSize = Destination.entries.size / 2

        Destination.entries.take(halfSize).forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) {
                            destination.selectedIcon.invoke()
                        } else {
                            destination.commonIcon.invoke()
                        },
                        contentDescription = destination.contentDescription,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            DarkGray
                        }
                    )
                },
                selected = false,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(destination.label,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            DarkGray
                        }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Destination.entries.drop(halfSize).forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) {
                            destination.selectedIcon.invoke()
                        } else {
                            destination.commonIcon.invoke()
                        },
                        contentDescription = destination.contentDescription,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            DarkGray
                        }
                    )
                },
                selected = false,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(destination.label,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            DarkGray
                        }
                    )
                }
            )
        }
    }
}
