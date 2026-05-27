package compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// 📏 VALUE RESOLVERS FOR SPACING & ROUNDING
// ==========================================

sealed class SpaceValue {
    object None : SpaceValue()
    object Compact : SpaceValue()
    object Normal : SpaceValue()
    object Loose : SpaceValue()
    data class Custom(val value: Dp) : SpaceValue()

    val dp: Dp
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            None -> 0.dp
            Compact -> UITokens.Layout.spacingCompact
            Normal -> UITokens.Layout.spacingNormal
            Loose -> UITokens.Layout.spacingLoose
            is Custom -> value
        }
}

sealed class RadiusValue {
    object None : RadiusValue()
    object Control : RadiusValue()
    object Container : RadiusValue()
    object Full : RadiusValue()
    data class Custom(val value: Dp) : RadiusValue()

    val dp: Dp
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            None -> 0.dp
            Control -> UITokens.Layout.radiusControl
            Container -> UITokens.Layout.radiusContainer
            Full -> UITokens.Layout.radiusFull
            is Custom -> value
        }
}

// ==========================================
// 🧱 LAYOUT & STRUCTURE PRIMITIVES
// ==========================================

@Composable
fun UIBox(
    modifier: Modifier = Modifier,
    padding: SpaceValue = SpaceValue.None,
    radius: RadiusValue = RadiusValue.None,
    surface: Boolean = false,
    wrapped: Boolean = false,
    blurPanel: Boolean = false,
    border: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = UITokens.colors
    val effects = UITokens.effects
    val radiusDp = radius.dp
    val paddingDp = padding.dp

    val contentColor = when {
        wrapped -> colors.componentWrappedText
        surface -> colors.textPrimary
        else -> colors.textPrimary
    }

    val shape = RoundedCornerShape(radiusDp)

    Box(
        modifier = modifier
            .clip(shape)
    ) {
        // 1. Layered Background Panel (Blurred if blurPanel is true)
        if (wrapped) {
            Box(modifier = Modifier.matchParentSize().background(colors.componentWrappedBg))
        } else if (blurPanel) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(colors.bgSurface.copy(alpha = effects.panelOpacity))
                    .blur(effects.panelBlur)
            )
        } else if (surface) {
            Box(modifier = Modifier.matchParentSize().background(colors.bgSurface))
        }

        // 2. Border Outline Layer
        if (border) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.dp, colors.textSecondary.copy(alpha = 0.18f), shape)
            )
        }

        // 3. Crisp Content Layer on Top
        Box(
            modifier = Modifier.padding(paddingDp)
        ) {
            CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides contentColor,
                content = content
            )
        }
    }
}

@Composable
fun UIHStack(
    modifier: Modifier = Modifier,
    gap: SpaceValue = SpaceValue.Normal,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap.dp),
        verticalAlignment = verticalAlignment,
        content = content
    )
}

@Composable
fun UIVStack(
    modifier: Modifier = Modifier,
    gap: SpaceValue = SpaceValue.Normal,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(gap.dp),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
fun UIZStack(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = alignment,
        content = content
    )
}

