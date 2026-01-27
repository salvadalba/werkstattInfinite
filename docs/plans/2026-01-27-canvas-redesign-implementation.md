# Werkstatt Canvas Redesign Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Transform Werkstatt into a modern minimal drawing app with 8 brush types, artist color palette, gallery home screen, and fixed canvas/image interaction.

**Architecture:** Update theme system first, then build new components bottom-up (data models → brush engine → UI components → screens). Fix the critical image overlay bug early. Use Jetpack Compose throughout.

**Tech Stack:** Kotlin, Jetpack Compose, Room Database, Material 3

---

## Phase 1: Theme & Foundation

### Task 1: Update Color Palette

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/ui/theme/Color.kt`

**Step 1: Replace color definitions**

```kotlin
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
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/theme/Color.kt
git commit -m "feat: update color palette to modern minimal with teal accent"
```

---

### Task 2: Add Noto Serif Font

**Files:**
- Create: `app/src/main/res/font/noto_serif_light.ttf` (download from Google Fonts)
- Create: `app/src/main/res/font/noto_serif_semibold.ttf` (download from Google Fonts)
- Modify: `app/src/main/java/com/gift/werkstatt/ui/theme/Type.kt`

**Step 1: Download fonts**

Download Noto Serif from Google Fonts and place:
- `noto_serif_light.ttf` (weight 300)
- `noto_serif_semibold.ttf` (weight 600)

Into `app/src/main/res/font/` directory.

**Step 2: Update Type.kt**

```kotlin
package com.gift.werkstatt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gift.werkstatt.R

