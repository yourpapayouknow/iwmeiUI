import SwiftUI

// MARK: - Backlog Item Model
public struct BacklogItem: Identifiable, Equatable {
    public let id = UUID()
    public let title: String
    public let owner: String
    public let status: BacklogStatus
}

public enum BacklogStatus: String, CaseIterable {
    case todo
    case doing
    case done
}

// MARK: - Root App Entry Point
@main
struct DemoApp: App {
    var body: some Scene {
        WindowGroup {
            DemoView()
                .frame(minWidth: 1280, maxWidth: .infinity, minHeight: 720, maxHeight: .infinity)
        }
    }
}

// MARK: - Main Demo View
public struct DemoView: View {
    // Theme Mode
    @State private var isDarkMode = true
    
    // UI Manager / Navigation State
    @StateObject private var toastManager = UIToastManager()
    @State private var drawerOpen = false
    @State private var dialogOpen = false
    
    // Tabs and Filter Values
    @State private var activeTab = "overview"
    @State private var segmented = "board"
    @State private var selectedSprint = "s43"
    @State private var priority = "P1"
    
    // Form Binding Data
    @State private var projectName = "统一跨端 UI 框架"
    @State private var description = "基于 design-tokens 构建 React、SwiftUI、Compose、WinUI 一致的组件行为与视觉语言。"
    @State private var notificationsEnabled = true
    @State private var analyticsEnabled = true
    @State private var accessChecked = true
    @State private var betaChecked = false
    @State private var betaIndeterminate = true
    @State private var progress = 62.0
    
    // Bottom Bar State
    @State private var activeBottom = "home"
    
    // Data list filtering
    @State private var filter = "all"
    @State private var searchKeyword = ""
    
    // 180 Mock Tasks
    private let backlog: [BacklogItem] = {
        let owners = ["林枫", "陈敏", "赵宇", "王珂", "李然", "周宁"]
        let statuses: [BacklogStatus] = [.todo, .doing, .done]
        return Array(0..<180).map { index in
            BacklogItem(
                title: "任务 #\(String(format: "%03d", index + 1)) - 跨端状态映射与组件对齐",
                owner: owners[index % owners.count],
                status: statuses[index % statuses.count]
            )
        }
    }()
    
    // Filtered Backlog
    private var filteredBacklog: [BacklogItem] {
        backlog.filter { item in
            let matchedStatus = filter == "all" || item.status.rawValue == filter
            let matchedKeyword = searchKeyword.isEmpty ||
                item.title.localizedCaseInsensitiveContains(searchKeyword) ||
                item.owner.localizedCaseInsensitiveContains(searchKeyword)
            return matchedStatus && matchedKeyword
        }
    }
    
    // Menu Options
    private let tabItems = [
        MenuOption(label: "总览", value: "overview"),
        MenuOption(label: "动态", value: "activity"),
        MenuOption(label: "设置", value: "settings")
    ]
    private let segmentedItems = [
        MenuOption(label: "看板", value: "board"),
        MenuOption(label: "日历", value: "calendar"),
        MenuOption(label: "时间线", value: "timeline")
    ]
    private let sprintMenuItems = [
        MenuOption(label: "迭代 42", value: "s42"),
        MenuOption(label: "迭代 43", value: "s43"),
        MenuOption(label: "迭代 44", value: "s44")
    ]
    private let priorityItems = [
        MenuOption(label: "P0 (最高)", value: "P0"),
        MenuOption(label: "P1 (高)", value: "P1"),
        MenuOption(label: "P2 (中)", value: "P2")
    ]
    private let quickFilterItems = [
        MenuOption(label: "全部", value: "all"),
        MenuOption(label: "待办", value: "todo"),
        MenuOption(label: "进行中", value: "doing"),
        MenuOption(label: "已完成", value: "done")
    ]
    
    public init() {}
    
