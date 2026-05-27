package compose

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.RRect
import org.jetbrains.skia.Rect

// ==========================================
// 🎨 DESIGN TOKENS COLOR DEFINITIONS (PRIMITIVES)
// ==========================================

object UIColors {
    val BlackOled = Color(0xFF121212)
    val WhitePure = Color(0xFFFFFFFF)

    object Gray {
        val G50 = Color(0xFFFAFAFA)
        val G100 = Color(0xFFF5F5F5)
        val G200 = Color(0xFFEEEEEE)
        val G700 = Color(0xFF616161)
        val G800 = Color(0xFF424242)
        val G900 = Color(0xFF212121)
    }

    object Navy {
        val N700 = Color(0xFF1E2D3F)
        val N800 = Color(0xFF152131)
        val N900 = Color(0xFF0E1722)
    }

    object Teal {
        val T50 = Color(0xFFEAFFFC)
        val T100 = Color(0xFFC8FFF8)
        val T500 = Color(0xFF0FF7E5)
        val T700 = Color(0xFF00CDBD)
        val T900 = Color(0xFF00756D)
    }

    object Orange {
        val O500 = Color(0xFFFF6D00)
        val O700 = Color(0xFFE65100)
        val O900 = Color(0xFFBF360C)
    }

    object Error {
        val Light = Color(0xFFEF5350)
        val Dark = Color(0xFFCF6679)
    }
}

// ==========================================
// 🎭 SEMANTIC STRUCTS & CUSTOM SYSTEMS
// ==========================================

data class QuantumColors(
    val bgBase: Color,
    val bgSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val componentWrappedBg: Color,
    val componentWrappedText: Color,
    val actionPrimary: Color,
    val actionPrimaryHover: Color,
    val textOnAction: Color,
    val systemError: Color,
    val isLight: Boolean
)

data class ShadowStyle(
    val color1: Color,
    val radius1: Dp,
    val dx1: Dp,
    val dy1: Dp,
    val color2: Color = Color.Transparent,
    val radius2: Dp = 0.dp,
    val dx2: Dp = 0.dp,
    val dy2: Dp = 0.dp
)

data class QuantumEffects(
    val panelBlur: Dp,
    val panelOpacity: Float,
    val glowShadow: ShadowStyle,
    val surfaceShadow: ShadowStyle,
    val softShadow: ShadowStyle,
    val transitionQuick: Int,      // ms
    val transitionNormal: Int,     // ms
    val transitionSlow: Int,       // ms
    val easeCurve: Easing
)

data class QuantumTypography(
    val fontName: String = "System",
    val heading1: TextStyle,
    val heading2: TextStyle,
    val heading3: TextStyle,
    val heading4: TextStyle,
    val bodyPrimary: TextStyle,
    val bodySecondary: TextStyle,
    val caption: TextStyle
)

// ==========================================
// 🌗 LIGHT & DARK THEME SPECIFICATIONS
// ==========================================

val lightQuantumColors = QuantumColors(
    bgBase = UIColors.Gray.G50,
    bgSurface = UIColors.WhitePure,
    textPrimary = UIColors.Gray.G900,
    textSecondary = UIColors.Gray.G700,
    componentWrappedBg = UIColors.Teal.T100,
    componentWrappedText = UIColors.Teal.T900,
    actionPrimary = UIColors.Orange.O700,
    actionPrimaryHover = UIColors.Orange.O900,
    textOnAction = UIColors.WhitePure,
    systemError = UIColors.Error.Light,
    isLight = true
)

val darkQuantumColors = QuantumColors(
    bgBase = UIColors.Navy.N900,
    bgSurface = UIColors.Navy.N800,
    textPrimary = UIColors.Gray.G50,
    textSecondary = UIColors.Gray.G200,
    componentWrappedBg = UIColors.Teal.T700,
    componentWrappedText = UIColors.Teal.T50,
    actionPrimary = UIColors.Orange.O500,
    actionPrimaryHover = UIColors.Orange.O700,
    textOnAction = UIColors.Gray.G900,
    systemError = UIColors.Error.Dark,
    isLight = false
)

