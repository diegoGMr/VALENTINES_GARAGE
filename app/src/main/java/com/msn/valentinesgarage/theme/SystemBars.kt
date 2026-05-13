package com.msn.valentinesgarage.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun Modifier.topSafeDrawingPadding(): Modifier =
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))

@Composable
fun ConfigureSystemBars(
    statusBarColor: Color,
    darkStatusBarIcons: Boolean = statusBarColor.prefersDarkSystemBarIcons(),
    navigationBarColor: Color = statusBarColor,
    darkNavigationBarIcons: Boolean = navigationBarColor.prefersDarkSystemBarIcons(),
) {
    val view = LocalView.current

    if (view.isInEditMode) {
        return
    }

    SideEffect {
        val activity = view.context.findActivity() ?: return@SideEffect
        val window = activity.window
        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = darkStatusBarIcons
            isAppearanceLightNavigationBars = darkNavigationBarIcons
        }
    }
}

private fun Color.prefersDarkSystemBarIcons(): Boolean = luminance() > 0.5f

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