    public var body: some View {
        ZStack {
            // Apply Global Dynamic Background Style
            QuantumBackground()
            
            // App Layout Shell
            VStack(spacing: 0) {
                
                // 1. Top AppBar
                HStack(spacing: 12) {
                    UIIconButton(.menu, variant: .ghost, size: 36) {
                        withAnimation(UITokens.Effects.transitionNormal) {
                            drawerOpen = true
                        }
                    }
                    
                    UIHeading("iwmeiUI SwiftUI 示例", level: 4)
                    
                    Spacer()
                    
                    UIBadge(isDarkMode ? "深色" : "浅色", tone: .info)
                    
                    // Native Switch Style Toggle mapped to Quantum Logic
                    UIToggle(isOn: $isDarkMode) {
                        EmptyView()
                    }
                    .scaleEffect(0.9)
                    
                    UIIconButton(.settings, variant: .ghost, size: 36) {
                        toastManager.show(
                            title: "设置已打开",
                            description: "已加载基于 Token 的配置项。",
                            tone: .success
                        )
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(
                    Color.clear.background(.thinMaterial)
                )
                .overlay(
                    VStack {
                        Spacer()
                        Rectangle()
                            .frame(height: 1)
                            .foregroundColor(UITokens.Colors.componentWrappedBg.opacity(0.34))
                    }
                )
                
                // 2. Main Scroll Content View
                UIScrollView {
                    UIVStack(gap: .loose) {
                        
                        // SECTION 1: Hero card
                        UICard {
                            UIGrid(columns: nil, minItemWidth: 420, gap: .normal) {
                                UIVStack(gap: .normal, alignment: .leading) {
                                    UIBadge("Apple / SwiftUI", tone: .success)
                                    
                                    UIHeading("基于 Token 的跨端组件体系", level: 2)
                                    
                                    UIText(
                                        "这个示例将 design-tokens.json 与 plan.md 直接映射为可用的 SwiftUI 组件系统，覆盖主题切换、表单录入、状态反馈、导航以及大数据列表的滚动加载。",
                                        tone: .secondary
                                    )
                                    
                                    UIHStack(gap: .compact) {
                                        UIButton("保存草稿", variant: .primary) {
                                            toastManager.show(
                                                title: "草稿已保存",
                                                description: "组件映射草稿已保存成功。",
                                                tone: .success
                                            )
                                        }
                                        UIButton("打开弹窗", variant: .secondary) {
                                            withAnimation { dialogOpen = true }
                                        }
                                        UIButton("加入部署", variant: .ghost) {
                                            toastManager.show(
                                                title: "部署已排队",
                                                description: "Apple SwiftUI 构建流水线已开始执行。",
                                                tone: .info
                                            )
                                        }
                                    }
                                }
                                
                                UIZStack {
                                    UIImage("hero")
                                        .frame(minHeight: 180)
                                    
                                    VStack {
                                        Spacer()
                                        UIBox(padding: .normal, radius: .container, wrapped: true) {
                                            UIHStack(gap: .compact, alignment: .center) {
                                                UIAvatar(name: "陈敏", size: 42)
                                                
                                                UIVStack(gap: .custom(4), alignment: .leading) {
                                                    UIText("陈敏", tone: .wrapped, weight: .bold)
                                                    UIText("设计系统负责人", tone: .wrapped)
                                                        .font(.system(size: 12))
                                                }
                                                Spacer()
                                            }
                                        }
                                        .padding(12)
                                    }
                                }
                            }
                        }
                        
                        // SECTION 2: Layout & Tabs card
                        UICard {
                            UIVStack(gap: .normal, alignment: .leading) {
                                UIHStack {
                                    UIHeading("布局、导航与内容层", level: 3)
                                    Spacer()
                                    // Custom Tab Bar component
                                    UICustomSegmented(options: tabItems, selection: $activeTab)
                                        .frame(maxWidth: 240)
                                }
                                
                                UIHStack(gap: .normal) {
                                    UICustomSegmented(options: segmentedItems, selection: $segmented)
                                        .frame(maxWidth: 260)
                                    Spacer()
                                    UIMenu(label: nil, options: sprintMenuItems, selection: $selectedSprint)
                                        .frame(width: 140)
                                }
                                
                                UIDivider()
                                
                                if activeTab == "overview" {
                                    UIGrid(columns: nil, minItemWidth: 240, gap: .compact) {
                                        UIBox(padding: .normal, radius: .container, surface: true, border: true) {
                                            UIVStack(gap: .custom(8), alignment: .leading) {
                                                UIText("原子组件", tone: .secondary)
                                                UIHeading("23", level: 4)
                                                UIText("布局与内容基础能力", tone: .primary)
                                            }
                                        }
                                        UIBox(padding: .normal, radius: .container, surface: true, border: true) {
                                            UIVStack(gap: .custom(8), alignment: .leading) {
                                                UIText("复合组件", tone: .secondary)
                                                UIHeading("17", level: 4)
                                                UIText("表单、反馈与骨架组件", tone: .primary)
                                            }
                                        }
                                        UIBox(padding: .normal, radius: .container, surface: true, border: true) {
                                            UIVStack(gap: .custom(8), alignment: .leading) {
                                                UIText("主题模式", tone: .secondary)
                                                UIHeading("2", level: 4)
                                                UIText("浅色与深色，共享布局", tone: .primary)
                                            }
                                        }
                                    }
                                } else if activeTab == "activity" {
                                    UIVStack(gap: .compact, alignment: .leading) {
                                        UIHStack(gap: .compact, alignment: .center) {
                                            UISpinner(size: 20)
                                            UIText("正在同步跨平台组件动态...", tone: .primary)
                                        }
                                        UIProgressBar(value: progress, max: 100)
                                        UIText("实时同步进度：\(Int(progress))%", tone: .secondary)
                                    }
                                } else if activeTab == "settings" {
                                    UIGrid(columns: nil, minItemWidth: 280, gap: .compact) {
                                        UIVStack(gap: .normal, alignment: .leading) {
                                            UIToggle(isOn: $notificationsEnabled) {
                                                Text("开启消息通知")
                                            }
                                            UIToggle(isOn: $analyticsEnabled) {
                                                Text("开启使用分析")
                                            }
                                        }
                                        UIRadioGroup(
                                            label: "优先级",
                                            selection: $priority,
                                            options: priorityItems,
                                            horizontal: true
                                        )
                                    }
                                }
                            }
                        }
                        
                        // SECTION 3: Forms and feedback card
                        UICard {
                            UIGrid(columns: nil, minItemWidth: 380, gap: .normal) {
                                // Form inputs
                                UIVStack(gap: .normal, alignment: .leading) {
                                    UIHeading("录入与触发层", level: 3)
                                    
                                    UITextField(
                                        label: "项目名称",
                                        placeholder: "请输入项目名称",
                                        text: $projectName,
                                        prefix: UIIcon(.home, size: 16)
                                    )
                                    
                                    UITextArea(
                                        label: "项目说明",
                                        placeholder: "请输入说明",
                                        text: $description,
                                        error: description.count < 20 ? "请至少输入 20 个字符。" : nil
                                    )
                                    
                                    UISlider(label: "交付进度", value: $progress, bounds: 0...100)
                                    
                                    UIHStack(gap: .compact) {
                                        UICheckbox("生产权限", isOn: $accessChecked)
                                        UICheckbox(
                                            "Beta 功能",
                                            isOn: $betaChecked,
                                            indeterminate: betaIndeterminate
                                        )
                                        .simultaneousGesture(TapGesture().onEnded {
                                            betaIndeterminate = false
                                        })
                                    }
                                }
                                
                                // Feedback controls
                                UIVStack(gap: .normal, alignment: .leading) {
                                    UIHeading("反馈与状态层", level: 3)
                                    
                                    UIHStack(gap: .compact) {
                                        UIBadge("默认")
                                        UIBadge("进行中", tone: .info)
                                        UIBadge("已完成", tone: .success)
                                        UIBadge("错误", tone: .error)
                                    }
                                    
                                    UIBox(padding: .normal, radius: .container, surface: true, border: true) {
                                        UIVStack(gap: .custom(8), alignment: .leading) {
                                            UISkeleton(width: 160, height: 14)
                                            UISkeleton(width: 240, height: 14)
                                            UISkeleton(width: 190, height: 14)
                                        }
                                    }
                                    
                                    UIHStack(gap: .compact) {
                                        UIButton(
                                            "显示提示",
                                            variant: .secondary,
                                            leading: UIIcon(.bell, size: 16)
                                        ) {
                                            toastManager.show(
                                                title: "后台任务已启动",
                                                description: "上传任务正在执行中。",
                                                tone: .info
                                            )
                                        }
                                        
                                        UIButton(
                                            "确认操作",
                                            variant: .ghost,
                                            leading: UIIcon(.alert, size: 16)
                                        ) {
                                            withAnimation { dialogOpen = true }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // SECTION 4: List and Accordion
                        UICard {
                            UIVStack(gap: .normal, alignment: .leading) {
                                UIHeading("复杂数据展示层", level: 3)
                                
                                UIHStack(gap: .compact) {
                                    ForEach(quickFilterItems, id: \.value) { item in
                                        UIChip(
                                            selected: filter == item.value,
                                            label: item.label
                                        ) {
                                            withAnimation {
                                                filter = item.value
                                            }
                                        }
                                    }
                                    
                                    UITextField(
                                        placeholder: "搜索任务标题或负责人",
                                        text: $searchKeyword,
                                        prefix: UIIcon(.search, size: 16)
                                    )
                                }
                                
                                // Scroll list container for high performance
                                UIScrollView(maxHeight: 280) {
                                    UIVStack(gap: .compact) {
                                        ForEach(filteredBacklog.indices, id: \.self) { idx in
                                            let item = filteredBacklog[idx]
                                            BacklogRowView(item: item, index: idx)
                                        }
                                    }
                                }
                                
                                UIAccordion(items: [
                                    UIAccordionItem(
                                        title: "实现策略",
                                        content: UIText("先做原子组件与 Token 映射，再补交互态，最后组合页面级骨架。", tone: .secondary)
                                    ),
                                    UIAccordionItem(
                                        title: "可访问性说明",
                                        content: UIText("保留焦点高亮、语义标签和深浅主题下稳定的对比度。", tone: .secondary)
                                    ),
                                    UIAccordionItem(
                                        title: "性能说明",
                                        content: UIText("长列表使用延迟堆叠加载，在保证视觉一致的同时控制渲染开销。", tone: .secondary)
                                    )
                                ])
                            }
                        }
                        
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 24)
                    .padding(.bottom, 80) // bottom padding for floating bar
                }
            }
            
            // 3. Floating Bottom Navigation Bar
            VStack {
                Spacer()
                BottomBarView(active: $activeBottom)
            }
            
            // 4. Fixed Floating Action Button (FAB)
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    UIFAB {
                        toastManager.show(
                            title: "快速创建",
                            description: "已生成一个新的任务草稿。",
                            tone: .success
                        )
                    } content: {
                        UIIcon(.plus, size: 22)
                    }
                    .padding(.trailing, 24)
                    .padding(.bottom, 92) // float above bottom tab bar
                }
            }
            
            // 5. Drawer sliding overlay
            if drawerOpen {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation(UITokens.Effects.transitionNormal) {
                            drawerOpen = false
                        }
                    }
                
                HStack {
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            UIHeading("组件导航", level: 3)
                            Spacer()
                            UIIconButton(.close, variant: .ghost, size: 36) {
                                withAnimation(UITokens.Effects.transitionNormal) {
                                    drawerOpen = false
                                }
                            }
                        }
                        
                        UIDivider()
                        
                        UIVStack(gap: .compact, alignment: .leading) {
                            UIButton("总览", variant: .ghost) {
                                activeTab = "overview"
                                withAnimation { drawerOpen = false }
                            }
                            UIButton("动态", variant: .ghost) {
                                activeTab = "activity"
                                withAnimation { drawerOpen = false }
                            }
                            UIButton("设置", variant: .ghost) {
                                activeTab = "settings"
                                withAnimation { drawerOpen = false }
                            }
                        }
                        
                        Spacer()
                    }
                    .padding()
                    .frame(width: 260)
                    .background(UITokens.Colors.bgSurface)
                    .shadow(color: Color.black.opacity(0.3), radius: 10, x: 5, y: 0)
                    .transition(.move(edge: .leading))
                    
                    Spacer()
                }
                .ignoresSafeArea()
            }
            
            // 6. Dialog Overlay
            if dialogOpen {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                
                UIDialog(
                    title: "确认发布这套组件吗？",
                    description: "发布后将把当前 SwiftUI 版本作为其他平台实现的对齐基线。",
                    confirmText: "立即发布",
                    cancelText: "稍后再说",
                    onConfirm: {
                        toastManager.show(
                            title: "发布成功",
                            description: "SwiftUI 基线组件已成功发布。",
                            tone: .success
                        )
                        withAnimation { dialogOpen = false }
                    },
                    onCancel: {
                        withAnimation { dialogOpen = false }
                    }
                )
                .transition(.scale.combined(with: .opacity))
            }
            
            // 7. Toast Alerts Layer
            VStack {
                Spacer()
                UIToastViewport(manager: toastManager)
                    .padding(.bottom, 80) // shift above bottom bar
            }
        }
        // Force the Light / Dark Preferred Scheme dynamically
        .preferredColorScheme(isDarkMode ? .dark : .light)
    }
}

// MARK: - Backlog Row View
struct BacklogRowView: View {
    let item: BacklogItem
    let index: Int
    
    var body: some View {
        HStack {
            HStack(spacing: 8) {
                Text("#\(String(format: "%03d", index + 1))")
                    .font(.system(size: 13))
                    .foregroundColor(UITokens.Colors.textSecondary)
                
                Text(item.title)
                    .font(.system(size: 14, weight: .medium))
                    .lineLimit(1)
                    .foregroundColor(UITokens.Colors.textPrimary)
            }
            
            Spacer()
            
            HStack(spacing: 12) {
                Text(item.owner)
                    .font(.system(size: 13))
                    .foregroundColor(UITokens.Colors.textSecondary)
                
                let tone: UIBadgeTone = {
                    switch item.status {
                    case .done: return .success
                    case .doing: return .info
                    case .todo: return .default
                    }
                }()
                let statusLabel: String = {
                    switch item.status {
                    case .done: return "已完成"
                    case .doing: return "进行中"
                    case .todo: return "待办"
                    }
                }()
                
                UIBadge(statusLabel, tone: tone)
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 12)
        .background(UITokens.Colors.bgSurface.opacity(0.4))
        .cornerRadius(UITokens.Layout.radiusControl - 2)
    }
}

// MARK: - Bottom Navigation View
struct BottomBarView: View {
    @Binding var active: String
    
    var body: some View {
        HStack(spacing: 6) {
            BottomBarItemView(iconName: .home, label: "首页", badgeCount: 2, value: "home", active: $active)
            BottomBarItemView(iconName: .search, label: "搜索", badgeCount: 0, value: "search", active: $active)
            BottomBarItemView(iconName: .bell, label: "提醒", badgeCount: 5, value: "alerts", active: $active)
            BottomBarItemView(iconName: .user, label: "我的", badgeCount: 0, value: "profile", active: $active)
        }
        .padding(8)
        .background(
            Color.clear.background(.ultraThinMaterial)
        )
        .cornerRadius(UITokens.Layout.radiusContainer + 8)
        .overlay(
            RoundedRectangle(cornerRadius: UITokens.Layout.radiusContainer + 8)
                .stroke(UITokens.Colors.componentWrappedText.opacity(0.24), lineWidth: 1)
        )
        .shadow(color: Color.black.opacity(0.2), radius: 10, y: 5)
        .padding(.horizontal)
        .padding(.bottom, 12)
    }
}

struct BottomBarItemView: View {
    let iconName: UIIconName
    let label: String
    let badgeCount: Int
    let value: String
    @Binding var active: String
    
    var body: some View {
        let isSelected = active == value
        Button {
            withAnimation(UITokens.Effects.transitionNormal) {
                active = value
            }
        } label: {
            VStack(spacing: 4) {
                ZStack(alignment: .topTrailing) {
                    UIIcon(iconName, size: 20)
                        .foregroundColor(isSelected ? UITokens.Colors.actionPrimary : UITokens.Colors.componentWrappedText)
                    
                    if badgeCount > 0 {
                        Text("\(badgeCount)")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 4)
                            .padding(.vertical, 1)
                            .background(Color.red)
                            .clipShape(Capsule())
                            .offset(x: 12, y: -6)
                    }
                }
                
                Text(label)
                    .font(.system(size: 11, weight: isSelected ? .bold : .regular))
                    .foregroundColor(isSelected ? UITokens.Colors.actionPrimary : UITokens.Colors.componentWrappedText)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
            .background(isSelected ? UITokens.Colors.componentWrappedText.opacity(0.14) : Color.clear)
            .cornerRadius(UITokens.Layout.radiusContainer)
        }
        .buttonStyle(.plain)
    }
}
