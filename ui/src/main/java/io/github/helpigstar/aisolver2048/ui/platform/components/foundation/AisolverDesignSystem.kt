package io.github.helpigstar.aisolver2048.ui.platform.components.foundation

import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AisolverLightColorScheme = lightColorScheme(
    primary = Color(0xFF8F7A66),
    onPrimary = Color(0xFFF9F6F2),
    secondary = Color(0xFFEDC22E),
    onSecondary = Color(0xFFF9F6F2),
    tertiary = Color(0xFFBBADA0),
    onTertiary = Color(0xFFF9F6F2),
    background = Color(0xFFFAF8EF),
    onBackground = Color(0xFF776E65),
    surface = Color(0xFFFAF8EF),
    onSurface = Color(0xFF776E65),
    surfaceVariant = Color(0xFFEEE4DA),
    onSurfaceVariant = Color(0xFF776E65),
    primaryContainer = Color(0xFFBBADA0),
    onPrimaryContainer = Color(0xFFF9F6F2),
    secondaryContainer = Color(0x33EDC22E),
    onSecondaryContainer = Color(0xFF776E65)
)

val AisolverTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 42.sp,
        letterSpacing = (-1).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 13.sp
    )
)

data class AisolverBoardMetrics(
    val isCompact: Boolean,
    val boardSize: Dp,
    val boardPadding: Dp,
    val gap: Dp,
    val cellSize: Dp,
) {
    fun offsetFor(index: Int): Dp = (cellSize + gap) * index
}

data class AisolverTileAppearance(
    val background: Color,
    val contentColor: Color,
    val fontSize: TextUnit,
    val glowColor: Color,
    val insetBorderColor: Color,
)

object AisolverUiTokens {
    val PageBackground: Color = Color(0xFFFAF8EF)
    val PrimaryText: Color = Color(0xFF776E65)
    val BrightText: Color = Color(0xFFF9F6F2)
    val BaseTile: Color = Color(0xFFEEE4DA)
    val BoardBackground: Color = Color(0xFFBBADA0)
    val ActionBrown: Color = Color(0xFF8F7A66)
    val Gold: Color = Color(0xFFEDC22E)
    val GridCellFill: Color = Color(0x59EEE4DA)
    val DefaultOverlay: Color = Color(0x80EEE4DA)
    val WinOverlay: Color = Color(0x80EDC22E)
    val Divider: Color = Color(0xFFD8D4D0)
    val ScoreAddition: Color = Color(0xE6776E65)
    val SelectionTint: Color = Color(0x33EDC22E)

    val Breakpoint: Dp = 520.dp
    val DesktopBoardSize: Dp = 500.dp
    val MobileBoardSize: Dp = 280.dp
    val DesktopBoardPadding: Dp = 15.dp
    val MobileBoardPadding: Dp = 10.dp
    val DesktopGap: Dp = 15.dp
    val MobileGap: Dp = 10.dp
    val BoardRadius: Dp = 6.dp
    val TileRadius: Dp = 3.dp
    val ButtonRadius: Dp = 3.dp
    val ButtonHeight: Dp = 42.dp
    val BadgeHeight: Dp = 55.dp

    fun boardMetrics(maxWidth: Dp): AisolverBoardMetrics {
        val isCompact = maxWidth < Breakpoint
        val targetBoard = if (isCompact) MobileBoardSize else DesktopBoardSize
        val boardSize = if (maxWidth < targetBoard) maxWidth else targetBoard
        val padding = if (isCompact) MobileBoardPadding else DesktopBoardPadding
        val gap = if (isCompact) MobileGap else DesktopGap
        val cellSize = (boardSize - (padding * 2) - (gap * 3)) / 4
        return AisolverBoardMetrics(
            isCompact = isCompact,
            boardSize = boardSize,
            boardPadding = padding,
            gap = gap,
            cellSize = cellSize
        )
    }

    fun tileAppearance(value: Int, isCompact: Boolean): AisolverTileAppearance {
        val fontSize = when {
            value > 2048 -> if (isCompact) 10.sp else 30.sp
            value >= 1024 -> if (isCompact) 15.sp else 35.sp
            value >= 128 -> if (isCompact) 25.sp else 45.sp
            else -> if (isCompact) 35.sp else 55.sp
        }

        return when {
            value <= 2 -> AisolverTileAppearance(
                background = Color(0xFFEEE4DA),
                contentColor = PrimaryText,
                fontSize = fontSize,
                glowColor = Color.Transparent,
                insetBorderColor = Color.Transparent
            )

            value == 4 -> AisolverTileAppearance(
                background = Color(0xFFEDE0C8),
                contentColor = PrimaryText,
                fontSize = fontSize,
                glowColor = Color.Transparent,
                insetBorderColor = Color.Transparent
            )

            value == 8 -> brightTile(Color(0xFFF2B179), fontSize, Color.Transparent, Color.Transparent)
            value == 16 -> brightTile(Color(0xFFF59563), fontSize, Color.Transparent, Color.Transparent)
            value == 32 -> brightTile(Color(0xFFF67C5F), fontSize, Color.Transparent, Color.Transparent)
            value == 64 -> brightTile(Color(0xFFF65E3B), fontSize, Color.Transparent, Color.Transparent)
            value == 128 -> brightTile(Color(0xFFEDCF72), fontSize, Color(0x3CF3D774), Color(0x24FFFFFF))
            value == 256 -> brightTile(Color(0xFFEDCC61), fontSize, Color(0x51F3D774), Color(0x30FFFFFF))
            value == 512 -> brightTile(Color(0xFFEDC850), fontSize, Color(0x65F3D774), Color(0x3CFFFFFF))
            value == 1024 -> brightTile(Color(0xFFEDC53F), fontSize, Color(0x79F3D774), Color(0x49FFFFFF))
            value == 2048 -> brightTile(Color(0xFFEDC22E), fontSize, Color(0x8EF3D774), Color(0x55FFFFFF))
            else -> brightTile(Color(0xFF3C3A32), fontSize, Color.Transparent, Color.Transparent)
        }
    }

    private fun brightTile(
        background: Color,
        fontSize: TextUnit,
        glowColor: Color,
        insetBorderColor: Color,
    ): AisolverTileAppearance = AisolverTileAppearance(
        background = background,
        contentColor = BrightText,
        fontSize = fontSize,
        glowColor = glowColor,
        insetBorderColor = insetBorderColor
    )
}
