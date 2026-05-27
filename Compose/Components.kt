package compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID
import kotlin.math.roundToInt

// ==========================================
// 🕹️ BUTTONS & ACTIONS
// ==========================================

enum class UIButtonVariant { Primary, Secondary, Ghost }
enum class UIButtonSize { Sm, Md, Lg }

@Composable
fun UIButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: UIButtonVariant = UIButtonVariant.Primary,
    size: UIButtonSize = UIButtonSize.Md,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = UITokens.colors
    val effects = UITokens.effects

    val height = when (size) {
        UIButtonSize.Sm -> 34.dp
        UIButtonSize.Md -> 40.dp
        UIButtonSize.Lg -> 46.dp
    }
    val horizPadding = when (size) {
        UIButtonSize.Sm -> 12.dp
        UIButtonSize.Md -> 16.dp
        UIButtonSize.Lg -> 18.dp
    }
    val fontSize = when (size) {
        UIButtonSize.Sm -> 13.sp
        UIButtonSize.Md -> 15.sp
        UIButtonSize.Lg -> 16.sp
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth press scaling & vertical offset cues
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1.0f,
        animationSpec = tween(150, easing = effects.easeCurve),
        label = "button_scale"
    )
    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 0.dp else (-1).dp,
        animationSpec = tween(150, easing = effects.easeCurve),
        label = "button_offset"
    )

    val shape = RoundedCornerShape(UITokens.Layout.radiusControl)

    val buttonBackground = when (variant) {
        UIButtonVariant.Primary -> {
            val baseColor = if (isPressed) colors.actionPrimaryHover else colors.actionPrimary
            Brush.verticalGradient(colors = listOf(baseColor.copy(alpha = 0.88f), baseColor))
        }
        UIButtonVariant.Secondary -> {
            Brush.verticalGradient(colors = listOf(colors.bgSurface.copy(alpha = 0.85f), colors.bgSurface))
        }
        UIButtonVariant.Ghost -> {
            SolidColor(colors.bgSurface.copy(alpha = if (isPressed) 0.6f else 0.4f))
        }
    }

    val borderStroke = when (variant) {
        UIButtonVariant.Primary -> BorderStroke(1.dp, colors.actionPrimary.copy(alpha = 0.4f))
        UIButtonVariant.Secondary -> BorderStroke(1.dp, colors.componentWrappedBg.copy(alpha = 0.44f))
        UIButtonVariant.Ghost -> BorderStroke(1.dp, colors.componentWrappedBg.copy(alpha = 0.42f))
    }

    val textAndIconColor = when (variant) {
        UIButtonVariant.Primary -> colors.textOnAction
        else -> colors.textPrimary
    }

    val shadow = when (variant) {
        UIButtonVariant.Primary -> if (isPressed) ShadowStyle(Color.Transparent, 0.dp, 0.dp, 0.dp) else effects.glowShadow
        UIButtonVariant.Secondary -> if (isPressed) ShadowStyle(Color.Transparent, 0.dp, 0.dp, 0.dp) else effects.softShadow
        UIButtonVariant.Ghost -> ShadowStyle(Color.Transparent, 0.dp, 0.dp, 0.dp)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .offset(y = offsetY)
            .uiShadow(shadow, borderRadius = UITokens.Layout.radiusControl)
            .background(buttonBackground, shape)
            .border(borderStroke, shape)
            .clip(shape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = horizPadding)
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides textAndIconColor) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (leadingIcon != null) leadingIcon()
                ProvideTextStyle(
                    value = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        color = textAndIconColor
                    ),
                    content = content
                )
                if (trailingIcon != null) trailingIcon()
            }
        }
    }
}

@Composable
fun UIIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: UIButtonVariant = UIButtonVariant.Ghost,
    size: Dp = 38.dp,
    icon: @Composable () -> Unit
) {
    val colors = UITokens.colors
    val effects = UITokens.effects

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(150, easing = effects.easeCurve),
        label = "icon_button_scale"
    )

    val shape = CircleShape

    val buttonBackground = when (variant) {
        UIButtonVariant.Primary -> SolidColor(colors.actionPrimary)
        UIButtonVariant.Secondary -> SolidColor(colors.bgSurface.copy(alpha = 0.9f))
        UIButtonVariant.Ghost -> SolidColor(colors.bgSurface.copy(alpha = if (isPressed) 0.5f else 0.3f))
    }

    val borderStroke = when (variant) {
        UIButtonVariant.Primary -> BorderStroke(1.dp, colors.actionPrimary.copy(alpha = 0.6f))
        UIButtonVariant.Secondary -> BorderStroke(1.dp, colors.componentWrappedBg.copy(alpha = 0.5f))
        UIButtonVariant.Ghost -> BorderStroke(1.dp, colors.componentWrappedBg.copy(alpha = 0.4f))
    }

    val tintColor = when (variant) {
        UIButtonVariant.Primary -> colors.textOnAction
        else -> colors.textPrimary
    }

    val shadow = if (isPressed || variant == UIButtonVariant.Ghost) {
        ShadowStyle(Color.Transparent, 0.dp, 0.dp, 0.dp)
    } else {
        effects.softShadow
    }

    Box(
        modifier = modifier
            .scale(scale)
            .uiShadow(shadow, borderRadius = 9999.dp)
            .size(size)
            .background(buttonBackground, shape)
            .border(borderStroke, shape)
            .clip(shape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides tintColor, content = icon)
    }
}

@Composable
fun UIFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = UITokens.colors
    val effects = UITokens.effects

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(150, easing = effects.easeCurve),
        label = "fab_scale"
    )
    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 0.dp else (-2).dp,
        animationSpec = tween(150, easing = effects.easeCurve),
        label = "fab_offset"
    )

    val shape = CircleShape
    val buttonBackground = Brush.verticalGradient(
        colors = listOf(colors.actionPrimary.copy(alpha = 0.9f), colors.actionPrimary)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .offset(y = offsetY)
            .uiShadow(effects.glowShadow, borderRadius = 9999.dp)
            .size(56.dp)
            .background(buttonBackground, shape)
            .clip(shape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.textOnAction, content = content)
    }
}

// ==========================================
// 🎛️ DATA ENTRY & INPUTS
// ==========================================

data class MenuOption(
    val label: String,
    val value: String
)

@Composable
fun UIMenu(
    options: List<MenuOption>,
    selection: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val colors = UITokens.colors
    var expanded by remember { mutableStateOf(false) }

    val selectedOption = options.find { it.value == selection } ?: options.firstOrNull()
    val displayText = selectedOption?.label ?: selection

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (label != null) {
            UIText(text = label, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
        }

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(colors.bgSurface.copy(alpha = 0.8f), colors.bgSurface)
                        ),
                        RoundedCornerShape(UITokens.Layout.radiusControl)
                    )
                    .border(
                        1.dp,
                        colors.componentWrappedBg.copy(alpha = 0.46f),
                        RoundedCornerShape(UITokens.Layout.radiusControl)
                    )
                    .clip(RoundedCornerShape(UITokens.Layout.radiusControl))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UIText(displayText, tone = UITextTone.Primary)
                UIIcon(UIIconName.ChevronDown, size = 12.dp, tint = colors.componentWrappedText)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.bgSurface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { UIText(option.label, tone = UITextTone.Primary) },
                        onClick = {
                            onSelectionChange(option.value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UITextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    prefix: (@Composable () -> Unit)? = null,
    error: String? = null
) {
    val colors = UITokens.colors
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(UITokens.Layout.radiusControl)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (label != null) {
            UIText(text = label, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
        }

        val borderBrush = when {
            error != null -> SolidColor(colors.systemError)
            isFocused -> SolidColor(colors.actionPrimary.copy(alpha = 0.72f))
            else -> SolidColor(colors.componentWrappedBg.copy(alpha = 0.40f))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.bgSurface.copy(alpha = 0.84f), colors.bgSurface)
                    ),
                    shape
                )
                .border(1.dp, borderBrush, shape)
                .clip(shape)
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = 12.dp),
            textStyle = TextStyle(
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontFamily = FontFamily.Default
            ),
            singleLine = true,
            cursorBrush = SolidColor(colors.actionPrimary),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (prefix != null) {
                        CompositionLocalProvider(LocalContentColor provides colors.componentWrappedText.copy(alpha = 0.5f)) {
                            prefix()
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = colors.textSecondary.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        if (error != null) {
            UIText(text = error, tone = UITextTone.Error, style = UITokens.typography.caption)
        }
    }
}

@Composable
fun UITextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    error: String? = null
) {
    val colors = UITokens.colors
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(UITokens.Layout.radiusControl)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (label != null) {
            UIText(text = label, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
        }

        val borderBrush = when {
            error != null -> SolidColor(colors.systemError)
            isFocused -> SolidColor(colors.actionPrimary.copy(alpha = 0.72f))
            else -> SolidColor(colors.componentWrappedBg.copy(alpha = 0.40f))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.bgSurface.copy(alpha = 0.84f), colors.bgSurface)
                    ),
                    shape
                )
                .border(1.dp, borderBrush, shape)
                .clip(shape)
                .onFocusChanged { isFocused = it.isFocused }
                .padding(12.dp),
            textStyle = TextStyle(
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontFamily = FontFamily.Default
            ),
            singleLine = false,
            cursorBrush = SolidColor(colors.actionPrimary),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = colors.textSecondary.copy(alpha = 0.5f),
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (error != null) {
            UIText(text = error, tone = UITextTone.Error, style = UITokens.typography.caption)
        }
    }
}

@Composable
fun UIToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null
) {
    val colors = UITokens.colors
    val effects = UITokens.effects

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 9.5.dp else (-9.5).dp,
        animationSpec = tween(effects.transitionNormal, easing = effects.easeCurve),
        label = "toggle_thumb_offset"
    )

    val trackBg = if (checked) colors.componentWrappedBg else colors.componentWrappedBg.copy(alpha = 0.44f)

    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onCheckedChange(!checked)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(27.dp)
                .background(trackBg, CircleShape)
                .padding(horizontal = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(21.dp)
                    .uiShadow(
                        ShadowStyle(
                            color1 = Color.Black.copy(alpha = 0.28f),
                            radius1 = 3.dp,
                            dx1 = 0.dp,
                            dy1 = 2.dp
                        ),
                        borderRadius = 9999.dp
                    )
                    .background(
                        Brush.verticalGradient(colors = listOf(Color.White, Color(0xFFF0FFFC))),
                        CircleShape
                    )
            )
        }

        if (label != null) {
            CompositionLocalProvider(LocalContentColor provides colors.textPrimary, content = label)
        }
    }
}

@Composable
fun UICheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    label: (@Composable () -> Unit)? = null
) {
    val colors = UITokens.colors
    val active = checked || indeterminate

    val boxBg = if (active) {
        Brush.verticalGradient(colors = listOf(colors.actionPrimary.copy(alpha = 0.7f), colors.actionPrimary))
    } else {
        Brush.verticalGradient(colors = listOf(colors.bgSurface.copy(alpha = 0.72f), colors.bgSurface))
    }

    val borderBrush = if (active) {
        SolidColor(colors.actionPrimary)
    } else {
        SolidColor(colors.componentWrappedBg.copy(alpha = 0.54f))
    }

    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onCheckedChange(!checked)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(boxBg, RoundedCornerShape(4.dp))
                .border(1.dp, borderBrush, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (active) {
                UIIcon(
                    name = if (indeterminate) UIIconName.Alert else UIIconName.Check,
                    size = 11.dp,
                    tint = colors.textOnAction
                )
            }
        }

        if (label != null) {
            CompositionLocalProvider(LocalContentColor provides colors.textPrimary, content = label)
        }
    }
}

@Composable
fun UIRadioGroup(
    options: List<MenuOption>,
    selection: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    horizontal: Boolean = false
) {
    val colors = UITokens.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label != null) {
            UIText(text = label, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
        }

        val itemsLayout = @Composable {
            options.forEach { option ->
                val selected = selection == option.value
                Row(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSelectionChange(option.value)
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(
                                1.dp,
                                if (selected) colors.actionPrimary else colors.componentWrappedBg.copy(alpha = 0.58f),
                                CircleShape
                            )
                            .padding(if (selected) 4.5.dp else 9.dp)
                            .background(
                                if (selected) colors.actionPrimary else Color.Transparent,
                                CircleShape
                            )
                    )
                    UIText(text = option.label, tone = UITextTone.Primary)
                }
            }
        }

        if (horizontal) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsLayout()
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsLayout()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UISlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f
) {
    val colors = UITokens.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label != null) {
                UIText(text = label, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
            } else {
                Spacer(modifier = Modifier.size(1.dp))
            }
            UIText(text = value.roundToInt().toString(), tone = UITextTone.Primary)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                activeTrackColor = colors.componentWrappedBg,
                inactiveTrackColor = colors.componentWrappedBg.copy(alpha = 0.24f),
                thumbColor = colors.actionPrimary
            )
        )
    }
}

