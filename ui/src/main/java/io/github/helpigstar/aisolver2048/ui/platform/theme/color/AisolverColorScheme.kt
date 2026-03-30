package io.github.helpigstar.aisolver2048.ui.platform.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Defines all the colors for the app.
 */
@Immutable
data class AisolverColorScheme(
    val text: TextColors,
    val background: BackgroundColors,
    val stroke: StrokeColors,
    val button: ButtonColors,
    val board: BoardColors,
    val tile: TileColors,
) {
    /**
     * Defines all the text colors for the app.
     */
    @Immutable
    data class TextColors(
        val primary: Color,
        val secondary: Color,
        val tertiary: Color,
        val inverse: Color,
        val boardPrimary: Color,
        val boardInverse: Color,
        val scoreAddition: Color,
    )

    /**
     * Defines all the background colors for the app.
     */
    @Immutable
    data class BackgroundColors(
        val primary: Color,
        val secondary: Color,
        val tertiary: Color,
        val utility: Color,
        val scrim: Color,
    )

    /**
     * Defines all the stroke colors for the app.
     */
    @Immutable
    data class StrokeColors(
        val divider: Color,
        val border: Color,
        val selection: Color,
    )

    /**
     * Defines all the button colors for the app.
     */
    @Immutable
    data class ButtonColors(
        val primaryBackground: Color,
        val primaryForeground: Color,
        val primaryBackgroundDisabled: Color,
        val primaryForegroundDisabled: Color,
        val autoAnalyzeBackground: Color,
        val autoAnalyzeForeground: Color,
        val stopBackground: Color,
        val stopForeground: Color,
        val utilityBackground: Color,
        val utilityForeground: Color,
        val utilityBackgroundDisabled: Color,
        val utilityForegroundDisabled: Color,
        val gameBackground: Color,
        val gameForeground: Color,
        val gameBackgroundDisabled: Color,
        val gameForegroundDisabled: Color,
    )

    /**
     * Defines all the board and overlay colors for the app.
     */
    @Immutable
    data class BoardColors(
        val background: Color,
        val cell: Color,
        val cellSelected: Color,
        val emptyTile: Color,
        val overlay: Color,
        val winOverlay: Color,
    )

    /**
     * Defines all the numbered tile colors for the app.
     */
    @Immutable
    data class TileColors(
        val value2: Color,
        val value4: Color,
        val value8: Color,
        val value16: Color,
        val value32: Color,
        val value64: Color,
        val value128: Color,
        val value256: Color,
        val value512: Color,
        val value1024: Color,
        val value2048: Color,
        val valueSuper: Color,
        val textDark: Color,
        val textLight: Color,
        val glow: Color,
    )
}
