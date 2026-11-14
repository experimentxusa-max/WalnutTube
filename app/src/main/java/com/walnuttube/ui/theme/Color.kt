package com.walnuttube.ui.theme

import androidx.compose.ui.graphics.Color

// Primary colors
val WalnutPrimary = Color(0xFF6200EE)
val WalnutPrimaryVariant = Color(0xFF3700B3)
val WalnutSecondary = Color(0xFF03DAC6)
val WalnutSecondaryVariant = Color(0xFF018786)
val WalnutBackground = Color(0xFFFFFFFF)
val WalnutSurface = Color(0xFFFFFFFF)
val WalnutError = Color(0xFFB00020)

// Dark theme colors
val WalnutPrimaryDark = Color(0xFFBB86FC)
val WalnutPrimaryVariantDark = Color(0xFF3700B3)
val WalnutSecondaryDark = Color(0xFF03DAC6)
val WalnutBackgroundDark = Color(0xFF121212)
val WalnutSurfaceDark = Color(0xFF121212)
val WalnutErrorDark = Color(0xFFCF6679)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = WalnutPrimaryDark,
    primaryContainer = WalnutPrimaryVariantDark,
    secondary = WalnutSecondaryDark,
    background = WalnutBackgroundDark,
    surface = WalnutSurfaceDark,
    error = WalnutErrorDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White,
)

val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = WalnutPrimary,
    primaryContainer = WalnutPrimaryVariant,
    secondary = WalnutSecondary,
    background = WalnutBackground,
    surface = WalnutSurface,
    error = WalnutError,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
)