// ==========================================
// 🧭 SEGMENTED & NAVIGATION TABS
// ==========================================

@Composable
fun UISegmented(
    options: List<MenuOption>,
    selection: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UITokens.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(colors.bgSurface.copy(alpha = 0.5f), RoundedCornerShape(UITokens.Layout.radiusControl))
            .border(
                1.dp,
                colors.componentWrappedBg.copy(alpha = 0.3f),
                RoundedCornerShape(UITokens.Layout.radiusControl)
            )
            .clip(RoundedCornerShape(UITokens.Layout.radiusControl))
            .padding(2.dp)
    ) {
        options.forEach { option ->
            val active = option.value == selection
            val itemBg = if (active) colors.componentWrappedBg else Color.Transparent
            val itemTextTone = if (active) UITextTone.Wrapped else UITextTone.Primary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(itemBg, RoundedCornerShape(UITokens.Layout.radiusControl - 2.dp))
                    .clip(RoundedCornerShape(UITokens.Layout.radiusControl - 2.dp))
                    .clickable { onSelectionChange(option.value) },
                contentAlignment = Alignment.Center
            ) {
                UIText(
                    text = option.label,
                    tone = itemTextTone,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    style = UITokens.typography.bodySecondary
                )
            }
        }
    }
}

@Composable
fun UITabs(
    options: List<MenuOption>,
    selection: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UITokens.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(colors.bgSurface.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val active = option.value == selection
            val borderModifier = if (active) {
                Modifier.drawBehind {
                    drawRect(
                        color = colors.actionPrimary,
                        topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - 2.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(size.width, 2.dp.toPx())
                    )
                }
            } else Modifier

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .then(borderModifier)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSelectionChange(option.value)
                    }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                UIText(
                    text = option.label,
                    tone = if (active) UITextTone.Action else UITextTone.Primary,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// 🚨 FEEDBACK & POPUPS
// ==========================================

@Composable
fun UISpinner(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    val colors = UITokens.colors
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = colors.actionPrimary,
        strokeWidth = 2.dp
    )
}

@Composable
fun UIProgressBar(
    value: Float,
    max: Float = 100f,
    modifier: Modifier = Modifier
) {
    val colors = UITokens.colors
    val progress = if (max == 0f) 0f else (value / max).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(colors.componentWrappedBg.copy(alpha = 0.24f), CircleShape)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(colors.actionPrimary, CircleShape)
        )
    }
}

enum class UIBadgeTone { Default, Info, Error, Success }

@Composable
fun UIBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: UIBadgeTone = UIBadgeTone.Default
) {
    val colors = UITokens.colors

    val bg = when (tone) {
        UIBadgeTone.Default -> colors.componentWrappedBg.copy(alpha = 0.4f)
        UIBadgeTone.Info -> colors.componentWrappedBg
        UIBadgeTone.Error -> colors.systemError.copy(alpha = 0.2f)
        UIBadgeTone.Success -> colors.componentWrappedBg.copy(alpha = 0.7f)
    }

    val textTone = when (tone) {
        UIBadgeTone.Default -> UITextTone.Primary
        UIBadgeTone.Info -> UITextTone.Wrapped
        UIBadgeTone.Error -> UITextTone.Error
        UIBadgeTone.Success -> UITextTone.Wrapped
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        UIText(
            text = text,
            tone = textTone,
            fontWeight = FontWeight.Bold,
            style = UITokens.typography.caption
        )
    }
}

// Toast alerts system definitions
enum class ToastTone { Info, Success, Error }

data class ToastItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val tone: ToastTone = ToastTone.Info
)

class UIToastManager(private val scope: CoroutineScope) {
    var items = mutableStateListOf<ToastItem>()
        private set

    fun show(title: String, description: String? = null, tone: ToastTone = ToastTone.Info) {
        val item = ToastItem(title = title, description = description, tone = tone)
        items.add(item)
        scope.launch {
            delay(3000)
            items.remove(item)
        }
    }

