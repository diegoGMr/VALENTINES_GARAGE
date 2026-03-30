package com.msn.valentinesgarage.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppColors.Orange,
    onPrimary = AppColors.FontBlackStrong,
    secondary = AppColors.Pink,
    onSecondary = AppColors.FontBlackStrong,
    tertiary = AppColors.Green,
    background = AppColors.LightGray,
    onBackground = AppColors.FontBlackMedium,
    surface = AppColors.LightGray,
    onSurface = AppColors.FontBlackMedium,
    error = AppColors.Red,
)

private val DarkColors = darkColorScheme(
    primary = AppColors.Orange,
    onPrimary = AppColors.FontBlackStrong,
    secondary = AppColors.Mint,
    onSecondary = AppColors.FontBlackStrong,
    tertiary = AppColors.Green,
    background = AppColors.NearBlack,
    onBackground = AppColors.LightGray,
    surface = AppColors.NearBlack,
    onSurface = AppColors.LightGray,
    error = AppColors.Red,
)

@Composable
fun ValentinesGarageTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}


