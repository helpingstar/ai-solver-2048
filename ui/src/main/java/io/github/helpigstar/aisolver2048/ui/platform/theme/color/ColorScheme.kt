package io.github.helpigstar.aisolver2048.ui.platform.theme.color

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

private val defaultBoardColors: AisolverColorScheme.BoardColors =
    AisolverColorScheme.BoardColors(
        background = PrimitiveColors.boardSurface,
        cell = PrimitiveColors.boardCellFill,
        cellSelected = PrimitiveColors.boardSelection,
        emptyTile = PrimitiveColors.boardBaseTile,
        overlay = PrimitiveColors.boardOverlay,
        winOverlay = PrimitiveColors.boardWinOverlay,
    )

private val defaultTileColors: AisolverColorScheme.TileColors =
    AisolverColorScheme.TileColors(
        value2 = PrimitiveColors.tile2,
        value4 = PrimitiveColors.tile4,
        value8 = PrimitiveColors.tile8,
        value16 = PrimitiveColors.tile16,
        value32 = PrimitiveColors.tile32,
        value64 = PrimitiveColors.tile64,
        value128 = PrimitiveColors.tile128,
        value256 = PrimitiveColors.tile256,
        value512 = PrimitiveColors.tile512,
        value1024 = PrimitiveColors.tile1024,
        value2048 = PrimitiveColors.tile2048,
        valueSuper = PrimitiveColors.tileSuper,
        textDark = PrimitiveColors.boardText,
        textLight = PrimitiveColors.boardTextInverse,
        glow = PrimitiveColors.boardGlow,
    )

/**
 * The default [AisolverColorScheme].
 */
val defaultAisolverColorScheme: AisolverColorScheme = AisolverColorScheme(
    text = AisolverColorScheme.TextColors(
        primary = PrimitiveColors.gray900,
        secondary = PrimitiveColors.gray700,
        tertiary = PrimitiveColors.gray500,
        inverse = PrimitiveColors.white,
        boardPrimary = PrimitiveColors.boardText,
        boardInverse = PrimitiveColors.boardTextInverse,
        scoreAddition = PrimitiveColors.boardScoreAddition,
    ),
    background = AisolverColorScheme.BackgroundColors(
        primary = PrimitiveColors.white,
        secondary = PrimitiveColors.white,
        tertiary = PrimitiveColors.gray50,
        utility = PrimitiveColors.gray100,
        scrim = PrimitiveColors.black.copy(alpha = 0.4f),
    ),
    stroke = AisolverColorScheme.StrokeColors(
        divider = PrimitiveColors.gray100,
        border = PrimitiveColors.gray200,
        selection = PrimitiveColors.boardGold,
    ),
    button = AisolverColorScheme.ButtonColors(
        primaryBackground = PrimitiveColors.blue500,
        primaryForeground = PrimitiveColors.white,
        primaryBackgroundDisabled = PrimitiveColors.gray200,
        primaryForegroundDisabled = PrimitiveColors.gray500,
        utilityBackground = PrimitiveColors.gray100,
        utilityForeground = PrimitiveColors.gray900,
        utilityBackgroundDisabled = PrimitiveColors.gray50,
        utilityForegroundDisabled = PrimitiveColors.gray500,
        gameBackground = PrimitiveColors.boardActionBrown,
        gameForeground = PrimitiveColors.boardTextInverse,
        gameBackgroundDisabled = PrimitiveColors.boardActionBrown.copy(alpha = 0.5f),
        gameForegroundDisabled = PrimitiveColors.boardTextInverse.copy(alpha = 0.6f),
    ),
    board = defaultBoardColors,
    tile = defaultTileColors,
)

/**
 * Creates an [AisolverColorScheme] based on dynamic Material You colors.
 */
@Suppress("LongMethod")
fun dynamicAisolverColorScheme(
    materialColorScheme: ColorScheme,
): AisolverColorScheme {
    return AisolverColorScheme(
        text = AisolverColorScheme.TextColors(
            primary = materialColorScheme.onBackground,
            secondary = materialColorScheme.onSurface,
            tertiary = materialColorScheme.onSurfaceVariant,
            inverse = materialColorScheme.onPrimary,
            boardPrimary = defaultAisolverColorScheme.text.boardPrimary,
            boardInverse = defaultAisolverColorScheme.text.boardInverse,
            scoreAddition = defaultAisolverColorScheme.text.scoreAddition,
        ),
        background = AisolverColorScheme.BackgroundColors(
            primary = materialColorScheme.background,
            secondary = materialColorScheme.surface,
            tertiary = materialColorScheme.surfaceContainerLow,
            utility = materialColorScheme.surfaceContainerHighest,
            scrim = materialColorScheme.scrim.copy(alpha = 0.4f),
        ),
        stroke = AisolverColorScheme.StrokeColors(
            divider = materialColorScheme.outlineVariant,
            border = materialColorScheme.outline,
            selection = defaultAisolverColorScheme.stroke.selection,
        ),
        button = AisolverColorScheme.ButtonColors(
            primaryBackground = materialColorScheme.primary,
            primaryForeground = materialColorScheme.onPrimary,
            primaryBackgroundDisabled = materialColorScheme.onSurface.copy(alpha = 0.12f),
            primaryForegroundDisabled = materialColorScheme.onSurface.copy(alpha = 0.38f),
            utilityBackground = materialColorScheme.surfaceContainerHighest,
            utilityForeground = materialColorScheme.onSurface,
            utilityBackgroundDisabled = materialColorScheme.surfaceContainer,
            utilityForegroundDisabled = materialColorScheme.onSurfaceVariant,
            gameBackground = defaultAisolverColorScheme.button.gameBackground,
            gameForeground = defaultAisolverColorScheme.button.gameForeground,
            gameBackgroundDisabled = defaultAisolverColorScheme.button.gameBackgroundDisabled,
            gameForegroundDisabled = defaultAisolverColorScheme.button.gameForegroundDisabled,
        ),
        board = defaultAisolverColorScheme.board,
        tile = defaultAisolverColorScheme.tile,
    )
}

/**
 * Derives a Material [ColorScheme] from the [AisolverColorScheme] using the [defaultColorScheme]
 * as a baseline.
 */
fun AisolverColorScheme.toMaterialColorScheme(
    defaultColorScheme: ColorScheme,
): ColorScheme = defaultColorScheme.copy(
    primary = this.button.primaryBackground,
    onPrimary = this.button.primaryForeground,
    secondary = this.stroke.selection,
    onSecondary = this.text.boardInverse,
    tertiary = this.board.background,
    onTertiary = this.text.boardInverse,
    background = this.background.primary,
    onBackground = this.text.primary,
    surface = this.background.secondary,
    onSurface = this.text.primary,
    surfaceVariant = this.background.tertiary,
    onSurfaceVariant = this.text.secondary,
    primaryContainer = this.background.utility,
    onPrimaryContainer = this.text.primary,
    secondaryContainer = this.board.cellSelected,
    onSecondaryContainer = this.text.boardPrimary,
    tertiaryContainer = this.board.emptyTile,
    onTertiaryContainer = this.text.boardPrimary,
    outline = this.stroke.border,
    outlineVariant = this.stroke.divider,
    scrim = this.background.scrim,
    error = this.tile.value64,
    onError = this.tile.textLight,
)

/**
 * The raw colors that support the [AisolverColorScheme].
 */
private data object PrimitiveColors {
    val white: Color = Color(color = 0xFFFFFFFF)
    val black: Color = Color(color = 0xFF000000)

    val gray50: Color = Color(color = 0xFFF9FAFB)
    val gray100: Color = Color(color = 0xFFF3F4F6)
    val gray200: Color = Color(color = 0xFFE5E7EB)
    val gray500: Color = Color(color = 0xFF6A7282)
    val gray700: Color = Color(color = 0xFF364153)
    val gray900: Color = Color(color = 0xFF101828)

    val blue500: Color = Color(color = 0xFF155DFC)

    val boardText: Color = Color(color = 0xFF776E65)
    val boardTextInverse: Color = Color(color = 0xFFF9F6F2)
    val boardBaseTile: Color = Color(color = 0xFFEEE4DA)
    val boardSurface: Color = Color(color = 0xFFBBADA0)
    val boardActionBrown: Color = Color(color = 0xFF8F7A66)
    val boardGold: Color = Color(color = 0xFFEDC22E)
    val boardGlow: Color = Color(color = 0xFFF3D774)
    val boardCellFill: Color = Color(color = 0x59EEE4DA)
    val boardOverlay: Color = Color(color = 0x80EEE4DA)
    val boardWinOverlay: Color = Color(color = 0x80EDC22E)
    val boardSelection: Color = Color(color = 0x33EDC22E)
    val boardScoreAddition: Color = Color(color = 0xE6776E65)

    val tile2: Color = Color(color = 0xFFEEE4DA)
    val tile4: Color = Color(color = 0xFFEDE0C8)
    val tile8: Color = Color(color = 0xFFF2B179)
    val tile16: Color = Color(color = 0xFFF59563)
    val tile32: Color = Color(color = 0xFFF67C5F)
    val tile64: Color = Color(color = 0xFFF65E3B)
    val tile128: Color = Color(color = 0xFFEDCF72)
    val tile256: Color = Color(color = 0xFFEDCC61)
    val tile512: Color = Color(color = 0xFFEDC850)
    val tile1024: Color = Color(color = 0xFFEDC53F)
    val tile2048: Color = Color(color = 0xFFEDC22E)
    val tileSuper: Color = Color(color = 0xFF3C3A32)
}