    fun dismiss(id: String) {
        items.removeAll { it.id == id }
    }
}

@Composable
fun rememberUIToastManager(): UIToastManager {
    val scope = rememberCoroutineScope()
    return remember(scope) { UIToastManager(scope) }
}

@Composable
fun UIToastViewport(
    manager: UIToastManager,
    modifier: Modifier = Modifier
) {
    val colors = UITokens.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        manager.items.forEach { item ->
            val accentColor = when (item.tone) {
                ToastTone.Info -> colors.componentWrappedBg
                ToastTone.Success -> colors.componentWrappedBg
                ToastTone.Error -> colors.systemError
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(colors.bgSurface.copy(alpha = 0.9f), RoundedCornerShape(UITokens.Layout.radiusControl))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(UITokens.Layout.radiusControl))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        UIText(text = item.title, tone = UITextTone.Primary, fontWeight = FontWeight.Bold)
                        if (item.description != null) {
                            UIText(text = item.description, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
                        }
                    }

                    UIIconButton(
                        onClick = { manager.dismiss(item.id) },
                        variant = UIButtonVariant.Ghost,
                        size = 28.dp
                    ) {
                        UIIcon(UIIconName.Close, size = 12.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun UIDialog(
    title: String,
    description: String? = null,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onCancel) {
        UIBox(
            padding = SpaceValue.Normal,
            radius = RadiusValue.Container,
            surface = true,
            border = true,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            UIVStack(
                gap = SpaceValue.Normal,
                horizontalAlignment = Alignment.Start
            ) {
                UIVStack(
                    gap = SpaceValue.Compact,
                    horizontalAlignment = Alignment.Start
                ) {
                    UIText(
                        text = title,
                        tone = UITextTone.Primary,
                        fontWeight = FontWeight.Bold,
                        style = UITokens.typography.heading3
                    )
                    if (description != null) {
                        UIText(text = description, tone = UITextTone.Secondary, style = UITokens.typography.bodySecondary)
                    }
                }

                UIHStack(
                    gap = SpaceValue.Compact,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UISpacer() // Push actions to bottom right
                    UIButton(onClick = onCancel, variant = UIButtonVariant.Ghost) {
                        UIText(cancelText)
                    }
                    UIButton(onClick = onConfirm, variant = UIButtonVariant.Primary) {
                        UIText(confirmText)
                    }
                }
            }
        }
    }
}

@Composable
fun UISkeleton(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = 16.dp,
    rounded: Boolean = true
) {
    val colors = UITokens.colors

    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_opacity"
    )

    val shape = if (rounded) RoundedCornerShape(UITokens.Layout.radiusControl) else RoundedCornerShape(0.dp)
    val widthModifier = if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()

    Box(
        modifier = modifier
            .then(widthModifier)
            .height(height)
            .background(colors.componentWrappedBg.copy(alpha = alpha), shape)
    )
}

// ==========================================
// 🧭 SCAFFOLDING & LAYOUT COMPOSITES
// ==========================================

@Composable
fun UITopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onMenu: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val colors = UITokens.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.bgSurface.copy(alpha = 0.5f))
            .drawBehind {
                drawLine(
                    color = colors.componentWrappedBg.copy(alpha = 0.34f),
                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onMenu != null) {
                UIIconButton(onClick = onMenu, variant = UIButtonVariant.Ghost, size = 36.dp) {
                    UIIcon(UIIconName.Menu)
                }
            }
            UIHeading(text = title, level = 4, tone = UITextTone.Primary)
        }

        if (actions != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions
            )
        }
    }
}

data class BottomBarItem(
    val value: String,
    val label: String,
    val icon: UIIconName,
    val badge: Int = 0
)