val lightQuantumEffects = QuantumEffects(
    panelBlur = 18.dp,
    panelOpacity = 0.84f,
    glowShadow = ShadowStyle(
        color1 = UIColors.Teal.T500.copy(alpha = 0.24f), radius1 = 26.dp, dx1 = 0.dp, dy1 = 10.dp,
        color2 = UIColors.Teal.T900.copy(alpha = 0.12f), radius2 = 12.dp, dx2 = 0.dp, dy2 = 4.dp
    ),
    surfaceShadow = ShadowStyle(
        color1 = UIColors.Teal.T900.copy(alpha = 0.16f), radius1 = 30.dp, dx1 = 0.dp, dy1 = 14.dp,
        color2 = UIColors.WhitePure.copy(alpha = 0.92f), radius2 = 0.dp, dx2 = 0.dp, dy2 = 1.dp
    ),
    softShadow = ShadowStyle(
        color1 = UIColors.Teal.T900.copy(alpha = 0.12f), radius1 = 18.dp, dx1 = 0.dp, dy1 = 8.dp
    ),
    transitionQuick = 180,
    transitionNormal = 300,
    transitionSlow = 460,
    easeCurve = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
)

val darkQuantumEffects = QuantumEffects(
    panelBlur = 22.dp,
    panelOpacity = 0.72f,
    glowShadow = ShadowStyle(
        color1 = UIColors.Orange.O500.copy(alpha = 0.34f), radius1 = 38.dp, dx1 = 0.dp, dy1 = 14.dp,
        color2 = UIColors.Teal.T900.copy(alpha = 0.24f), radius2 = 14.dp, dx2 = 0.dp, dy2 = 4.dp
    ),
    surfaceShadow = ShadowStyle(
        color1 = Color(0xFF040C18).copy(alpha = 0.52f), radius1 = 40.dp, dx1 = 0.dp, dy1 = 18.dp,
        color2 = UIColors.WhitePure.copy(alpha = 0.06f), radius2 = 0.dp, dx2 = 0.dp, dy2 = 1.dp
    ),
    softShadow = ShadowStyle(
        color1 = Color(0xFF081626).copy(alpha = 0.38f), radius1 = 24.dp, dx1 = 0.dp, dy1 = 10.dp
    ),
    transitionQuick = 180,
    transitionNormal = 300,
    transitionSlow = 460,
    easeCurve = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
)

val quantumTypography = QuantumTypography(
    heading1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp
    ),
    heading2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    heading3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    heading4 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    bodyPrimary = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodySecondary = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

// ==========================================
// 📦 UNIVERSAL DESIGN SYSTEM OBJECT
// ==========================================

object UITokens {
    object Layout {
        val spacingCompact = 8.dp
        val spacingNormal = 16.dp
        val spacingLoose = 24.dp

        val radiusControl = 10.dp
        val radiusContainer = 15.dp
        val radiusFull = 9999.dp
    }

    val colors: QuantumColors
        @Composable
        @ReadOnlyComposable
        get() = LocalQuantumColors.current

    val effects: QuantumEffects
        @Composable
        @ReadOnlyComposable
        get() = LocalQuantumEffects.current

    val typography: QuantumTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalQuantumTypography.current
}

val LocalQuantumColors = compositionLocalOf { lightQuantumColors }
val LocalQuantumEffects = compositionLocalOf { lightQuantumEffects }
val LocalQuantumTypography = compositionLocalOf { quantumTypography }

@Composable
fun QuantumTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkQuantumColors else lightQuantumColors
    val effects = if (darkTheme) darkQuantumEffects else lightQuantumEffects

    CompositionLocalProvider(
        LocalQuantumColors provides colors,
        LocalQuantumEffects provides effects,
        LocalQuantumTypography provides quantumTypography,
        content = content
    )
}

