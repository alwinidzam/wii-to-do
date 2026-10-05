package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class PixelAvatar(
  val id: String,
  val name: String,
  val role: String,
  val palette: Map<Char, Color>,
  val matrix: List<String>,
  val defaultBgColorHex: Long = 0xFF1E293B
)

// 1. Pixel Hacker / Dev: Dark obsidian hoodie, neon matrix green glasses
val PixelHacker = PixelAvatar(
  id = "pixel_hacker",
  name = "Cyber Hacker",
  role = "Developer & Core",
  palette = mapOf(
    '.' to Color.Transparent,
    'H' to Color(0xFF1E293B), // Dark Hoodie
    'F' to Color(0xFFFED7AA), // Face Skin
    'G' to Color(0xFF22C55E), // Terminal Green Visor
    'A' to Color(0xFF86EFAC), // Bright Green Accent
    'B' to Color(0xFF0F172A)  // Outline / Shadow
  ),
  matrix = listOf(
    "....HHHH....",
    "..HHBBBBHH..",
    ".HBBFFFFBBH.",
    ".HBFFFFFFBH.",
    ".HBGGGGGGGB.",
    ".HBGAAGAGGB.",
    ".HBBFFFFBBH.",
    "..HHBFFBHH..",
    "...HHHHHH...",
    "..HHHHHHHH..",
    ".HHHH..HHHH.",
    ".HH......HH."
  ),
  defaultBgColorHex = 0xFF0F172A
)

// 2. Pixel Cyber Ninja: Midnight mask, neon cyan visor slit, angular headband
val PixelNinja = PixelAvatar(
  id = "pixel_ninja",
  name = "Cyber Ninja",
  role = "Agile Assassin",
  palette = mapOf(
    '.' to Color.Transparent,
    'M' to Color(0xFF0F172A), // Midnight Mask
    'C' to Color(0xFF06B6D4), // Neon Cyan Eye Visor
    'B' to Color(0xFF38BDF8), // Glowing Cyan Accent
    'R' to Color(0xFFEF4444), // Crimson Ribbon
    'D' to Color(0xFF1E293B)  // Dark Fabric
  ),
  matrix = listOf(
    "...RRRRRR...",
    "..MMMMMMMM..",
    ".MMDDDDDDMM.",
    ".MMCCCCCCCCM.",
    ".MMCBCCCCBCM.",
    ".MMDDDDDDMM.",
    ".MMMMMMMMMM.",
    "..MMMMMMMM..",
    "...MMDDMM...",
    "..MDDDDDDM..",
    ".MDD....DDM.",
    ".MM......MM."
  ),
  defaultBgColorHex = 0xFF0B1329
)

// 3. Pixel Wizard / Mage: Amethyst purple hat, gold star, wizard eyes
val PixelWizard = PixelAvatar(
  id = "pixel_wizard",
  name = "Pixel Mage",
  role = "Flow Architect",
  palette = mapOf(
    '.' to Color.Transparent,
    'P' to Color(0xFF7C3AED), // Purple Hat
    'Y' to Color(0xFFFACC15), // Gold Star Accent
    'F' to Color(0xFFFDE68A), // Warm Face
    'W' to Color(0xFFF8FAFC), // White Beard
    'B' to Color(0xFF4C1D95)  // Deep Hat Rim
  ),
  matrix = listOf(
    ".....PP.....",
    "....PPPP....",
    "...PPYYPP...",
    "..PPYYYYPP..",
    ".BBBBBBBBBB.",
    "..BBFFFFBB..",
    "..BFFFFFBB..",
    "..BBWWWWBB..",
    "..BWWWWWWBB.",
    ".WWWWWWWWWW.",
    ".PPPP..PPPP.",
    ".PP......PP."
  ),
  defaultBgColorHex = 0xFF2E1065
)

// 4. Pixel Knight / Paladin: Titanium silver helm, crimson plume, visor slit
val PixelKnight = PixelAvatar(
  id = "pixel_knight",
  name = "Pixel Knight",
  role = "Focus Guardian",
  palette = mapOf(
    '.' to Color.Transparent,
    'R' to Color(0xFFDC2626), // Crimson Plume
    'S' to Color(0xFFCBD5E1), // Titanium Silver Helm
    'D' to Color(0xFF64748B), // Steel Shade
    'B' to Color(0xFF0F172A), // Visor Slit
    'G' to Color(0xFFF59E0B)  // Gold Trim
  ),
  matrix = listOf(
    "....RRRR....",
    "...RRRRRR...",
    "..SSGGGGSS..",
    ".SSSSSSSSSS.",
    ".SSBBBBBBSS.",
    ".SSBDDDBDSS.",
    ".SSDSSSSSDSS.",
    "..SSDDDDSS..",
    "...SSSSSS...",
    "..SSSSSSSS..",
    ".SSSS..SSSS.",
    ".SS......SS."
  ),
  defaultBgColorHex = 0xFF1E293B
)