val NotoSerifFamily = FontFamily(
    Font(R.font.noto_serif_light, FontWeight.Light),
    Font(R.font.noto_serif_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
```

**Step 3: Commit**

```bash
git add app/src/main/res/font/
git add app/src/main/java/com/gift/werkstatt/ui/theme/Type.kt
git commit -m "feat: add Noto Serif font family"
```

---

### Task 3: Update Theme

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/ui/theme/Theme.kt`

**Step 1: Update theme with new colors**

```kotlin
package com.gift.werkstatt.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AppAccent,
    onPrimary = AppBackground,
    secondary = AppPrimary,
    onSecondary = AppBackground,
    background = AppBackground,
    onBackground = AppPrimary,
    surface = AppSurface,
    onSurface = AppPrimary,
    error = AppError,
    onError = AppBackground,
    outline = AppSubtle
)

@Composable
fun WerkstattTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Legacy alias
@Composable
fun WerkstattInfiniteTheme(content: @Composable () -> Unit) = WerkstattTheme(content)
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/theme/Theme.kt
git commit -m "feat: update theme to modern minimal style"
```

---

## Phase 2: Data Models & Brush System

### Task 4: Create Brush Types

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/data/models/BrushData.kt`

**Step 1: Create brush data models**

```kotlin
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
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/data/models/BrushData.kt
git commit -m "feat: add brush type definitions"
```

---

### Task 5: Update Stroke Model for Brush Type

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/data/models/CanvasModels.kt`

**Step 1: Read current file to understand structure**

Check the existing Stroke data class.

**Step 2: Add brushType field to Stroke**

Add to the Stroke data class:

```kotlin
data class Stroke(
    val id: String = UUID.randomUUID().toString(),
    val points: List<StrokePoint> = emptyList(),
    val color: Long = 0xFF1A1A1A,
    val width: Float = 4f,
    val brushType: BrushType = BrushType.PEN,
    val opacity: Float = 1f
)
```

**Step 3: Update CanvasConverters if needed**

Ensure Gson can serialize BrushType (it should work as enum).

**Step 4: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/data/models/
git commit -m "feat: add brushType to Stroke model"
```

---

### Task 6: Create Brush Engine

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/canvas/BrushEngine.kt`

**Step 1: Create brush rendering engine**

```kotlin
package com.gift.werkstatt.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.data.models.StrokePoint
import kotlin.math.sqrt
import kotlin.random.Random

object BrushEngine {

    fun DrawScope.drawBrushStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        brushType: BrushType,
        opacity: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        if (points.size < 2) return

        val scaledWidth = width * scaleX
        val strokeColor = color.copy(alpha = opacity)

        when (brushType) {
            BrushType.PEN -> drawPenStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.FINE_PEN -> drawPenStroke(points, strokeColor, scaledWidth * 0.5f, scaleX, scaleY)
            BrushType.BALLPOINT -> drawBallpointStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.PENCIL -> drawPencilStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.MARKER -> drawMarkerStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.WATERCOLOR -> drawWatercolorStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.FOUNTAIN_INK -> drawFountainInkStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
            BrushType.BRUSH -> drawBrushStroke(points, strokeColor, scaledWidth, scaleX, scaleY)
        }
    }

    private fun DrawScope.drawPenStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val path = createSmoothPath(points, scaleX, scaleY)
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = width,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    private fun DrawScope.drawBallpointStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Ballpoint has slight opacity variation
        points.zipWithNext().forEach { (p1, p2) ->
            val opacity = 0.95f + Random.nextFloat() * 0.05f
            drawLine(
                color = color.copy(alpha = color.alpha * opacity),
                start = Offset(p1.x * scaleX, p1.y * scaleY),
                end = Offset(p2.x * scaleX, p2.y * scaleY),
                strokeWidth = width,
                cap = StrokeCap.Round
            )
        }
    }

    private fun DrawScope.drawPencilStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Pencil has grainy texture - draw multiple thin lines
        val path = createSmoothPath(points, scaleX, scaleY)

        // Base stroke
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.7f),
            style = Stroke(
                width = width,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Add texture dots along the path
        points.forEach { point ->
            repeat(3) {
                val offsetX = (Random.nextFloat() - 0.5f) * width * 0.8f
                val offsetY = (Random.nextFloat() - 0.5f) * width * 0.8f
                if (Random.nextFloat() > 0.5f) {
                    drawCircle(
                        color = color.copy(alpha = color.alpha * Random.nextFloat() * 0.5f),
                        radius = width * 0.1f,
                        center = Offset(
                            point.x * scaleX + offsetX,
                            point.y * scaleY + offsetY
                        )
                    )
                }
            }
        }
    }

    private fun DrawScope.drawMarkerStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val path = createSmoothPath(points, scaleX, scaleY)
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = width * 1.5f,
                cap = StrokeCap.Square,
                join = StrokeJoin.Bevel
            )
        )
    }

    private fun DrawScope.drawWatercolorStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Watercolor has soft edges - draw multiple passes with decreasing opacity
        val path = createSmoothPath(points, scaleX, scaleY)

        // Outer soft glow
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.2f),
            style = Stroke(
                width = width * 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Middle layer
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.4f),
            style = Stroke(
                width = width * 1.4f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Core stroke
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = width,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    private fun DrawScope.drawFountainInkStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Fountain ink varies thickness based on speed
        if (points.size < 2) return

        for (i in 1 until points.size) {
            val p1 = points[i - 1]
            val p2 = points[i]

            // Calculate speed (distance between points)
            val dx = p2.x - p1.x
            val dy = p2.y - p1.y
            val distance = sqrt(dx * dx + dy * dy)

            // Slower = thicker, faster = thinner
            val speedFactor = (1f - (distance / 50f).coerceIn(0f, 0.7f))
            val strokeWidth = width * (0.5f + speedFactor * 0.8f)

            drawLine(
                color = color,
                start = Offset(p1.x * scaleX, p1.y * scaleY),
                end = Offset(p2.x * scaleX, p2.y * scaleY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }

    private fun DrawScope.drawBrushStroke(
        points: List<StrokePoint>,
        color: Color,
        width: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        // Brush has tapered ends
        if (points.size < 2) return

        val totalPoints = points.size

        for (i in 1 until points.size) {
            val p1 = points[i - 1]
            val p2 = points[i]

            // Taper at start and end
            val progress = i.toFloat() / totalPoints
            val taperFactor = when {
                progress < 0.1f -> progress * 10f // Taper in
                progress > 0.9f -> (1f - progress) * 10f // Taper out
                else -> 1f
            }

            val strokeWidth = width * taperFactor.coerceIn(0.3f, 1f)

            drawLine(
                color = color,
                start = Offset(p1.x * scaleX, p1.y * scaleY),
                end = Offset(p2.x * scaleX, p2.y * scaleY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }

    private fun createSmoothPath(
        points: List<StrokePoint>,
        scaleX: Float,
        scaleY: Float
    ): Path {
        return Path().apply {
            moveTo(points[0].x * scaleX, points[0].y * scaleY)

            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val midX = (prev.x + curr.x) / 2 * scaleX
                val midY = (prev.y + curr.y) / 2 * scaleY
                quadraticBezierTo(prev.x * scaleX, prev.y * scaleY, midX, midY)
            }

            val last = points.last()
            lineTo(last.x * scaleX, last.y * scaleY)
        }
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/canvas/BrushEngine.kt
git commit -m "feat: add brush engine with 8 brush types"
```

---

### Task 7: Add Thumbnail to CanvasEntry

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/data/models/CanvasModels.kt` (or wherever CanvasEntry is)

**Step 1: Add thumbnailPath field**

```kotlin
@Entity(tableName = "canvas_entries")
data class CanvasEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "Untitled",
    val strokes: List<Stroke> = emptyList(),
    val images: List<CanvasImage> = emptyList(),
    val viewportX: Float = 0f,
    val viewportY: Float = 0f,
    val zoom: Float = 1f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailPath: String? = null  // NEW
)
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/data/models/
git commit -m "feat: add thumbnailPath to CanvasEntry"
```

---

## Phase 3: Color Picker Components

### Task 8: Create Color Palettes Data

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/data/models/ColorPalettes.kt`

**Step 1: Define color palettes**

```kotlin
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
        0xFFB2DFDBЛ, // Mint
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
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/data/models/ColorPalettes.kt
git commit -m "feat: add color palette definitions"
```

---

### Task 9: Create Color Wheel Component

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/components/ColorWheel.kt`

**Step 1: Create color wheel composable**

```kotlin
package com.gift.werkstatt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun ColorWheel(
    selectedHue: Float,
    onHueSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .size(200.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val hue = calculateHue(offset, center)
                    onHueSelected(hue)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val hue = calculateHue(change.position, center)
                    onHueSelected(hue)
                }
            }
    ) {
        center = Offset(size.width / 2, size.height / 2)
        radius = minOf(size.width, size.height) / 2 - 16f

        // Draw color wheel
        val ringWidth = 32f
        for (i in 0 until 360) {
            val startAngle = i.toFloat()
            drawArc(
                color = Color.hsv(startAngle, 1f, 1f),
                startAngle = startAngle - 90,
                sweepAngle = 1.5f,
                useCenter = false,
                style = Stroke(width = ringWidth),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }

        // Draw selection indicator
        val indicatorAngle = Math.toRadians((selectedHue - 90).toDouble())
        val indicatorX = center.x + radius * cos(indicatorAngle).toFloat()
        val indicatorY = center.y + radius * sin(indicatorAngle).toFloat()

        // White circle with black border
        drawCircle(
            color = Color.White,
            radius = 14f,
            center = Offset(indicatorX, indicatorY)
        )
        drawCircle(
            color = Color.Black,
            radius = 14f,
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 3f)
        )
        drawCircle(
            color = Color.hsv(selectedHue, 1f, 1f),
            radius = 8f,
            center = Offset(indicatorX, indicatorY)
        )
    }
}

