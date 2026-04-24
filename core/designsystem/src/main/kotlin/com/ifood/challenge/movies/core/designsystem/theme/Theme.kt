package com.ifood.challenge.movies.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val SeedDarkColors =
    darkColorScheme(
        primary = SeedPrimary,
        onPrimary = SeedOnPrimary,
        primaryContainer = SeedPrimaryContainer,
        onPrimaryContainer = SeedOnPrimaryContainer,
        secondary = SeedSecondary,
        onSecondary = SeedOnSecondary,
        secondaryContainer = SeedSecondaryContainer,
        onSecondaryContainer = SeedOnSecondaryContainer,
        tertiary = SeedTertiary,
        onTertiary = SeedOnTertiary,
        tertiaryContainer = SeedTertiaryContainer,
        onTertiaryContainer = SeedOnTertiaryContainer,
        error = SeedError,
        onError = SeedOnError,
        errorContainer = SeedErrorContainer,
        onErrorContainer = SeedOnErrorContainer,
        background = SeedSurfaceDark,
        onBackground = SeedOnSurface,
        surface = SeedSurfaceDark,
        onSurface = SeedOnSurface,
        surfaceVariant = SeedSurfaceContainerHigh,
        onSurfaceVariant = SeedOnSurfaceVariant,
        surfaceContainerLowest = SeedSurfaceContainerLowest,
        surfaceContainerLow = SeedSurfaceContainerLow,
        surfaceContainer = SeedSurfaceContainer,
        surfaceContainerHigh = SeedSurfaceContainerHigh,
        surfaceContainerHighest = SeedSurfaceContainerHighest,
        outline = SeedOutline,
        outlineVariant = SeedOutlineVariant,
        inverseSurface = SeedInverseSurface,
        inverseOnSurface = SeedInverseOnSurface,
        inversePrimary = SeedInversePrimary,
        scrim = SeedScrim,
    )

private val SeedLightColors =
    lightColorScheme(
        primary = SeedPrimaryLight,
        onPrimary = SeedOnPrimaryLight,
        primaryContainer = SeedPrimaryContainerLight,
        onPrimaryContainer = SeedOnPrimaryContainerLight,
        secondary = SeedSecondaryLight,
        onSecondary = SeedOnSecondaryLight,
        secondaryContainer = SeedSecondaryContainerLight,
        onSecondaryContainer = SeedOnSecondaryContainerLight,
        background = SeedSurfaceLight,
        onBackground = SeedOnSurfaceLight,
        surface = SeedSurfaceLight,
        onSurface = SeedOnSurfaceLight,
        surfaceVariant = SeedSurfaceContainerHighLight,
        onSurfaceVariant = SeedOnSurfaceVariantLight,
        surfaceContainerLowest = SeedSurfaceContainerLowestLight,
        surfaceContainerLow = SeedSurfaceContainerLowLight,
        surfaceContainer = SeedSurfaceContainerLight,
        surfaceContainerHigh = SeedSurfaceContainerHighLight,
        surfaceContainerHighest = SeedSurfaceContainerHighestLight,
        outline = SeedOutlineLight,
        outlineVariant = SeedOutlineVariantLight,
    )

@Composable
fun IfoodMoviesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> SeedDarkColors
            else -> SeedLightColors
        }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = IfoodTypography,
            shapes = IfoodShapes,
            content = content,
        )
    }
}
