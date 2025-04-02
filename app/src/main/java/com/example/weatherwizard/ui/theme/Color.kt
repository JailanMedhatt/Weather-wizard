package com.example.weatherwizard.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val orange = Color(0xFFFab005)
val DarkPrimary =Color(0xFF134CB5)
val darkSecondary = Color(0xFF5101026).copy(alpha = 0.3f)
val lightSecondary = Color(0xFF104084).copy(alpha = 0.3f)
val lightPrimary= Color(0xFF00A9F6)
val darkGradient = Brush.verticalGradient(colors =
listOf(Color(0xFF08244F),Color(0xFF134CB5),
    Color(0xFF0B42AB)))
val lightGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF00A2EC), // Start color
        Color(0xFF00A2EC), // Midpoint color
        Color(0xFF00A9F6)  // End color
    ),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
)