private fun calculateHue(position: Offset, center: Offset): Float {
    val dx = position.x - center.x
    val dy = position.y - center.y
    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    angle += 90 // Adjust so 0 is at top
    if (angle < 0) angle += 360
    return angle
}

@Composable
fun SaturationBrightnessSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    hue: Float,
    isSaturation: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isSaturation) {
        listOf(
            Color.hsv(hue, 0f, 1f),
            Color.hsv(hue, 1f, 1f)
        )
    } else {
        listOf(
            Color.hsv(hue, 1f, 0f),
            Color.hsv(hue, 1f, 1f)
        )
    }

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = Color(0xFF1A1A1A)
        )
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(4.dp)
        )
        androidx.compose.foundation.layout.Box {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val newValue = (offset.x / size.width).coerceIn(0f, 1f)
                            onValueChange(newValue)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val newValue = (change.position.x / size.width).coerceIn(0f, 1f)
                            onValueChange(newValue)
                        }
                    }
            ) {
                // Draw gradient background
                drawRect(
                    brush = Brush.horizontalGradient(gradientColors),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )

                // Draw indicator
                val indicatorX = value * size.width
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = Offset(indicatorX, size.height / 2)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 12f,
                    center = Offset(indicatorX, size.height / 2),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

private fun Modifier.fillMaxWidth() = this.then(Modifier.fillMaxWidth())
private fun Modifier.height(dp: androidx.compose.ui.unit.Dp) = this.then(Modifier.height(dp))
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/components/ColorWheel.kt
git commit -m "feat: add color wheel and saturation/brightness sliders"
```

---

### Task 10: Create Full Color Picker

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/components/ColorPicker.kt`

**Step 1: Create color picker composable**

```kotlin
package com.gift.werkstatt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.ColorPalettes
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSubtle

@Composable
fun ColorPicker(
    currentColor: Long,
    recentColors: List<Long>,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var hue by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(1f) }
    var brightness by remember { mutableStateOf(1f) }
    var selectedPalette by remember { mutableStateOf(ColorPalettes.PaletteType.BOLD) }

    // Initialize from current color
    LaunchedEffect(currentColor) {
        val color = Color(currentColor)
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(
            android.graphics.Color.argb(
                (color.alpha * 255).toInt(),
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            ),
            hsv
        )
        hue = hsv[0]
        saturation = hsv[1]
        brightness = hsv[2]
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Color wheel
        ColorWheel(
            selectedHue = hue,
            onHueSelected = { newHue ->
                hue = newHue
                onColorSelected(hsvToLong(hue, saturation, brightness))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Saturation slider
        SaturationBrightnessSlider(
            label = "Saturation",
            value = saturation,
            onValueChange = { newSat ->
                saturation = newSat
                onColorSelected(hsvToLong(hue, saturation, brightness))
            },
            hue = hue,
            isSaturation = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Brightness slider
        SaturationBrightnessSlider(
            label = "Brightness",
            value = brightness,
            onValueChange = { newBright ->
                brightness = newBright
                onColorSelected(hsvToLong(hue, saturation, brightness))
            },
            hue = hue,
            isSaturation = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Recent colors
        if (recentColors.isNotEmpty()) {
            Text(
                text = "RECENT",
                style = MaterialTheme.typography.labelMedium,
                color = AppPrimary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recentColors) { color ->
                    ColorSwatch(
                        color = color,
                        isSelected = color == currentColor,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Palette tabs
        Text(
            text = "PALETTES",
            style = MaterialTheme.typography.labelMedium,
            color = AppPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedPalette.ordinal,
            containerColor = Color.Transparent,
            contentColor = AppPrimary,
            edgePadding = 0.dp,
            divider = {}
        ) {
            ColorPalettes.PaletteType.values().forEach { palette ->
                Tab(
                    selected = selectedPalette == palette,
                    onClick = { selectedPalette = palette },
                    text = {
                        Text(
                            text = palette.displayName,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selectedContentColor = AppAccent,
                    unselectedContentColor = AppPrimary.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Palette colors grid
        val paletteColors = ColorPalettes.getPalette(selectedPalette)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(paletteColors) { color ->
                ColorSwatch(
                    color = color,
                    isSelected = color == currentColor,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(color))
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, AppAccent, RoundedCornerShape(8.dp))
                } else {
                    Modifier.border(1.dp, AppSubtle, RoundedCornerShape(8.dp))
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (isLightColor(color)) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun hsvToLong(hue: Float, saturation: Float, brightness: Float): Long {
    val color = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness))
    return color.toLong() and 0xFFFFFFFFL
}

private fun isLightColor(color: Long): Boolean {
    val c = Color(color)
    val luminance = 0.299 * c.red + 0.587 * c.green + 0.114 * c.blue
    return luminance > 0.5
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/components/ColorPicker.kt
git commit -m "feat: add full color picker with wheel, sliders, and palettes"
```

---

## Phase 4: Slide-up Panel & Brush Picker

### Task 11: Create Brush Picker

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/components/BrushPicker.kt`

**Step 1: Create brush picker composable**

```kotlin
package com.gift.werkstatt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSubtle
import com.gift.werkstatt.ui.theme.AppSurface

@Composable
fun BrushPicker(
    selectedBrush: BrushType,
    onBrushSelected: (BrushType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "BRUSHES",
            style = MaterialTheme.typography.labelMedium,
            color = AppPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(BrushType.values().toList()) { brushType ->
                BrushButton(
                    brushType = brushType,
                    isSelected = brushType == selectedBrush,
                    onClick = { onBrushSelected(brushType) }
                )
            }
        }
    }
}

