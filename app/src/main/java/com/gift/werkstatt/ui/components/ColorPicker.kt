package com.gift.werkstatt.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gift.werkstatt.data.models.ColorPalettes
import com.gift.werkstatt.ui.theme.AppAccent
import com.gift.werkstatt.ui.theme.AppPrimary
import com.gift.werkstatt.ui.theme.AppSubtle

/**
 * Full color picker combining:
 * - ColorWheel for hue selection
 * - Saturation slider
 * - Brightness slider
 * - Recent colors row
 * - Palette tabs + color grid
 *
 * @param currentColor The currently selected color as ARGB Long
 * @param recentColors List of recently used colors
 * @param onColorSelected Callback when a color is selected
 * @param modifier Modifier for the composable
 */
@Composable
fun ColorPicker(
    currentColor: Long,
    recentColors: List<Long>,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Initialize HSV state from currentColor
    val hsv = remember(currentColor) {
        val hsvArray = FloatArray(3)
        AndroidColor.colorToHSV(currentColor.toInt(), hsvArray)
        hsvArray
    }

    var hue by remember(currentColor) { mutableFloatStateOf(hsv[0]) }
    var saturation by remember(currentColor) { mutableFloatStateOf(hsv[1]) }
    var brightness by remember(currentColor) { mutableFloatStateOf(hsv[2]) }
    var selectedPaletteIndex by remember { mutableIntStateOf(0) }

    val paletteTypes = ColorPalettes.PaletteType.entries

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. ColorWheel (centered)
        ColorWheel(
            selectedHue = hue,
            onHueSelected = { newHue ->
                hue = newHue
                onColorSelected(hsvToLong(hue, saturation, brightness))
            },
            modifier = Modifier.size(200.dp)
        )

        // 2. Spacer(16dp)
        Spacer(modifier = Modifier.height(16.dp))

        // 3. Saturation slider
        SaturationBrightnessSlider(
            label = "SATURATION",
            value = saturation,
            onValueChange = { newSaturation ->
                saturation = newSaturation
                onColorSelected(hsvToLong(hue, saturation, brightness))
            },
            hue = hue,
            isSaturation = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Spacer(12dp)
        Spacer(modifier = Modifier.height(12.dp))

        // 5. Brightness slider
        SaturationBrightnessSlider(
            label = "BRIGHTNESS",
            value = brightness,
            onValueChange = { newBrightness ->
                brightness = newBrightness
                onColorSelected(hsvToLong(hue, saturation, brightness))
            },
            hue = hue,
            isSaturation = false,
            modifier = Modifier.fillMaxWidth()
        )

        // 6. Spacer(20dp)
        Spacer(modifier = Modifier.height(20.dp))

        // 7. "RECENT" label + row of color swatches
        if (recentColors.isNotEmpty()) {
            Text(
                text = "RECENT",
                style = MaterialTheme.typography.labelMedium,
                color = AppPrimary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(recentColors) { color ->
                    ColorSwatch(
                        color = color,
                        isSelected = color == currentColor,
                        onClick = { onColorSelected(color) }
                    )
                }
            }

            // 8. Spacer(16dp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 9. "PALETTES" label + ScrollableTabRow
        Text(
            text = "PALETTES",
            style = MaterialTheme.typography.labelMedium,
            color = AppPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedPaletteIndex,
            containerColor = Color.Transparent,
            contentColor = AppPrimary,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                if (selectedPaletteIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedPaletteIndex]),
                        color = AppAccent
                    )
                }
            },
            divider = {}
        ) {
            paletteTypes.forEachIndexed { index, paletteType ->
                Tab(
                    selected = selectedPaletteIndex == index,
                    onClick = { selectedPaletteIndex = index },
                    text = {
                        Text(
                            text = paletteType.displayName,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }

        // 10. Spacer(12dp)
        Spacer(modifier = Modifier.height(12.dp))

        // 11. Palette colors LazyRow
        val selectedPalette = ColorPalettes.getPalette(paletteTypes[selectedPaletteIndex])
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(selectedPalette) { color ->
                ColorSwatch(
                    color = color,
                    isSelected = color == currentColor,
                    onClick = { onColorSelected(color) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * A color swatch showing a single color
 * 40dp rounded square with selection indicator
 *
 * @param color The color to display as ARGB Long
 * @param isSelected Whether this swatch is currently selected
 * @param onClick Callback when clicked
 */
@Composable
fun ColorSwatch(
    color: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        if (isLightColor(color)) AppPrimary else Color.White
    } else {
        AppSubtle
    }
    val borderWidth = if (isSelected) 3.dp else 1.dp

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(color.toInt()))
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        // Selection indicator - inner ring for selected state
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(16.dp)
                    .border(
                        width = 2.dp,
                        color = if (isLightColor(color)) AppPrimary else Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/**
 * Convert HSV values to ARGB Long
 *
 * @param hue Hue value (0-360)
 * @param saturation Saturation value (0-1)
 * @param brightness Brightness/Value (0-1)
 * @return ARGB color as Long
 */
fun hsvToLong(hue: Float, saturation: Float, brightness: Float): Long {
    val hsv = floatArrayOf(hue, saturation, brightness)
    return AndroidColor.HSVToColor(hsv).toLong() and 0xFFFFFFFFL
}

/**
 * Determine if a color is light (for contrast calculations)
 * Uses luminance calculation
 *
 * @param color ARGB color as Long
 * @return true if the color is considered light
 */
fun isLightColor(color: Long): Boolean {
    val colorInt = color.toInt()
    val red = AndroidColor.red(colorInt)
    val green = AndroidColor.green(colorInt)
    val blue = AndroidColor.blue(colorInt)
    // Calculate relative luminance using standard coefficients
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    return luminance > 0.5
}
