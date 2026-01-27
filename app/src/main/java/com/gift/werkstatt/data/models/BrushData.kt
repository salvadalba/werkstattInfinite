package com.gift.werkstatt.data.models

enum class BrushType {
    PEN,
    FINE_PEN,
    BALLPOINT,
    PENCIL,
    MARKER,
    WATERCOLOR,
    FOUNTAIN_INK,
    BRUSH
}

data class BrushConfig(
    val type: BrushType = BrushType.PEN,
    val size: Float = 8f,
    val color: Long = 0xFF1A1A1A,
    val opacity: Float = 1f
)

object BrushDefaults {
    val PEN = BrushConfig(BrushType.PEN, 8f)
    val FINE_PEN = BrushConfig(BrushType.FINE_PEN, 4f)
    val BALLPOINT = BrushConfig(BrushType.BALLPOINT, 6f)
    val PENCIL = BrushConfig(BrushType.PENCIL, 10f, opacity = 0.9f)
    val MARKER = BrushConfig(BrushType.MARKER, 20f, opacity = 0.85f)
    val WATERCOLOR = BrushConfig(BrushType.WATERCOLOR, 24f, opacity = 0.6f)
    val FOUNTAIN_INK = BrushConfig(BrushType.FOUNTAIN_INK, 8f)
    val BRUSH = BrushConfig(BrushType.BRUSH, 16f)

    fun forType(type: BrushType): BrushConfig = when (type) {
        BrushType.PEN -> PEN
        BrushType.FINE_PEN -> FINE_PEN
        BrushType.BALLPOINT -> BALLPOINT
        BrushType.PENCIL -> PENCIL
        BrushType.MARKER -> MARKER
        BrushType.WATERCOLOR -> WATERCOLOR
        BrushType.FOUNTAIN_INK -> FOUNTAIN_INK
        BrushType.BRUSH -> BRUSH
    }

    val ALL_TYPES = BrushType.values().toList()
}