@Composable
fun UIBottomBar(
    items: List<BottomBarItem>,
    active: String,
    onActiveChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UITokens.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .height(64.dp)
            .background(colors.bgSurface.copy(alpha = 0.85f), RoundedCornerShape(UITokens.Layout.radiusContainer + 8.dp))
            .border(
                1.dp,
                colors.componentWrappedText.copy(alpha = 0.24f),
                RoundedCornerShape(UITokens.Layout.radiusContainer + 8.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.value == active

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) colors.componentWrappedText.copy(alpha = 0.14f) else Color.Transparent,
                        RoundedCornerShape(UITokens.Layout.radiusContainer)
                    )
                    .clip(RoundedCornerShape(UITokens.Layout.radiusContainer))
                    .clickable { onActiveChange(item.value) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        UIIcon(
                            name = item.icon,
                            size = 20.dp,
                            tint = if (isSelected) colors.actionPrimary else colors.componentWrappedText
                        )
                        if (item.badge > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 10.dp, y = (-3).dp)
                                    .background(Color.Red, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = item.badge.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) colors.actionPrimary else colors.componentWrappedText
                    )
                }
            }
        }
    }
}

@Composable
fun UIDrawer(
    open: Boolean,
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = UITokens.colors

    if (open) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClose() }
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(260.dp)
                .background(colors.bgSurface)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UIText(text = title, tone = UITextTone.Primary, fontWeight = FontWeight.Bold, style = UITokens.typography.heading3)
                    UIIconButton(onClick = onClose, variant = UIButtonVariant.Ghost, size = 36.dp) {
                        UIIcon(UIIconName.Close)
                    }
                }

                UIDivider()

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    content = content
                )
            }
        }
    }
}

// ==========================================
// 📦 HIGH LEVEL EXHIBITS (CARDS, COLLAPSIBLES, LISTS)
// ==========================================

@Composable
fun UICard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    UIBox(
        modifier = modifier,
        padding = SpaceValue.Normal,
        radius = RadiusValue.Container,
        surface = true,
        blurPanel = true,
        border = true,
        content = content
    )
}

@Composable
fun UIChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = UITokens.colors

    val bg = if (selected) colors.componentWrappedBg else colors.bgSurface.copy(alpha = 0.6f)
    val borderBrush = if (selected) colors.actionPrimary else colors.componentWrappedBg.copy(alpha = 0.4f)
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .background(bg, shape)
            .border(1.dp, borderBrush, shape)
            .clip(shape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides (if (selected) colors.componentWrappedText else colors.textPrimary),
            content = content
        )
    }
}

data class AccordionItem(
    val title: String,
    val content: @Composable () -> Unit
)

@Composable
fun UIAccordion(
    items: List<AccordionItem>,
    modifier: Modifier = Modifier
) {
    val colors = UITokens.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(UITokens.Layout.spacingCompact)
    ) {
        items.forEach { item ->
            var expanded by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgSurface.copy(alpha = 0.3f), RoundedCornerShape(UITokens.Layout.radiusControl))
                    .border(
                        1.dp,
                        colors.componentWrappedBg.copy(alpha = 0.2f),
                        RoundedCornerShape(UITokens.Layout.radiusControl)
                    )
                    .clip(RoundedCornerShape(UITokens.Layout.radiusControl))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UIText(text = item.title, tone = UITextTone.Primary, fontWeight = FontWeight.Bold)

                    val angle by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f,
                        animationSpec = tween(300),
                        label = "accordion_chevron_rotate"
                    )

                    Box(modifier = Modifier.rotate(angle)) {
                        UIIcon(name = UIIconName.ChevronDown, tint = colors.componentWrappedText)
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        item.content()
                    }
                }
            }
        }
    }
}

@Composable
fun <T> UIList(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    renderItem: @Composable (T, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        itemsIndexed(items) { index, item ->
            renderItem(item, index)
        }
    }
}

// ==========================================
// 📦 UNIFIED COMPONENT NAMESPACE OBJECT
// ==========================================

object UIComponents {
    @Composable
    fun Button(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        variant: UIButtonVariant = UIButtonVariant.Primary,
        size: UIButtonSize = UIButtonSize.Md,
        leadingIcon: (@Composable () -> Unit)? = null,
        trailingIcon: (@Composable () -> Unit)? = null,
        content: @Composable () -> Unit
    ) = UIButton(onClick, modifier, variant, size, leadingIcon, trailingIcon, content)