@Composable
private fun BrushButton(
    brushType: BrushType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) AppAccent else AppSurface
    val contentColor = if (isSelected) Color.White else AppPrimary
    val borderColor = if (isSelected) AppAccent else AppSubtle

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Brush icon representation
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            BrushIcon(brushType = brushType, color = contentColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = brushType.displayName(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BrushIcon(brushType: BrushType, color: Color) {
    // Simple line representations of each brush
    androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
        val strokeWidth = when (brushType) {
            BrushType.PEN -> 3f
            BrushType.FINE_PEN -> 1.5f
            BrushType.BALLPOINT -> 2.5f
            BrushType.PENCIL -> 4f
            BrushType.MARKER -> 8f
            BrushType.WATERCOLOR -> 6f
            BrushType.FOUNTAIN_INK -> 3f
            BrushType.BRUSH -> 5f
        }

        val alpha = when (brushType) {
            BrushType.WATERCOLOR -> 0.6f
            BrushType.MARKER -> 0.85f
            BrushType.PENCIL -> 0.8f
            else -> 1f
        }

        drawLine(
            color = color.copy(alpha = alpha),
            start = androidx.compose.ui.geometry.Offset(4f, 20f),
            end = androidx.compose.ui.geometry.Offset(20f, 4f),
            strokeWidth = strokeWidth,
            cap = when (brushType) {
                BrushType.MARKER -> androidx.compose.ui.graphics.StrokeCap.Square
                else -> androidx.compose.ui.graphics.StrokeCap.Round
            }
        )
    }
}

private fun BrushType.displayName(): String = when (this) {
    BrushType.PEN -> "Pen"
    BrushType.FINE_PEN -> "Fine"
    BrushType.BALLPOINT -> "Ball"
    BrushType.PENCIL -> "Pencil"
    BrushType.MARKER -> "Marker"
    BrushType.WATERCOLOR -> "Water"
    BrushType.FOUNTAIN_INK -> "Ink"
    BrushType.BRUSH -> "Brush"
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/components/BrushPicker.kt
git commit -m "feat: add brush picker component"
```

---

### Task 12: Create Slide-up Panel

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/components/SlideUpPanel.kt`

**Step 1: Create slide-up panel**

```kotlin
package com.gift.werkstatt.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.ui.theme.AppBackground
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSubtle

@Composable
fun SlideUpPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    selectedBrush: BrushType,
    brushSize: Float,
    currentColor: Long,
    recentColors: List<Long>,
    onBrushSelected: (BrushType) -> Unit,
    onSizeChanged: (Float) -> Unit,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(AppBackground)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        if (dragAmount > 50) {
                            onDismiss()
                        }
                    }
                }
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppSubtle)
                )
            }

            // Brush picker
            BrushPicker(
                selectedBrush = selectedBrush,
                onBrushSelected = onBrushSelected,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Size slider
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIZE",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppPrimary
                    )
                    Text(
                        text = "${brushSize.toInt()}px",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppPrimary.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = brushSize,
                    onValueChange = onSizeChanged,
                    valueRange = 1f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = com.gift.werkstatt.ui.theme.AppAccent,
                        activeTrackColor = com.gift.werkstatt.ui.theme.AppAccent,
                        inactiveTrackColor = AppSubtle
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = AppSubtle)

            // Color picker
            ColorPicker(
                currentColor = currentColor,
                recentColors = recentColors,
                onColorSelected = onColorSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/components/SlideUpPanel.kt
git commit -m "feat: add slide-up panel for brush and color selection"
```

---

## Phase 5: Fix Canvas & Image Interaction

### Task 13: Fix Image Touch Blocking - Create Image Edit Mode

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/canvas/ImageEditMode.kt`

**Step 1: Create image edit mode overlay**

```kotlin
package com.gift.werkstatt.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppError
import kotlin.math.roundToInt

@Composable
fun ImageEditOverlay(
    image: CanvasImage,
    scaleX: Float,
    scaleY: Float,
    onMove: (Float, Float) -> Unit,
    onResize: (Float, Float) -> Unit,
    onDelete: () -> Unit,
    onExitEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val screenX = (image.x * scaleX).roundToInt()
    val screenY = (image.y * scaleY).roundToInt()
    val screenW = (image.width * scaleX).roundToInt()
    val screenH = (image.height * scaleY).roundToInt()

    val handleSize = 24.dp
    val handlePx = with(density) { handleSize.toPx() }

    Box(
        modifier = modifier
            .offset { IntOffset(screenX, screenY) }
            .size(
                width = with(density) { screenW.toDp() },
                height = with(density) { screenH.toDp() }
            )
    ) {
        // Border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, AppAccent, RoundedCornerShape(4.dp))
                .pointerInput(image.id) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onMove(dragAmount.x / scaleX, dragAmount.y / scaleY)
                    }
                }
        )

        // Corner handles
        listOf(
            Alignment.TopStart,
            Alignment.TopEnd,
            Alignment.BottomStart,
            Alignment.BottomEnd
        ).forEach { alignment ->
            Box(
                modifier = Modifier
                    .align(alignment)
                    .offset(
                        x = when (alignment) {
                            Alignment.TopStart, Alignment.BottomStart -> (-handleSize / 2)
                            else -> (handleSize / 2)
                        },
                        y = when (alignment) {
                            Alignment.TopStart, Alignment.TopEnd -> (-handleSize / 2)
                            else -> (handleSize / 2)
                        }
                    )
                    .size(handleSize)
                    .background(AppAccent, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .pointerInput(image.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Only bottom-end resizes, others just visual
                            if (alignment == Alignment.BottomEnd) {
                                val avgDelta = (dragAmount.x + dragAmount.y) / 2
                                onResize(
                                    avgDelta / scaleX,
                                    avgDelta / scaleX * (image.height / image.width)
                                )
                            }
                        }
                    }
            )
        }

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = AppError,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete image"
            )
        }
    }
}