// ==========================================
// 🛠️ SHADOW DRAWING COMPOSABLE UTILITIES
// ==========================================

fun Modifier.uiShadow(
    shadow: ShadowStyle,
    borderRadius: Dp = 0.dp
): Modifier = this.drawBehind {
    if (shadow.color1 == Color.Transparent && shadow.color2 == Color.Transparent) {
        return@drawBehind
    }

    drawIntoCanvas { canvas ->
        val drawShadowLayer = { col: Color, rad: Dp, dx: Dp, dy: Dp ->
            if (col != Color.Transparent && rad > 0.dp) {
                val sigma = rad.toPx() / 2f
                if (sigma > 0f) {
                    val paint = Paint().apply {
                        color = col.toArgb()
                        maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, sigma)
                    }
                    val nativeCanvas = canvas.nativeCanvas
                    val offsetLeft = dx.toPx()
                    val offsetTop = dy.toPx()

                    if (borderRadius >= 9999.dp) {
                        val r = size.minDimension / 2f
                        nativeCanvas.drawCircle(
                            size.width / 2f + offsetLeft,
                            size.height / 2f + offsetTop,
                            r,
                            paint
                        )
                    } else {
                        val rect = Rect.makeXYWH(
                            offsetLeft,
                            offsetTop,
                            size.width,
                            size.height
                        )
                        if (borderRadius > 0.dp) {
                            val rrect = RRect.makeXYWH(
                                offsetLeft,
                                offsetTop,
                                size.width,
                                size.height,
                                borderRadius.toPx()
                            )
                            nativeCanvas.drawRRect(rrect, paint)
                        } else {
                            nativeCanvas.drawRect(rect, paint)
                        }
                    }
                }
            }
        }

        // Layer both shadows sequentially
        drawShadowLayer(shadow.color1, shadow.radius1, shadow.dx1, shadow.dy1)
        drawShadowLayer(shadow.color2, shadow.radius2, shadow.dx2, shadow.dy2)
    }
}

// ==========================================
// 🌌 MESH BACKGROUND WITH OVERLAPPING RADIALS
// ==========================================

@Composable
fun QuantumBackground(modifier: Modifier = Modifier) {
    val colors = UITokens.colors
    val isLight = colors.isLight

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // 1. Draw base linear background gradient
                val baseGradient = if (!isLight) {
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0D151F), Color(0xFF0E1722))
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFAFBFB), Color(0xFFFAFAFA))
                    )
                }
                drawRect(brush = baseGradient)

                // 2. Draw overlapping vibrant radial glows
                val width = size.width
                val height = size.height
                val maxLen = maxOf(width, height)

                // Top-right teal glow
                val trColor = colors.componentWrappedBg.copy(alpha = if (!isLight) 0.32f else 0.54f)
                val trBrush = Brush.radialGradient(
                    colors = listOf(trColor, Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(width, 0f),
                    radius = maxLen * (if (!isLight) 0.46f else 0.48f)
                )
                drawRect(brush = trBrush)

                // Left middle-top teal glow
                val lmtColor = colors.componentWrappedBg.copy(alpha = if (!isLight) 0.24f else 0.42f)
                val lmtBrush = Brush.radialGradient(
                    colors = listOf(lmtColor, Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(0f, height * 0.25f),
                    radius = maxLen * (if (!isLight) 0.42f else 0.40f)
                )
                drawRect(brush = lmtBrush)

                // Bottom-right orange/action glow
                val brColor = colors.actionPrimary.copy(alpha = if (!isLight) 0.18f else 0.14f)
                val brBrush = Brush.radialGradient(
                    colors = listOf(brColor, Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(width * 0.88f, height * 0.92f),
                    radius = maxLen * (if (!isLight) 0.36f else 0.32f)
                )
                drawRect(brush = brBrush)
            }
    )
}
