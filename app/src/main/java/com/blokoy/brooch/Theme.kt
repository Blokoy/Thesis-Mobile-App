package com.blokoy.brooch

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BroochColors {
    val Green = Color(0xFF0F6B57)
    val Red = Color(0xFFB02A22)
    val RedSoft = Color(0xFFF4DEDE)
    val RedBorder = Color(0xFFDE8F8F)
    val Background = Color(0xFFE6EAE9)
    val Card = Color(0xFFFFFFFF)
    val CardBorder = Color(0xFFD3D9D7)
    val Ink = Color(0xFF16201E)
    val Muted = Color(0xFF5E6a67)
    val Track = Color(0xFFCDD4D2)
}

@Composable
fun BroochTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BroochColors.Green,
            error = BroochColors.Red,
            background = BroochColors.Background,
            surface = BroochColors.Card,
            onBackground = BroochColors.Ink,
            onSurface = BroochColors.Ink
        ),
        content = content
    )
}