    @Composable
    fun IconButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        variant: UIButtonVariant = UIButtonVariant.Ghost,
        size: Dp = 38.dp,
        icon: @Composable () -> Unit
    ) = UIIconButton(onClick, modifier, variant, size, icon)

    @Composable
    fun FAB(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) = UIFAB(onClick, modifier, content)

    @Composable
    fun Menu(
        options: List<MenuOption>,
        selection: String,
        onSelectionChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String? = null
    ) = UIMenu(options, selection, onSelectionChange, modifier, label)

    @Composable
    fun TextField(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String? = null,
        placeholder: String = "",
        prefix: (@Composable () -> Unit)? = null,
        error: String? = null
    ) = UITextField(value, onValueChange, modifier, label, placeholder, prefix, error)

    @Composable
    fun TextArea(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String? = null,
        placeholder: String = "",
        error: String? = null
    ) = UITextArea(value, onValueChange, modifier, label, placeholder, error)

    @Composable
    fun Toggle(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        label: (@Composable () -> Unit)? = null
    ) = UIToggle(checked, onCheckedChange, modifier, label)

    @Composable
    fun Checkbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        indeterminate: Boolean = false,
        label: (@Composable () -> Unit)? = null
    ) = UICheckbox(checked, onCheckedChange, modifier, indeterminate, label)

    @Composable
    fun RadioGroup(
        options: List<MenuOption>,
        selection: String,
        onSelectionChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        label: String? = null,
        horizontal: Boolean = false
    ) = UIRadioGroup(options, selection, onSelectionChange, modifier, label, horizontal)

    @Composable
    fun Slider(
        value: Float,
        onValueChange: (Float) -> Unit,
        modifier: Modifier = Modifier,
        label: String? = null,
        valueRange: ClosedFloatingPointRange<Float> = 0f..100f
    ) = UISlider(value, onValueChange, modifier, label, valueRange)

    @Composable
    fun Segmented(
        options: List<MenuOption>,
        selection: String,
        onSelectionChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) = UISegmented(options, selection, onSelectionChange, modifier)

    @Composable
    fun Tabs(
        options: List<MenuOption>,
        selection: String,
        onSelectionChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) = UITabs(options, selection, onSelectionChange, modifier)

    @Composable
    fun Spinner(
        modifier: Modifier = Modifier,
        size: Dp = 22.dp
    ) = UISpinner(modifier, size)

    @Composable
    fun ProgressBar(
        value: Float,
        max: Float = 100f,
        modifier: Modifier = Modifier
    ) = UIProgressBar(value, max, modifier)

    @Composable
    fun Badge(
        text: String,
        modifier: Modifier = Modifier,
        tone: UIBadgeTone = UIBadgeTone.Default
    ) = UIBadge(text, modifier, tone)

    @Composable
    fun ToastViewport(
        manager: UIToastManager,
        modifier: Modifier = Modifier
    ) = UIToastViewport(manager, modifier)

    @Composable
    fun Dialog(
        title: String,
        description: String? = null,
        confirmText: String = "Confirm",
        cancelText: String = "Cancel",
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) = UIDialog(title, description, confirmText, cancelText, onConfirm, onCancel)

    @Composable
    fun Skeleton(
        modifier: Modifier = Modifier,
        width: Dp? = null,
        height: Dp = 16.dp,
        rounded: Boolean = true
    ) = UISkeleton(modifier, width, height, rounded)

    @Composable
    fun TopAppBar(
        title: String,
        modifier: Modifier = Modifier,
        onMenu: (() -> Unit)? = null,
        actions: (@Composable RowScope.() -> Unit)? = null
    ) = UITopAppBar(title, modifier, onMenu, actions)

    @Composable
    fun BottomBar(
        items: List<BottomBarItem>,
        active: String,
        onActiveChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) = UIBottomBar(items, active, onActiveChange, modifier)

    @Composable
    fun Drawer(
        open: Boolean,
        title: String,
        onClose: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) = UIDrawer(open, title, onClose, modifier, content)

    @Composable
    fun Card(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) = UICard(modifier, content)

    @Composable
    fun Chip(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) = UIChip(selected, onClick, modifier, content)

    @Composable
    fun Accordion(
        items: List<AccordionItem>,
        modifier: Modifier = Modifier
    ) = UIAccordion(items, modifier)

    @Composable
    fun <T> List(
        items: List<T>,
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(0.dp),
        verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
        renderItem: @Composable (T, Int) -> Unit
    ) = UIList(items, modifier, contentPadding, verticalArrangement, renderItem)
}