@Composable
fun EditModeHint(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Tap outside to exit edit mode",
                color = Color.White,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/canvas/ImageEditMode.kt
git commit -m "feat: add image edit mode overlay with handles"
```

---

### Task 14: Update FixedCanvas with Brush Engine

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/ui/canvas/FixedCanvas.kt`

**Step 1: Update imports and add brush engine**

Update the FixedCanvas to use BrushEngine for rendering strokes:

```kotlin
package com.gift.werkstatt.ui.canvas

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.gift.werkstatt.data.models.BrushType
import com.gift.werkstatt.data.models.CanvasImage
import com.gift.werkstatt.data.models.Stroke as StrokeData
import com.gift.werkstatt.data.models.StrokePoint
import com.gift.werkstatt.ui.canvas.BrushEngine.drawBrushStroke
import com.gift.werkstatt.ui.theme.CanvasBackground
import com.gift.werkstatt.ui.theme.GridColor

// Fixed canvas dimensions (2:3 aspect ratio)
const val CANVAS_WIDTH = 1500f
const val CANVAS_HEIGHT = 2250f

enum class GridMode {
    NONE,
    LINES,
    DOTS
}

@Composable
fun FixedCanvas(
    strokes: List<StrokeData>,
    images: List<CanvasImage>,
    currentStroke: List<StrokePoint>,
    onStrokeStart: (StrokePoint) -> Unit,
    onStrokeMove: (StrokePoint) -> Unit,
    onStrokeEnd: () -> Unit,
    gridMode: GridMode = GridMode.LINES,
    gridSize: Float = 40f,
    strokeColor: Color,
    strokeWidth: Float,
    brushType: BrushType = BrushType.PEN,
    brushOpacity: Float = 1f,
    eraserMode: Boolean = false,
    editingImageId: String? = null,
    onImageDoubleTap: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val imageCache = remember { mutableMapOf<String, ImageBitmap?>() }

    LaunchedEffect(images.map { it.filePath }) {
        val currentPaths = images.map { it.filePath }.toSet()
        imageCache.keys.retainAll(currentPaths)
    }

    val loadedImages = remember(images.map { it.filePath }) {
        images.mapNotNull { canvasImage ->
            val cached = imageCache[canvasImage.filePath]
            if (cached != null) {
                canvasImage to cached
            } else {
                try {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(canvasImage.filePath, options)

                    val targetWidth = (canvasImage.width * 2).toInt()
                    val targetHeight = (canvasImage.height * 2).toInt()
                    val inSampleSize = calculateInSampleSize(
                        options.outWidth,
                        options.outHeight,
                        targetWidth,
                        targetHeight
                    )

                    options.apply {
                        this.inSampleSize = inSampleSize
                        inJustDecodeBounds = false
                    }

                    val bitmap = BitmapFactory.decodeFile(canvasImage.filePath, options)
                    val imageBitmap = bitmap?.asImageBitmap()
                    imageCache[canvasImage.filePath] = imageBitmap
                    imageBitmap?.let { canvasImage to it }
                } catch (e: Exception) {
                    imageCache[canvasImage.filePath] = null
                    null
                }
            }
        }
    }

    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(CANVAS_WIDTH / CANVAS_HEIGHT)
            .background(CanvasBackground)
            .pointerInput(editingImageId) {
                // Only handle drawing if NOT in image edit mode
                if (editingImageId != null) return@pointerInput

                canvasSize = Offset(size.width.toFloat(), size.height.toFloat())

                awaitEachGesture {
                    val firstDown = awaitFirstDown()

                    val scaleX = CANVAS_WIDTH / size.width
                    val scaleY = CANVAS_HEIGHT / size.height

                    val canvasX = firstDown.position.x * scaleX
                    val canvasY = firstDown.position.y * scaleY
                    onStrokeStart(StrokePoint(canvasX, canvasY, 1f))

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.pressed }
                        if (change != null) {
                            val cx = change.position.x * scaleX
                            val cy = change.position.y * scaleY
                            onStrokeMove(StrokePoint(cx, cy, 1f))
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    onStrokeEnd()
                }
            }
    ) {
        val scaleX = size.width / CANVAS_WIDTH
        val scaleY = size.height / CANVAS_HEIGHT

        // Draw grid
        when (gridMode) {
            GridMode.LINES -> drawLineGrid(gridSize, scaleX, scaleY)
            GridMode.DOTS -> drawDottedGrid(gridSize, scaleX, scaleY)
            GridMode.NONE -> { }
        }

        // Draw images
        loadedImages.forEach { (canvasImage, imageBitmap) ->
            val screenX = (canvasImage.x * scaleX).toInt()
            val screenY = (canvasImage.y * scaleY).toInt()
            val screenW = (canvasImage.width * scaleX).toInt()
            val screenH = (canvasImage.height * scaleY).toInt()

            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(screenX, screenY),
                dstSize = IntSize(screenW, screenH)
            )
        }

        // Draw completed strokes using brush engine
        strokes.forEach { stroke ->
            drawBrushStroke(
                points = stroke.points,
                color = Color(stroke.color),
                width = stroke.width,
                brushType = stroke.brushType,
                opacity = stroke.opacity,
                scaleX = scaleX,
                scaleY = scaleY
            )
        }

        // Draw current stroke
        if (currentStroke.isNotEmpty()) {
            drawBrushStroke(
                points = currentStroke,
                color = strokeColor,
                width = strokeWidth,
                brushType = brushType,
                opacity = brushOpacity,
                scaleX = scaleX,
                scaleY = scaleY
            )
        }
    }
}

// Keep existing helper functions: drawLineGrid, drawDottedGrid, calculateInSampleSize
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/canvas/FixedCanvas.kt
git commit -m "feat: integrate brush engine into FixedCanvas"
```

---

### Task 15: Update CanvasViewModel for Brush Support

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/ui/canvas/CanvasViewModel.kt`

**Step 1: Add brush state and recent colors**

Add to CanvasState:

```kotlin
data class CanvasState(
    val currentEntry: CanvasEntry? = null,
    val strokes: List<Stroke> = emptyList(),
    val images: List<CanvasImage> = emptyList(),
    val currentStroke: List<StrokePoint> = emptyList(),
    val viewportOffset: Offset = Offset.Zero,
    val zoom: Float = 1f,
    val gridMode: GridMode = GridMode.LINES,
    val snapToGrid: Boolean = false,
    val strokeColor: Long = 0xFF1A1A1A,
    val strokeWidth: Float = 8f,
    val brushType: BrushType = BrushType.PEN,
    val brushOpacity: Float = 1f,
    val eraserMode: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val allEntries: List<CanvasEntry> = emptyList(),
    val recentColors: List<Long> = emptyList(),
    val editingImageId: String? = null  // For image edit mode
)
```

**Step 2: Add brush and color methods to ViewModel**

```kotlin
fun setBrushType(type: BrushType) {
    val defaults = BrushDefaults.forType(type)
    _state.value = _state.value.copy(
        brushType = type,
        brushOpacity = defaults.opacity
    )
}

fun setBrushSize(size: Float) {
    _state.value = _state.value.copy(strokeWidth = size)
}

fun setColor(color: Long) {
    val recentColors = _state.value.recentColors.toMutableList()
    recentColors.remove(color)
    recentColors.add(0, color)
    if (recentColors.size > 8) {
        recentColors.removeLast()
    }
    _state.value = _state.value.copy(
        strokeColor = color,
        recentColors = recentColors
    )
}

fun enterImageEditMode(imageId: String) {
    _state.value = _state.value.copy(editingImageId = imageId)
}

fun exitImageEditMode() {
    _state.value = _state.value.copy(editingImageId = null)
}
```

**Step 3: Update onStrokeEnd to include brush type**

```kotlin
fun onStrokeEnd() {
    if (_state.value.eraserMode) return

    val currentStroke = _state.value.currentStroke
    if (currentStroke.size >= 2) {
        val newStroke = Stroke(
            points = currentStroke,
            color = _state.value.strokeColor,
            width = _state.value.strokeWidth,
            brushType = _state.value.brushType,
            opacity = _state.value.brushOpacity
        )
        _state.value = _state.value.copy(
            strokes = _state.value.strokes + newStroke,
            currentStroke = emptyList()
        )
        markUnsaved()
    } else {
        _state.value = _state.value.copy(currentStroke = emptyList())
    }
}
```

**Step 4: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/canvas/CanvasViewModel.kt
git commit -m "feat: add brush type, recent colors, and image edit mode to ViewModel"
```

---

## Phase 6: Updated Screens

### Task 16: Rewrite CanvasScreen with New UI

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/ui/screens/CanvasScreen.kt`

**Step 1: Rewrite with slim bars and slide-up panel**

This is a major rewrite. Key changes:
- Slim top bar (48dp) with back, title, save indicator
- Slim bottom bar with brush preview, color dot, menu
- Slide-up panel for brush/color selection
- Image double-tap enters edit mode
- No more ImageOverlay blocking touches

Full implementation provided in the code - see the design document for layout specs.

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/screens/CanvasScreen.kt
git commit -m "feat: rewrite CanvasScreen with modern UI and slide-up panel"
```

---

### Task 17: Create Gallery Screen

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/ui/screens/GalleryScreen.kt`

**Step 1: Create gallery screen with grid**

```kotlin
package com.gift.werkstatt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gift.werkstatt.data.models.CanvasEntry
import com.gift.werkstatt.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GalleryScreen(
    entries: List<CanvasEntry>,
    onEntryClick: (CanvasEntry) -> Unit,
    onNewEntry: () -> Unit,
    onDeleteEntry: (CanvasEntry) -> Unit,
    onSetThumbnail: (CanvasEntry) -> Unit,
    onRenameEntry: (CanvasEntry, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEntry by remember { mutableStateOf<CanvasEntry?>(null) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewEntry,
                containerColor = AppAccent,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Canvas")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Werkstatt",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AppPrimary
                )
            }

            if (entries.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Start your first canvas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppPrimary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNewEntry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppAccent
                            )
                        ) {
                            Text("Create")
                        }
                    }
                }
            } else {
                // Gallery grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries, key = { it.id }) { entry ->
                        GalleryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry) },
                            onLongPress = {
                                selectedEntry = entry
                                showOptionsMenu = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Options menu
    if (showOptionsMenu && selectedEntry != null) {
        ModalBottomSheet(
            onDismissRequest = { showOptionsMenu = false },
            containerColor = AppBackground
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = selectedEntry?.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        showOptionsMenu = false
                        selectedEntry?.let { onSetThumbnail(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Thumbnail", color = AppPrimary)
                }

                TextButton(
                    onClick = {
                        showOptionsMenu = false
                        showRenameDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Rename", color = AppPrimary)
                }

                TextButton(
                    onClick = {
                        showOptionsMenu = false
                        showDeleteConfirm = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete", color = AppError)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Rename dialog
    if (showRenameDialog && selectedEntry != null) {
        var newTitle by remember { mutableStateOf(selectedEntry?.title ?: "") }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Canvas") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedEntry?.let { onRenameEntry(it, newTitle) }
                    showRenameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete confirmation
    if (showDeleteConfirm && selectedEntry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Canvas?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    selectedEntry?.let { onDeleteEntry(it) }
                    showDeleteConfirm = false
                }) {
                    Text("Delete", color = AppError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun GalleryCard(
    entry: CanvasEntry,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Thumbnail area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(AppSubtle),
                contentAlignment = Alignment.Center
            ) {
                if (entry.thumbnailPath != null && File(entry.thumbnailPath).exists()) {
                    AsyncImage(
                        model = entry.thumbnailPath,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder with stroke count
                    Text(
                        text = "${entry.strokes.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppPrimary.copy(alpha = 0.3f)
                    )
                }
            }

            // Info area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppBackground)
                    .padding(12.dp)
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateFormat.format(Date(entry.updatedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppPrimary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/ui/screens/GalleryScreen.kt
git commit -m "feat: add gallery screen with grid layout and custom thumbnails"
```

---

### Task 18: Update Navigation to Use Gallery as Home

**Files:**
- Modify: `app/src/main/java/com/gift/werkstatt/WerkstattApp.kt` (or navigation file)

**Step 1: Update navigation**

Change start destination from EntryListScreen to GalleryScreen.

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/
git commit -m "feat: set Gallery as home screen"
```

---

### Task 19: Add Coil Dependency for Image Loading

**Files:**
- Modify: `app/build.gradle.kts`

**Step 1: Add Coil dependency**

```kotlin
// Image loading
implementation("io.coil-kt:coil-compose:2.5.0")
```

**Step 2: Sync and commit**

```bash
git add app/build.gradle.kts
git commit -m "chore: add Coil for image loading"
```

---

## Phase 7: Final Integration & Polish

### Task 20: Create Thumbnail Manager

**Files:**
- Create: `app/src/main/java/com/gift/werkstatt/util/ThumbnailManager.kt`

**Step 1: Create thumbnail utilities**

```kotlin
package com.gift.werkstatt.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ThumbnailManager {

    private const val THUMBNAIL_DIR = "thumbnails"
    private const val THUMBNAIL_SIZE = 400

    fun saveThumbnail(context: Context, bitmap: Bitmap, entryId: String): String {
        val dir = File(context.filesDir, THUMBNAIL_DIR)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "thumb_$entryId.jpg")

        // Scale to thumbnail size
        val scaled = Bitmap.createScaledBitmap(
            bitmap,
            THUMBNAIL_SIZE,
            (THUMBNAIL_SIZE * bitmap.height / bitmap.width),
            true
        )

        FileOutputStream(file).use { out ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }

        if (scaled != bitmap) scaled.recycle()

        return file.absolutePath
    }

    fun saveThumbnailFromUri(context: Context, uri: Uri, entryId: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let { saveThumbnail(context, it, entryId) }
        } catch (e: Exception) {
            null
        }
    }

    fun deleteThumbnail(context: Context, entryId: String) {
        val dir = File(context.filesDir, THUMBNAIL_DIR)
        val file = File(dir, "thumb_$entryId.jpg")
        if (file.exists()) file.delete()
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/gift/werkstatt/util/ThumbnailManager.kt
git commit -m "feat: add thumbnail manager utility"
```

---

### Task 21: Final Testing & Cleanup

**Step 1: Build and test**

```bash
./gradlew assembleDebug
```

**Step 2: Test on Pixel 8a**
- Verify drawing works over images
- Verify double-tap enters image edit mode
- Verify all 8 brush types render correctly
- Verify color picker and palettes work
- Verify gallery grid displays correctly
- Verify custom thumbnails save and display

**Step 3: Final commit**

```bash
git add .
git commit -m "feat: complete canvas redesign with modern UI, brushes, and gallery"
```

---

## Summary

**Total Tasks:** 21

**Phase 1 - Theme:** Tasks 1-3 (Colors, Fonts, Theme)
**Phase 2 - Data:** Tasks 4-7 (Brushes, Stroke model, Thumbnails)
**Phase 3 - Color Picker:** Tasks 8-10 (Palettes, Wheel, Full picker)
**Phase 4 - Panel:** Tasks 11-12 (Brush picker, Slide-up panel)
**Phase 5 - Canvas Fix:** Tasks 13-15 (Image edit mode, Canvas update, ViewModel)
**Phase 6 - Screens:** Tasks 16-19 (CanvasScreen, GalleryScreen, Navigation, Coil)
**Phase 7 - Polish:** Tasks 20-21 (Thumbnails, Testing)

---

**Plan complete and saved to `docs/plans/2026-01-27-canvas-redesign-implementation.md`.**

**Two execution options:**

**1. Subagent-Driven (this session)** - I dispatch fresh subagent per task, review between tasks, fast iteration

**2. Parallel Session (separate)** - Open new session with executing-plans, batch execution with checkpoints

**Which approach?**
