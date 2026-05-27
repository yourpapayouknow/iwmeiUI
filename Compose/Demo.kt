package compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

// ==========================================
// 📋 DATA MODELS FOR THE DEMO
// ==========================================

data class BacklogItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val owner: String,
    val status: String // "todo", "doing", "done"
)

// ==========================================
// 📱 MAIN DEMO VIEW ASSEMBLY
// ==========================================

@Composable
fun DemoView() {
    // Theme Mode Controller
    var isDarkMode by rememberSaveable { mutableStateOf(true) }

    // Floating Overlays States
    val toastManager = rememberUIToastManager()
    var drawerOpen by remember { mutableStateOf(false) }
    var dialogOpen by remember { mutableStateOf(false) }

    // Segment & Tab Selectors
    var activeTab by remember { mutableStateOf("overview") }
    var segmented by remember { mutableStateOf("board") }
    var selectedSprint by remember { mutableStateOf("s43") }
    var priority by remember { mutableStateOf("P1") }

    // Interactive Form States
    var projectName by remember { mutableStateOf("统一跨端 UI 框架") }
    var description by remember { mutableStateOf("基于 design-tokens 构建 React、SwiftUI、Compose、WinUI 一致的组件行为与视觉语言。") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var analyticsEnabled by remember { mutableStateOf(true) }
    var accessChecked by remember { mutableStateOf(true) }
    var betaChecked by remember { mutableStateOf(false) }
    var betaIndeterminate by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(62f) }

    // Footer bottom active item
    var activeBottom by remember { mutableStateOf("home") }

    // Data Filtering state
    var filter by remember { mutableStateOf("all") }
    var searchKeyword by remember { mutableStateOf("") }

    // Prepopulate 180 Backlog tasks
    val owners = listOf("林枫", "陈敏", "赵宇", "王珂", "李然", "周宁")
    val statuses = listOf("todo", "doing", "done")
    val backlog = remember {
        List(180) { index ->
            val formattedIndex = String.format("%03d", index + 1)
            BacklogItem(
                title = "任务 #$formattedIndex - 跨端状态映射与组件对齐",
                owner = owners[index % owners.size],
                status = statuses[index % statuses.size]
            )
        }
    }

    // Filter tasks responsively
    val filteredBacklog = remember(backlog, filter, searchKeyword) {
        backlog.filter { item ->
            val matchedStatus = filter == "all" || item.status == filter
            val matchedKeyword = searchKeyword.isEmpty() ||
                    item.title.contains(searchKeyword, ignoreCase = true) ||
                    item.owner.contains(searchKeyword, ignoreCase = true)
            matchedStatus && matchedKeyword
        }
    }

    // Dropdown / Option collections
    val tabItems = listOf(
        MenuOption("总览", "overview"),
        MenuOption("动态", "activity"),
        MenuOption("设置", "settings")
    )
    val segmentedItems = listOf(
        MenuOption("看板", "board"),
        MenuOption("日历", "calendar"),
        MenuOption("时间线", "timeline")
    )
    val sprintMenuItems = listOf(
        MenuOption("迭代 42", "s42"),
        MenuOption("迭代 43", "s43"),
        MenuOption("迭代 44", "s44")
    )
    val priorityItems = listOf(
        MenuOption("P0 (最高)", "P0"),
        MenuOption("P1 (高)", "P1"),
        MenuOption("P2 (中)", "P2")
    )
    val quickFilterItems = listOf(
        MenuOption("全部", "all"),
        MenuOption("待办", "todo"),
        MenuOption("进行中", "doing"),
        MenuOption("已完成", "done")
    )

    QuantumTheme(darkTheme = isDarkMode) {
        val colors = UITokens.colors

        UIBox(
            modifier = Modifier.fillMaxSize()
        ) {
            // Apply mesh base gradient backgrounds
            QuantumBackground()

            // Application shell
            Column(modifier = Modifier.fillMaxSize()) {

                // 1. TOP HEADER APPBAR
                UITopAppBar(
                    title = "iwmeiUI Compose 示例",
                    onMenu = { drawerOpen = true },
                    actions = {
                        UIBadge(if (isDarkMode) "深色" else "浅色", tone = UIBadgeTone.Info)

                        UIToggle(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it }
                        )

                        UIIconButton(
                            onClick = {
                                toastManager.show(
                                    title = "设置已打开",
                                    description = "已加载基于 Token 的配置项。",
                                    tone = ToastTone.Success
                                )
                            },
                            variant = UIButtonVariant.Ghost,
                            size = 36.dp
                        ) {
                            UIIcon(name = UIIconName.Settings)
                        }
                    }
                )

                // 2. SCROLL CONTENT REGION
                Box(modifier = Modifier.weight(1f)) {
                    UIScrollView(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        UIVStack(
                            gap = SpaceValue.Loose,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .padding(bottom = 80.dp) // Leave spacer room for floating footer
                        ) {

                            // CARD SECTION 1: HERO CONTAINER
                            UICard {
                                UIGrid(
                                    minItemWidth = 420.dp,
                                    gap = SpaceValue.Normal,
                                    items = listOf(
                                        {
                                            UIVStack(
                                                gap = SpaceValue.Normal,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                UIBadge("Android / Compose", tone = UIBadgeTone.Success)

                                                UIHeading("基于 Token 的跨端组件体系", level = 2)

                                                UIText(
                                                    text = "这个示例将 design-tokens.json 与 plan.md 直接映射为可用的 Jetpack Compose 组件系统，覆盖主题切换、表单录入、状态反馈、导航以及大数据列表的滚动加载。",
                                                    tone = UITextTone.Secondary
                                                )

                                                UIHStack(gap = SpaceValue.Compact) {
                                                    UIButton(
                                                        onClick = {
                                                            toastManager.show(
                                                                title = "草稿已保存",
                                                                description = "组件映射草稿已保存成功。",
                                                                tone = ToastTone.Success
                                                            )
                                                        },
                                                        variant = UIButtonVariant.Primary
                                                    ) {
                                                        UIText("保存草稿")
                                                    }
                                                    UIButton(
                                                        onClick = { dialogOpen = true },
                                                        variant = UIButtonVariant.Secondary
                                                    ) {
                                                        UIText("打开弹窗")
                                                    }
                                                    UIButton(
                                                        onClick = {
                                                            toastManager.show(
                                                                title = "部署已排队",
                                                                description = "Android Compose 构建流水线已开始执行。",
                                                                tone = ToastTone.Info
                                                            )
                                                        },
                                                        variant = UIButtonVariant.Ghost
                                                    ) {
                                                        UIText("加入部署")
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            UIZStack {
                                                UIImage("hero", modifier = Modifier.height(180.dp))

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(12.dp),
                                                    contentAlignment = Alignment.BottomStart
                                                ) {
                                                    UIBox(
                                                        padding = SpaceValue.Normal,
                                                        radius = RadiusValue.Container,
                                                        wrapped = true
                                                    ) {
                                                        UIHStack(
                                                            gap = SpaceValue.Compact,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            UIAvatar(name = "陈敏", size = 42.dp)

                                                            UIVStack(
                                                                gap = SpaceValue.Custom(4.dp),
                                                                horizontalAlignment = Alignment.Start
                                                            ) {
                                                                UIText("陈敏", tone = UITextTone.Wrapped, fontWeight = FontWeight.Bold)
                                                                UIText(
                                                                    text = "设计系统负责人",
                                                                    tone = UITextTone.Wrapped,
                                                                    style = UITokens.typography.caption
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                )
                            }

                            // CARD SECTION 2: LAYOUTS & NAVIGATION TABS
                            UICard {
                                UIVStack(
                                    gap = SpaceValue.Normal,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        UIHeading("布局、导航与内容层", level = 3)
                                        UISegmented(
                                            options = tabItems,
                                            selection = activeTab,
                                            onSelectionChange = { activeTab = it },
                                            modifier = Modifier.width(240.dp)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        UISegmented(
                                            options = segmentedItems,
                                            selection = segmented,
                                            onSelectionChange = { segmented = it },
                                            modifier = Modifier.width(260.dp)
                                        )
                                        UIMenu(
                                            options = sprintMenuItems,
                                            selection = selectedSprint,
                                            onSelectionChange = { selectedSprint = it },
                                            modifier = Modifier.width(140.dp)
                                        )
                                    }

                                    UIDivider()

                                    when (activeTab) {
                                        "overview" -> {
                                            UIGrid(
                                                minItemWidth = 240.dp,
                                                gap = SpaceValue.Compact,
                                                items = listOf(
                                                    {
                                                        UIBox(padding = SpaceValue.Normal, radius = RadiusValue.Container, surface = true, border = true) {
                                                            UIVStack(gap = SpaceValue.Custom(8.dp), horizontalAlignment = Alignment.Start) {
                                                                UIText("原子组件", tone = UITextTone.Secondary)
                                                                UIHeading("23", level = 4)
                                                                UIText("布局与内容基础能力", tone = UITextTone.Primary)
                                                            }
                                                        }
                                                    },
                                                    {
                                                        UIBox(padding = SpaceValue.Normal, radius = RadiusValue.Container, surface = true, border = true) {
                                                            UIVStack(gap = SpaceValue.Custom(8.dp), horizontalAlignment = Alignment.Start) {
                                                                UIText("复合组件", tone = UITextTone.Secondary)
                                                                UIHeading("17", level = 4)
                                                                UIText("表单、反馈与骨架组件", tone = UITextTone.Primary)
                                                            }
                                                        }
                                                    },
                                                    {
                                                        UIBox(padding = SpaceValue.Normal, radius = RadiusValue.Container, surface = true, border = true) {
                                                            UIVStack(gap = SpaceValue.Custom(8.dp), horizontalAlignment = Alignment.Start) {
                                                                UIText("主题模式", tone = UITextTone.Secondary)
                                                                UIHeading("2", level = 4)
                                                                UIText("浅色与深色，共享布局", tone = UITextTone.Primary)
                                                            }
                                                        }
                                                    }
                                                )
                                            )
                                        }
                                        "activity" -> {
                                            UIVStack(gap = SpaceValue.Compact, horizontalAlignment = Alignment.Start) {
                                                UIHStack(gap = SpaceValue.Compact, verticalAlignment = Alignment.CenterVertically) {
                                                    UISpinner(size = 20.dp)
                                                    UIText("正在同步跨平台组件动态...", tone = UITextTone.Primary)
                                                }
                                                UIProgressBar(value = progress, max = 100f)
                                                UIText("实时同步进度：${progress.toInt()}%", tone = UITextTone.Secondary)
                                            }
                                        }
                                        "settings" -> {
                                            UIGrid(
                                                minItemWidth = 280.dp,
                                                gap = SpaceValue.Compact,
                                                items = listOf(
                                                    {
                                                        UIVStack(gap = SpaceValue.Normal, horizontalAlignment = Alignment.Start) {
                                                            UIToggle(
                                                                checked = notificationsEnabled,
                                                                onCheckedChange = { notificationsEnabled = it },
                                                                label = { Text("开启消息通知") }
                                                            )
                                                            UIToggle(
                                                                checked = analyticsEnabled,
                                                                onCheckedChange = { analyticsEnabled = it },
                                                                label = { Text("开启使用分析") }
                                                            )
                                                        }
                                                    },
                                                    {
                                                        UIRadioGroup(
                                                            label = "优先级",
                                                            selection = priority,
                                                            onSelectionChange = { priority = it },
                                                            options = priorityItems,
                                                            horizontal = true
                                                        )
                                                    }
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // CARD SECTION 3: FORM ENTRY AND FEEDBACK INDICATORS
                            UICard {
                                UIGrid(
                                    minItemWidth = 380.dp,
                                    gap = SpaceValue.Normal,
                                    items = listOf(
                                        {
                                            UIVStack(gap = SpaceValue.Normal, horizontalAlignment = Alignment.Start) {
                                                UIHeading("录入与触发层", level = 3)

                                                UITextField(
                                                    value = projectName,
                                                    onValueChange = { projectName = it },
                                                    label = "项目名称",
                                                    placeholder = "请输入项目名称",
                                                    prefix = { UIIcon(UIIconName.Home, size = 16.dp) }
                                                )

                                                UITextArea(
                                                    value = description,
                                                    onValueChange = { description = it },
                                                    label = "项目说明",
                                                    placeholder = "请输入说明",
                                                    error = if (description.length < 20) "请至少输入 20 个字符。" else null
                                                )

                                                UISlider(
                                                    label = "交付进度",
                                                    value = progress,
                                                    onValueChange = { progress = it },
                                                    valueRange = 0f..100f
                                                )

                                                UIHStack(gap = SpaceValue.Compact) {
                                                    UICheckbox(
                                                        checked = accessChecked,
                                                        onCheckedChange = { accessChecked = it },
                                                        label = { Text("生产权限") }
                                                    )
                                                    UICheckbox(
                                                        checked = betaChecked,
                                                        onCheckedChange = {
                                                            betaChecked = it
                                                            betaIndeterminate = false
                                                        },
                                                        indeterminate = betaIndeterminate,
                                                        label = { Text("Beta 功能") }
                                                    )
                                                }
                                            }
                                        },
                                        {
                                            UIVStack(gap = SpaceValue.Normal, horizontalAlignment = Alignment.Start) {
                                                UIHeading("反馈与状态层", level = 3)

                                                UIHStack(gap = SpaceValue.Compact) {
                                                    UIBadge("默认")
                                                    UIBadge("进行中", tone = UIBadgeTone.Info)
                                                    UIBadge("已完成", tone = UIBadgeTone.Success)
                                                    UIBadge("错误", tone = UIBadgeTone.Error)
                                                }

                                                UIBox(padding = SpaceValue.Normal, radius = RadiusValue.Container, surface = true, border = true) {
                                                    UIVStack(gap = SpaceValue.Custom(8.dp), horizontalAlignment = Alignment.Start) {
                                                        UISkeleton(width = 160.dp, height = 14.dp)
                                                        UISkeleton(width = 240.dp, height = 14.dp)
                                                        UISkeleton(width = 190.dp, height = 14.dp)
                                                    }
                                                }

                                                UIHStack(gap = SpaceValue.Compact) {
                                                    UIButton(
                                                        onClick = {
                                                            toastManager.show(
                                                                title = "后台任务已启动",
                                                                description = "上传任务正在执行中。",
                                                                tone = ToastTone.Info
                                                            )
                                                        },
                                                        variant = UIButtonVariant.Secondary,
                                                        leadingIcon = { UIIcon(UIIconName.Bell, size = 16.dp) }
                                                    ) {
                                                        UIText("显示提示")
                                                    }

                                                    UIButton(
                                                        onClick = { dialogOpen = true },
                                                        variant = UIButtonVariant.Ghost,
                                                        leadingIcon = { UIIcon(UIIconName.Alert, size = 16.dp) }
                                                    ) {
                                                        UIText("确认操作")
                                                    }
                                                }
                                            }
                                        }
                                    )
                                )
                            }

                            // CARD SECTION 4: DATA LISTS AND COLLAPSIBLES
                            UICard {
                                UIVStack(
                                    gap = SpaceValue.Normal,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    UIHeading("复杂数据展示层", level = 3)

                                    UIHStack(gap = SpaceValue.Compact, verticalAlignment = Alignment.CenterVertically) {
                                        quickFilterItems.forEach { item ->
                                            UIChip(
                                                selected = filter == item.value,
                                                onClick = { filter = item.value }
                                            ) {
                                                UIText(item.label)
                                            }
                                        }

                                        UITextField(
                                            value = searchKeyword,
                                            onValueChange = { searchKeyword = it },
                                            placeholder = "搜索任务标题或负责人",
                                            prefix = { UIIcon(UIIconName.Search, size = 16.dp) }
                                        )
                                    }

                                    // Display Tasks using high performance UIList
                                    Box(modifier = Modifier.height(280.dp)) {
                                        UIList(
                                            items = filteredBacklog,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) { item, index ->
                                            BacklogRowView(item = item, index = index)
                                        }
                                    }

                                    UIAccordion(
                                        items = listOf(
                                            AccordionItem("实现策略") {
                                                UIText("先做原子组件与 Token 映射，再补交互态，最后组合页面级骨架。", tone = UITextTone.Secondary)
                                            },
                                            AccordionItem("可访问性说明") {
                                                UIText("保留焦点高亮、语义标签和深浅主题下稳定的对比度。", tone = UITextTone.Secondary)
                                            },
                                            AccordionItem("性能说明") {
                                                UIText("长列表使用延迟堆叠加载，在保证视觉一致的同时控制渲染开销。", tone = UITextTone.Secondary)
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. BOTTOM TAB NAVIGATION BAR (FLOATING OVERLAY)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                UIBottomBar(
                    items = listOf(
                        BottomBarItem("home", "首页", UIIconName.Home, badge = 2),
                        BottomBarItem("search", "搜索", UIIconName.Search),
                        BottomBarItem("alerts", "提醒", UIIconName.Bell, badge = 5),
                        BottomBarItem("profile", "我的", UIIconName.User)
                    ),
                    active = activeBottom,
                    onActiveChange = { activeBottom = it }
                )
            }

            // 4. FLOATING ACTION BUTTON (FAB OVERLAY)
             Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(end = 24.dp, bottom = 92.dp),
                 contentAlignment = Alignment.BottomEnd
             ) {
                UIFAB(
                    onClick = {
                        toastManager.show(
                            title = "快速创建",
                            description = "已生成一个新的任务草稿。",
                            tone = ToastTone.Success
                        )
                    }
                ) {
                    UIIcon(name = UIIconName.Plus, size = 22.dp)
                }
            }

            // 5. SLIDING SIDE DRAWER OVERLAY
            UIDrawer(
                open = drawerOpen,
                title = "组件导航",
                onClose = { drawerOpen = false }
            ) {
                UIButton(onClick = { activeTab = "overview"; drawerOpen = false }, variant = UIButtonVariant.Ghost) {
                    UIText("总览")
                }
                UIButton(onClick = { activeTab = "activity"; drawerOpen = false }, variant = UIButtonVariant.Ghost) {
                    UIText("动态")
                }
                UIButton(onClick = { activeTab = "settings"; drawerOpen = false }, variant = UIButtonVariant.Ghost) {
                    UIText("设置")
                }
            }

            // 6. CONFIRM DIALOG OVERLAY
            if (dialogOpen) {
                // Dim Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { dialogOpen = false }
                )

                UIDialog(
                    title = "确认发布这套组件吗？",
                    description = "发布后将把当前 Compose 版本作为其他平台实现的对齐基线。",
                    confirmText = "立即发布",
                    cancelText = "稍后再说",
                    onConfirm = {
                        toastManager.show(
                            title = "发布成功",
                            description = "Compose 基线组件已成功发布。",
                            tone = ToastTone.Success
                        )
                        dialogOpen = false
                    },
                    onCancel = {
                        dialogOpen = false
                    }
                )
            }

            // 7. TOAST FLOATING VIEWS LAYER
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                UIToastViewport(manager = toastManager)
            }
        }
    }
}

// ==========================================
// 🛠️ BACKLOG ROW PREVIEW SUBVIEW
// ==========================================

@Composable
fun BacklogRowView(item: BacklogItem, index: Int) {
    val colors = UITokens.colors

    val badgeTone = when (item.status) {
        "done" -> UIBadgeTone.Success
        "doing" -> UIBadgeTone.Info
        else -> UIBadgeTone.Default
    }

    val statusLabel = when (item.status) {
        "done" -> "已完成"
        "doing" -> "进行中"
        else -> "待办"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgSurface.copy(alpha = 0.4f), RoundedCornerShape(UITokens.Layout.radiusControl - 2.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1.5f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedIndex = String.format("%03d", index + 1)
            UIText(
                text = "#$formattedIndex",
                tone = UITextTone.Secondary,
                style = UITokens.typography.bodySecondary
            )
            UIText(
                text = item.title,
                tone = UITextTone.Primary,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            UIText(
                text = item.owner,
                tone = UITextTone.Secondary,
                style = UITokens.typography.bodySecondary
            )
            UIBadge(text = statusLabel, tone = badgeTone)
        }
    }
}

@Preview
@Composable
fun DemoViewPreview() {
    DemoView()
}