// 5. Pixel Space Explorer: Astronaut helmet, curved gold starlight visor
val PixelAstronaut = PixelAvatar(
  id = "pixel_astronaut",
  name = "Astronaut",
  role = "Deep Space Flow",
  palette = mapOf(
    '.' to Color.Transparent,
    'W' to Color(0xFFF8FAFC), // White Suit
    'G' to Color(0xFFF59E0B), // Golden Visor
    'Y' to Color(0xFFFEF08A), // Visor Glint
    'B' to Color(0xFF94A3B8), // Helmet Trim
    'R' to Color(0xFF3B82F6)  // Cosmic Blue Detail
  ),
  matrix = listOf(
    "..WWWWWWWW..",
    ".WWBBBBBBWW.",
    "WWBGGGGGGWW.",
    "WWGGGGYYGGWW",
    "WWGGGGYYGGWW",
    "WWBGGGGGGWW.",
    ".WWBBBBBBWW.",
    "..WWRRRRWW..",
    "...WWWWWW...",
    "..WWWWWWWW..",
    ".WWWW..WWWW.",
    ".WW......WW."
  ),
  defaultBgColorHex = 0xFF0F172A
)

// 6. Retro Gamer Boy: Cherry-red backwards cap with yellow headphones
val PixelGamer = PixelAvatar(
  id = "pixel_gamer",
  name = "Retro Gamer",
  role = "Speedrunner",
  palette = mapOf(
    '.' to Color.Transparent,
    'R' to Color(0xFFEF4444), // Red Cap
    'Y' to Color(0xFFEAB308), // Yellow Headphones
    'F' to Color(0xFFFED7AA), // Skin
    'B' to Color(0xFF1E293B), // Dark Hair/Eyes
    'W' to Color(0xFFF8FAFC)  // Cap Brim
  ),
  matrix = listOf(
    "..RRRRRRRR..",
    ".RRRRRRRRRR.",
    "YYRFFFFFFFFW",
    "YYBFFBFFBFFW",
    "YYFFFFFFFFFW",
    "YYRFFFFFFR..",
    ".YYRRRRRR...",
    "..RRRRRRRR..",
    "...RFFFFR...",
    "..RRRRRRRR..",
    ".RRRR..RRRR.",
    ".RR......RR."
  ),
  defaultBgColorHex = 0xFF1C1917
)

// 7. Pixel Scholar / Academic: Mortarboard cap, round circular spectacles
val PixelScholar = PixelAvatar(
  id = "pixel_scholar",
  name = "Pixel Scholar",
  role = "Research Master",
  palette = mapOf(
    '.' to Color.Transparent,
    'H' to Color(0xFF047857), // Emerald Cap
    'T' to Color(0xFFF59E0B), // Gold Tassel
    'F' to Color(0xFFFED7AA), // Skin
    'G' to Color(0xFF0F172A), // Glasses Frame
    'B' to Color(0xFF38BDF8)  // Glass Lens
  ),
  matrix = listOf(
    "TTTTTTTTTTTT",
    "...HHHHHH...",
    "..HHHHHHHH..",
    ".HHHHHHHHHH.",
    ".HHFFFFFFHH.",
    ".HHGBBGBBHH.",
    ".HHGFFGFFHH.",
    "..HHFFFFHH..",
    "...HHHHHH...",
    "..HHHHHHHH..",
    ".HHHH..HHHH.",
    ".HH......HH."
  ),
  defaultBgColorHex = 0xFF064E3B
)

// 8. Pixel Cyber Cat: Calico / Obsidian 8-bit kitten with ears & whiskers
val PixelCat = PixelAvatar(
  id = "pixel_cat",
  name = "Pixel Cat",
  role = "Companion",
  palette = mapOf(
    '.' to Color.Transparent,
    'E' to Color(0xFFF43F5E), // Pink Ears
    'C' to Color(0xFF1E293B), // Charcoal Fur
    'G' to Color(0xFF4ADE80), // Neon Green Eyes
    'W' to Color(0xFFF8FAFC), // White Whiskers/Muzzle
    'N' to Color(0xFFFB7185)  // Cute Nose
  ),
  matrix = listOf(
    ".EE......EE.",
    "EEEE....EEEE",
    "CCCCCCCCCCCC",
    "CCCCCCCCCCCC",
    "CCGGCCCCGGCC",
    "CCGGCCCCGGCC",
    "WWWWNNNNWWWW",
    "CCCCCCCCCCCC",
    ".CCCCCCCCCC.",
    "..CCCCCCCC..",
    ".CCCC..CCCC.",
    ".CC......CC."
  ),
  defaultBgColorHex = 0xFF1E1B4B
)

val PIXEL_AVATAR_LIST = listOf(
  PixelHacker,
  PixelNinja,
  PixelWizard,
  PixelKnight,
  PixelAstronaut,
  PixelGamer,
  PixelScholar,
  PixelCat
)

@Composable
fun PixelArtAvatar(
  avatar: PixelAvatar,
  modifier: Modifier = Modifier,
  backgroundColor: Color? = null
) {
  val bg = backgroundColor ?: Color(avatar.defaultBgColorHex)
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(bg),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .padding(6.dp)
    ) {
      val rows = avatar.matrix.size
      val cols = avatar.matrix[0].length
      val pixelWidth = size.width / cols
      val pixelHeight = size.height / rows

      avatar.matrix.forEachIndexed { rowIndex, rowStr ->
        rowStr.forEachIndexed { colIndex, charKey ->
          val color = avatar.palette[charKey] ?: Color.Transparent
          if (color != Color.Transparent) {
            drawRect(
              color = color,
              topLeft = Offset(colIndex * pixelWidth, rowIndex * pixelHeight),
              // Slight 0.35f bleed prevents fractional pixel sub-raster gaps
              size = Size(pixelWidth + 0.35f, pixelHeight + 0.35f)
            )
          }
        }
      }
    }
  }
}
