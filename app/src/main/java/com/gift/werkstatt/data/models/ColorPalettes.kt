package com.gift.werkstatt.data.models

object ColorPalettes {

    val Bold = listOf(
        0xFFE53935L, // Red
        0xFF1E88E5L, // Blue
        0xFFFDD835L, // Yellow
        0xFF43A047L, // Green
        0xFFFB8C00L, // Orange
        0xFF8E24AAL, // Purple
        0xFF1A1A1AL, // Black
        0xFFFFFFFFL  // White
    )

    val Pastel = listOf(
        0xFFF8BBD9L, // Pink
        0xFFE1BEE7L, // Lavender
        0xFFB2DFDBL, // Mint
        0xFFFFE0B2L, // Peach
        0xFFBBDEFBL, // Baby blue
        0xFFFFF8E1L, // Cream
        0xFFD7CCC8L, // Taupe
        0xFFC8E6C9L  // Sage
    )

    val Earth = listOf(
        0xFF6D4C41L, // Brown
        0xFFD84315L, // Terracotta
        0xFF827717L, // Olive
        0xFFF9A825L, // Ochre
        0xFFBF360CL, // Rust
        0xFFD7CCC8L, // Sand
        0xFF4E342EL, // Dark brown
        0xFF8D6E63L  // Sienna
    )

    val Neon = listOf(
        0xFFFF1744L, // Hot pink
        0xFF00E5FFL, // Electric blue
        0xFF76FF03L, // Lime
        0xFFFF9100L, // Bright orange
        0xFFD500F9L, // Magenta
        0xFFFFEA00L, // Neon yellow
        0xFF00E676L, // Neon green
        0xFF651FFFL  // Electric purple
    )

    val Skin = listOf(
        0xFFFFDBACL, // Light
        0xFFF1C27DL, // Fair
        0xFFE0AC69L, // Medium light
        0xFFC68642L, // Medium
        0xFF8D5524L, // Tan
        0xFF6B4423L, // Brown
        0xFF4A2912L, // Dark brown
        0xFF2D1810L  // Deep
    )

    val Vintage = listOf(
        0xFFD4A03BL, // Mustard
        0xFFB87D7DL, // Dusty rose
        0xFF87A878L, // Sage
        0xFF8B3A3AL, // Burgundy
        0xFF2C3E50L, // Navy
        0xFFA08887L, // Mauve
        0xFF6B5344L, // Sepia
        0xFF4A6741L  // Forest
    )

    enum class PaletteType(val displayName: String) {
        BOLD("Bold"),
        PASTEL("Pastel"),
        EARTH("Earth"),
        NEON("Neon"),
        SKIN("Skin"),
        VINTAGE("Vintage")
    }

    fun getPalette(type: PaletteType): List<Long> = when (type) {
        PaletteType.BOLD -> Bold
        PaletteType.PASTEL -> Pastel
        PaletteType.EARTH -> Earth
        PaletteType.NEON -> Neon
        PaletteType.SKIN -> Skin
        PaletteType.VINTAGE -> Vintage
    }
}