@Composable
fun UIGrid(
    modifier: Modifier = Modifier,
    columns: Int? = null,
    minItemWidth: Dp = 220.dp,
    gap: SpaceValue = SpaceValue.Normal,
    items: List<@Composable () -> Unit>
) {
    // Flow layout builder to avoid nested vertical scrolling crashes
    BoxWithConstraints(modifier = modifier) {
        val containerWidth = maxWidth
        val cols = columns ?: maxOf(1, (containerWidth / minItemWidth).toInt())
        val itemGap = gap.dp

        val rows = items.chunked(cols)
        Column(verticalArrangement = Arrangement.spacedBy(itemGap)) {
            for (rowItems in rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(itemGap)) {
                    for (item in rowItems) {
                        Box(modifier = Modifier.weight(1f)) {
                            item()
                        }
                    }
                    if (rowItems.size < cols) {
                        for (i in 0 until (cols - rowItems.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UIScrollView(
    modifier: Modifier = Modifier,
    maxHeight: Dp? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    val scrollModifier = Modifier.verticalScroll(scrollState)
    val heightModifier = if (maxHeight != null) Modifier.heightIn(max = maxHeight) else Modifier

    Column(
        modifier = modifier
            .then(heightModifier)
            .then(scrollModifier),
        content = content
    )
}

@Composable
fun UISpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier)
}

@Composable
fun ColumnScope.UISpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(1f))
}

@Composable
fun RowScope.UISpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(1f))
}

@Composable
fun UIDivider(
    modifier: Modifier = Modifier,
    vertical: Boolean = false
) {
    val colors = UITokens.colors
    if (vertical) {
        Box(
            modifier = modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(colors.componentWrappedBg.copy(alpha = 0.38f))
        )
    } else {
        Box(
            modifier = modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(colors.componentWrappedBg.copy(alpha = 0.35f))
        )
    }
}

// ==========================================
// 📝 TYPOGRAPHY & CONTENT PRIMITIVES
// ==========================================

enum class UITextTone {
    Primary, Secondary, Wrapped, Action, Error
}

@Composable
@ReadOnlyComposable
fun UITextTone.resolveColor(): Color {
    val colors = UITokens.colors
    return when (this) {
        UITextTone.Primary -> colors.textPrimary
        UITextTone.Secondary -> colors.textSecondary
        UITextTone.Wrapped -> colors.componentWrappedText
        UITextTone.Action -> colors.actionPrimary
        UITextTone.Error -> colors.systemError
    }
}

@Composable
fun UIText(
    text: String,
    modifier: Modifier = Modifier,
    tone: UITextTone = UITextTone.Primary,
    fontWeight: FontWeight = FontWeight.Medium,
    style: TextStyle = UITokens.typography.bodyPrimary,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        color = tone.resolveColor(),
        fontWeight = fontWeight,
        style = style,
        maxLines = maxLines,
        overflow = if (maxLines < Int.MAX_VALUE) TextOverflow.Ellipsis else TextOverflow.Clip
    )
}

@Composable
fun UIHeading(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 2,
    tone: UITextTone = UITextTone.Primary
) {
    val typography = UITokens.typography
    val textStyle = when (level) {
        1 -> typography.heading1
        2 -> typography.heading2
        3 -> typography.heading3
        4 -> typography.heading4
        else -> typography.heading2
    }
    Text(
        text = text,
        modifier = modifier,
        color = tone.resolveColor(),
        style = textStyle
    )
}

@Composable
fun UIImage(
    name: String,
    modifier: Modifier = Modifier,
    radius: RadiusValue = RadiusValue.Container,
    contentScale: ContentScale = ContentScale.Crop
) {
    val colors = UITokens.colors
    val radiusDp = radius.dp
    val shape = RoundedCornerShape(radiusDp)

    // Render a high-fidelity visual mock-up gradient card as fallback placeholder
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.componentWrappedBg.copy(alpha = 0.8f),
                        colors.bgSurface
                    )
                )
            )
            .border(1.dp, colors.componentWrappedBg.copy(alpha = 0.4f), shape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UIIcon(
                name = UIIconName.Alert,
                size = 28.dp,
                tint = colors.componentWrappedText
            )
            Spacer(modifier = Modifier.height(8.dp))
            UIText(
                text = "Image: $name",
                tone = UITextTone.Wrapped,
                fontWeight = FontWeight.SemiBold,
                style = UITokens.typography.caption
            )
        }
    }
}

enum class UIIconName {
    Menu, Home, Search, Bell, User, Settings, Plus, Close, ChevronDown, Check, Alert
}

fun UIIconName.toVector(): ImageVector {
    return when (this) {
        UIIconName.Menu -> Icons.Default.Menu
        UIIconName.Home -> Icons.Default.Home
        UIIconName.Search -> Icons.Default.Search
        UIIconName.Bell -> Icons.Default.Notifications
        UIIconName.User -> Icons.Default.Person
        UIIconName.Settings -> Icons.Default.Settings
        UIIconName.Plus -> Icons.Default.Add
        UIIconName.Close -> Icons.Default.Close
        UIIconName.ChevronDown -> Icons.Default.KeyboardArrowDown
        UIIconName.Check -> Icons.Default.Check
        UIIconName.Alert -> Icons.Default.Warning
    }
}

@Composable
fun UIIcon(
    name: UIIconName,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    tint: Color = androidx.compose.material3.LocalContentColor.current
) {
    Icon(
        imageVector = name.toVector(),
        contentDescription = name.name,
        modifier = modifier.size(size),
        tint = tint
    )
}

