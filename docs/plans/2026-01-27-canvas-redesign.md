# Werkstatt Canvas Redesign

## Overview

Complete redesign of the Werkstatt drawing app focusing on:
- Fixing canvas/image interaction (can't paint on images)
- Modern minimal UI refresh
- Expanded brush system (8 types)
- Artist's color palette with presets
- Gallery-style home with custom thumbnails

## App Flow

```
Gallery (Home) → Canvas Editor → Slide-up Tools Panel
                     ↓
              Image Edit Mode (double-tap)
```

## Visual Design System

### Colors (App UI)
```
Background:     #FFFFFF (pure white)
Surface:        #FAFAFA (off-white for cards)
Primary:        #1A1A1A (near-black for text/icons)
Accent:         #66C7B6 (teal/mint)
Subtle:         #E5E5E5 (borders, dividers)
Error:          #EF4444 (delete actions)
```

### Typography
- Headers: Noto Serif Semi Bold (600)
- Body: Noto Serif Light (300)
- Sizes: 14sp body, 18sp titles, 12sp captions

### Iconography
- Thin line icons (1.5px stroke)
- 24dp standard size
- Rounded caps and joins

### Spacing & Layout
- 16dp standard padding
- 8dp between elements
- 12dp card corner radius
- Subtle shadows: `0 2px 8px rgba(0,0,0,0.08)`

## Screen Designs

### 1. Gallery Screen (Home)

Grid layout with 2 columns showing canvas thumbnails.

**Layout:**
- Header: "Werkstatt" (Noto Serif Semi Bold)
- Grid: 2 columns, 8dp gap, 3:4 aspect ratio thumbnails
- Title + date below each thumbnail
- Floating "+" button (teal accent)

**Interactions:**
- Tap thumbnail → Opens canvas editor
- Long-press thumbnail → Options menu:
  - "Set Thumbnail" → Opens crop picker
  - "Rename" → Inline text edit
  - "Delete" → Confirmation dialog

**Custom Thumbnail Flow:**
1. Long-press canvas → "Set Thumbnail"
2. Options:
   - "Capture Current" - screenshots canvas as-is
   - "Choose Area" - crop rectangle over canvas
   - "From Gallery" - pick image from phone
3. Thumbnail updates immediately

**Empty State:**
- Centered illustration (simple line drawing)
- "Start your first canvas" text
- Teal accent "Create" button

### 2. Canvas Screen

Full-screen canvas with minimal chrome. Drawing works everywhere including over images.

**Layout:**
- Slim top bar (48dp): back arrow + title + save indicator
- Full canvas area
- Slim bottom bar: brush picker + size slider + color circle + menu

**Bottom Bar Elements:**
- Brush button (current brush icon + name)
- Size slider (inline, compact)
- Color circle (current color, tap to open panel)
- Menu icon (grid, export, undo, eraser)

### 3. Image Edit Mode

Activated by double-tapping an image. Drawing disabled while active.

**Visual:**
- Teal border around selected image
- Corner handles (circles) for resizing
- Delete button below image
- Hint toast: "Tap outside to exit edit"

**Behavior:**
- Double-tap image → enters edit mode
- Drag image to move
- Drag corners to resize (maintains aspect ratio)
- Tap delete button to remove
- Tap outside image → exits edit mode

### 4. Slide-up Panel

Brush and color selection panel.

**Layout (top to bottom):**
1. Drag handle
2. BRUSHES section - horizontal row of 8 brush icons
3. SIZE slider with px label
4. COLOR section:
   - Color wheel
   - Saturation slider
   - Brightness slider
5. RECENT - row of 8 recent colors
6. PALETTES - tabs + color grid

## Brush System

### 8 Brush Types

| Brush | Style | Behavior |
|-------|-------|----------|
| Pen | Smooth, consistent | Bezier curves, uniform width |
| Fine Pen | Thin, precise | 0.5x base width, sharp edges |
| Ballpoint | Slightly textured | Minor opacity variation (95-100%) |
| Pencil | Grainy texture | Alpha noise pattern, soft edges |
| Marker | Flat, bold | Chisel cap, semi-transparent (85%) |
| Watercolor | Soft, bleeds | Feathered edges, 60% opacity |
| Fountain Ink | Variable thickness | Width varies with speed |
| Brush | Painterly | Tapered ends, pressure-sensitive |

### Size Range
1px - 50px (slider)

## Color System (Artist's Palette)

### Color Picker
- Color wheel for hue selection
- Saturation slider
- Brightness slider

### Recent Colors
Auto-saves last 8 colors used

### Preset Palettes

| Palette | Description |
|---------|-------------|
| Bold | Pure red, blue, yellow, green, orange, purple, black, white |
| Pastel | Soft pink, lavender, mint, peach, baby blue, cream |
| Earth | Browns, terracotta, olive, ochre, rust, sand |
| Neon | Hot pink, electric blue, lime, bright orange, magenta |
| Skin | Range of skin tones from light to dark |
| Vintage | Muted mustard, dusty rose, sage, burgundy, navy |

## Technical Implementation

### Files to Create

| File | Purpose |
|------|---------|
| `GalleryScreen.kt` | New home screen with grid |
| `ImageEditMode.kt` | Double-tap edit overlay |
| `SlideUpPanel.kt` | Brush + color picker panel |
| `ColorPicker.kt` | Color wheel + palettes |
| `BrushEngine.kt` | 8 brush type renderers |
| `BrushData.kt` | Brush definitions |
| `ThumbnailManager.kt` | Custom thumbnail handling |

### Files to Modify

| File | Changes |
|------|---------|
| `Color.kt` | New color palette + accent |
| `Type.kt` | Noto Serif font family |
| `Theme.kt` | Modern minimal theme |
| `CanvasScreen.kt` | Slim bars, drawing-first mode |
| `FixedCanvas.kt` | Remove image overlay blocking |
| `CanvasEntry.kt` | Add thumbnail field |

### Data Model Changes

```kotlin
// Add to CanvasEntry
data class CanvasEntry(
    // existing fields...
    val thumbnailPath: String? = null
)

// New brush type
enum class BrushType {
    PEN, FINE_PEN, BALLPOINT, PENCIL,
    MARKER, WATERCOLOR, FOUNTAIN_INK, BRUSH
}

// Brush configuration
data class BrushConfig(
    val type: BrushType,
    val size: Float,
    val color: Long,
    val opacity: Float = 1f
)
```

### Key Technical Fixes

1. **Remove ImageOverlay touch blocking** - Current overlay intercepts all touch events
2. **Add selectedImageId state** - Track which image is in edit mode
3. **Brush rendering** - Different path effects per BrushType
4. **Thumbnail generation** - Bitmap capture and storage

## Target Device

Pixel 8a (1080 x 2400, 6.1")
