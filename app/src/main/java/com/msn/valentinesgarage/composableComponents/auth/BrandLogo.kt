package com.msn.valentinesgarage.composableComponents.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun BrandLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp, 44.dp)) {
        drawLightningBolts(this, size)
    }
}

private fun drawLightningBolts(scope: DrawScope, canvasSize: Size) {
    val w = canvasSize.width
    val h = canvasSize.height

    val leftBolt = Path().apply {
        moveTo(w * 0.10f, h * 0.00f)
        lineTo(w * 0.38f, h * 0.00f)
        lineTo(w * 0.24f, h * 0.47f)
        lineTo(w * 0.40f, h * 0.47f)
        lineTo(w * 0.14f, h * 1.00f)
        lineTo(w * 0.24f, h * 0.60f)
        lineTo(w * 0.06f, h * 0.60f)
        close()
    }

    val rightBolt = Path().apply {
        moveTo(w * 0.42f, h * 0.00f)
        lineTo(w * 0.72f, h * 0.00f)
        lineTo(w * 0.56f, h * 0.50f)
        lineTo(w * 0.74f, h * 0.50f)
        lineTo(w * 0.44f, h * 1.00f)
        lineTo(w * 0.56f, h * 0.63f)
        lineTo(w * 0.38f, h * 0.63f)
        close()
    }

    scope.drawPath(leftBolt, color = AppColors.Orange)
    scope.drawPath(rightBolt, color = AppColors.Orange)
}

@Preview(showBackground = true)
@Composable
fun PreviewBrandLogo() {
    BrandLogo()
}