@Composable
fun UIIcon(
    vector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    tint: Color = androidx.compose.material3.LocalContentColor.current
) {
    Icon(
        imageVector = vector,
        contentDescription = null,
        modifier = modifier.size(size),
        tint = tint
    )
}

@Composable
fun UIAvatar(
    name: String,
    modifier: Modifier = Modifier,
    src: String? = null,
    size: Dp = 40.dp
) {
    val colors = UITokens.colors
    val effects = UITokens.effects

    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull() }
        .joinToString("")
        .take(2)
        .uppercase()

    Box(
        modifier = modifier
            .size(size)
            .uiShadow(effects.softShadow, borderRadius = 9999.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.componentWrappedBg,
                        colors.componentWrappedBg.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        UIText(
            text = initials,
            tone = UITextTone.Wrapped,
            fontWeight = FontWeight.Bold,
            style = UITokens.typography.bodyPrimary.copy(fontSize = (size.value * 0.36f).sp)
        )
    }
}

// ==========================================
// 📦 UNIFIED COMPONENT NAMESPACE OBJECT
// ==========================================

object UI {
    @Composable
    fun Box(
        modifier: Modifier = Modifier,
        padding: SpaceValue = SpaceValue.None,
        radius: RadiusValue = RadiusValue.None,
        surface: Boolean = false,
        wrapped: Boolean = false,
        blurPanel: Boolean = false,
        border: Boolean = false,
        content: @Composable () -> Unit
    ) = UIBox(modifier, padding, radius, surface, wrapped, blurPanel, border, content)

    @Composable
    fun HStack(
        modifier: Modifier = Modifier,
        gap: SpaceValue = SpaceValue.Normal,
        verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
        content: @Composable RowScope.() -> Unit
    ) = UIHStack(modifier, gap, verticalAlignment, content)

    @Composable
    fun VStack(
        modifier: Modifier = Modifier,
        gap: SpaceValue = SpaceValue.Normal,
        horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        content: @Composable ColumnScope.() -> Unit
    ) = UIVStack(modifier, gap, horizontalAlignment, content)

    @Composable
    fun ZStack(
        modifier: Modifier = Modifier,
        alignment: Alignment = Alignment.Center,
        content: @Composable BoxScope.() -> Unit
    ) = UIZStack(modifier, alignment, content)

    @Composable
    fun Grid(
        modifier: Modifier = Modifier,
        columns: Int? = null,
        minItemWidth: Dp = 220.dp,
        gap: SpaceValue = SpaceValue.Normal,
        items: List<@Composable () -> Unit>
    ) = UIGrid(modifier, columns, minItemWidth, gap, items)

    @Composable
    fun ScrollView(
        modifier: Modifier = Modifier,
        maxHeight: Dp? = null,
        content: @Composable ColumnScope.() -> Unit
    ) = UIScrollView(modifier, maxHeight, content)

    @Composable
    fun Spacer(modifier: Modifier = Modifier) = UISpacer(modifier)

    @Composable
    fun Divider(
        modifier: Modifier = Modifier,
        vertical: Boolean = false
    ) = UIDivider(modifier, vertical)

    @Composable
    fun Text(
        text: String,
        modifier: Modifier = Modifier,
        tone: UITextTone = UITextTone.Primary,
        fontWeight: FontWeight = FontWeight.Medium,
        style: TextStyle = UITokens.typography.bodyPrimary,
        maxLines: Int = Int.MAX_VALUE
    ) = UIText(text, modifier, tone, fontWeight, style, maxLines)

    @Composable
    fun Heading(
        text: String,
        modifier: Modifier = Modifier,
        level: Int = 2,
        tone: UITextTone = UITextTone.Primary
    ) = UIHeading(text, modifier, level, tone)

    @Composable
    fun Image(
        name: String,
        modifier: Modifier = Modifier,
        radius: RadiusValue = RadiusValue.Container,
        contentScale: ContentScale = ContentScale.Crop
    ) = UIImage(name, modifier, radius, contentScale)

    @Composable
    fun Icon(
        name: UIIconName,
        modifier: Modifier = Modifier,
        size: Dp = 18.dp,
        tint: Color = androidx.compose.material3.LocalContentColor.current
    ) = UIIcon(name, modifier, size, tint)

    @Composable
    fun Avatar(
        name: String,
        modifier: Modifier = Modifier,
        src: String? = null,
        size: Dp = 40.dp
    ) = UIAvatar(name, modifier, src, size)
}
