package com.ifood.challenge.movies.core.designsystem.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation that renders the same Composable in light and dark themes.
 * Apply alongside `IfoodMoviesTheme(darkTheme = isSystemInDarkTheme())` for automatic switching.
 */
@Preview(name = "Light", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewThemes
