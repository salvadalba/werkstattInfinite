package com.gift.werkstatt.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Minimal Palette
val AppBackground = Color(0xFFFFFFFF)
val AppSurface = Color(0xFFFAFAFA)
val AppPrimary = Color(0xFF1A1A1A)
val AppAccent = Color(0xFF66C7B6)
val AppSubtle = Color(0xFFE5E5E5)
val AppError = Color(0xFFEF4444)

// Canvas Colors
val CanvasBackground = Color(0xFFFAFAFA)
val GridColor = Color(0xFF808080).copy(alpha = 0.2f)

// Legacy aliases (for gradual migration)
val BauhausBlue = AppAccent
val BauhausBlack = AppPrimary
val BauhausWhite = AppBackground
val BauhausGray = AppSubtle
val BauhausLightGray = AppSurface
val BauhausDarkGray = Color(0xFF2A2A2A)
val StrokeColor = AppPrimary
