import SwiftUI

// MARK: - Enums for Layout Value Resolvers
public enum SpaceValue {
    case none
    case compact
    case normal
    case loose
    case custom(CGFloat)
    
    public var value: CGFloat {
        switch self {
        case .none: return 0
        case .compact: return UITokens.Layout.spacingCompact
        case .normal: return UITokens.Layout.spacingNormal
        case .loose: return UITokens.Layout.spacingLoose
        case .custom(let val): return val
        }
    }
}

public enum RadiusValue {
    case none
    case control
    case container
    case full
    case custom(CGFloat)
    
    public var value: CGFloat {
        switch self {
        case .none: return 0
        case .control: return UITokens.Layout.radiusControl
        case .container: return UITokens.Layout.radiusContainer
        case .full: return UITokens.Layout.radiusFull
        case .custom(let val): return val
        }
    }
}

// MARK: - Box Component
public struct UIBox<Content: View>: View {
    public let padding: SpaceValue
    public let radius: RadiusValue
    public let surface: Bool
    public let wrapped: Bool
    public let blurPanel: Bool
    public let border: Bool
    public let content: () -> Content
    
    @Environment(\.colorScheme) var colorScheme
    
    public init(
        padding: SpaceValue = .none,
        radius: RadiusValue = .none,
        surface: Bool = false,
        wrapped: Bool = false,
        blurPanel: Bool = false,
        border: Bool = false,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.padding = padding
        self.radius = radius
        self.surface = surface
        self.wrapped = wrapped
        self.blurPanel = blurPanel
        self.border = border
        self.content = content
    }
    
    public var body: some View {
        content()
            .padding(padding.value)
            .background(backgroundView)
            .clipShape(RoundedRectangle(cornerRadius: radius.value))
            .overlay(
                RoundedRectangle(cornerRadius: radius.value)
                    .stroke(
                        border ? UITokens.Colors.textSecondary.opacity(0.18) : Color.clear,
                        lineWidth: 1
                    )
            )
    }
    
    @ViewBuilder
    private var backgroundView: some View {
        if wrapped {
            UITokens.Colors.componentWrappedBg
        } else if blurPanel {
            if #available(iOS 15.0, macOS 12.0, *) {
                Color.clear.background(.ultraThinMaterial)
            } else {
                UITokens.Colors.bgSurface.opacity(UITokens.Effects.panelOpacity(for: colorScheme))
            }
        } else if surface {
            UITokens.Colors.bgSurface
        } else {
            Color.clear
        }
    }
}

// MARK: - HStack Component
public struct UIHStack<Content: View>: View {
    public let gap: SpaceValue
    public let alignment: VerticalAlignment
    public let content: () -> Content
    
    public init(
        gap: SpaceValue = .normal,
        alignment: VerticalAlignment = .center,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.gap = gap
        self.alignment = alignment
        self.content = content
    }
    
    public var body: some View {
        HStack(alignment: alignment, spacing: gap.value) {
            content()
        }
    }
}

// MARK: - VStack Component
public struct UIVStack<Content: View>: View {
    public let gap: SpaceValue
    public let alignment: HorizontalAlignment
    public let content: () -> Content
    
    public init(
        gap: SpaceValue = .normal,
        alignment: HorizontalAlignment = .center,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.gap = gap
        self.alignment = alignment
        self.content = content
    }
    
    public var body: some View {
        VStack(alignment: alignment, spacing: gap.value) {
            content()
        }
    }
}

// MARK: - ZStack Component
public struct UIZStack<Content: View>: View {
    public let alignment: Alignment
    public let content: () -> Content
    
    public init(
        alignment: Alignment = .center,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.alignment = alignment
        self.content = content
    }
    
    public var body: some View {
        ZStack(alignment: alignment) {
            content()
        }
    }
}

// MARK: - Grid Component
public struct UIGrid<Content: View>: View {
    public let columns: Int?
    public let minItemWidth: CGFloat
    public let gap: SpaceValue
    public let content: () -> Content
    
    public init(
        columns: Int? = nil,
        minItemWidth: CGFloat = 220.0,
        gap: SpaceValue = .normal,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.columns = columns
        self.minItemWidth = minItemWidth
        self.gap = gap
        self.content = content
    }
    
    public var body: some View {
        let spacing = gap.value
        let gridColumns: [GridItem] = {
            if let cols = columns {
                return Array(repeating: GridItem(.flexible(), spacing: spacing), count: cols)
            } else {
                return [GridItem(.adaptive(minimum: minItemWidth), spacing: spacing)]
            }
        }()
        
        LazyVGrid(columns: gridColumns, spacing: spacing) {
            content()
        }
    }
}

// MARK: - ScrollView Component
public struct UIScrollView<Content: View>: View {
    public let axes: Axis.Set
    public let showIndicators: Bool
    public let maxHeight: CGFloat?
    public let content: () -> Content
    
    public init(
        _ axes: Axis.Set = .vertical,
        showIndicators: Bool = true,
        maxHeight: CGFloat? = nil,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.axes = axes
        self.showIndicators = showIndicators
        self.maxHeight = maxHeight
        self.content = content
    }
    
    public var body: some View {
        ScrollView(axes, showsIndicators: showIndicators) {
            content()
        }
        .frame(maxHeight: maxHeight)
    }
}

// MARK: - Spacer Component
public struct UISpacer: View {
    public init() {}
    public var body: some View {
        Spacer()
    }
}

// MARK: - Divider Component
public struct UIDivider: View {
    public let vertical: Bool
    
    public init(vertical: Bool = false) {
        self.vertical = vertical
    }
    
    public var body: some View {
        if vertical {
            Divider()
                .frame(width: 1)
                .background(UITokens.Colors.componentWrappedBg.opacity(0.38))
        } else {
            Divider()
                .frame(height: 1)
                .background(UITokens.Colors.componentWrappedBg.opacity(0.35))
        }
    }
}

// MARK: - Text Tones
public enum UITextTone {
    case primary
    case secondary
    case wrapped
    case action
    case error
    
    public var color: Color {
        switch self {
        case .primary: return UITokens.Colors.textPrimary
        case .secondary: return UITokens.Colors.textSecondary
        case .wrapped: return UITokens.Colors.componentWrappedText
        case .action: return UITokens.Colors.actionPrimary
        case .error: return UITokens.Colors.systemError
        }
    }
}

// MARK: - Text Component
public struct UIText: View {
    public let text: String
    public let tone: UITextTone
    public let weight: Font.Weight
    public let font: Font
    
    public init(
        _ text: String,
        tone: UITextTone = .primary,
        weight: Font.Weight = .medium,
        font: Font = .system(size: 15)
    ) {
        self.text = text
        self.tone = tone
        self.weight = weight
        self.font = font
    }
    
    public var body: some View {
        Text(text)
            .font(font.weight(weight))
            .foregroundColor(tone.color)
    }
}

// MARK: - Heading Component
public struct UIHeading: View {
    public let text: String
    public let level: Int
    public let tone: UITextTone
    
    public init(
        _ text: String,
        level: Int = 2,
        tone: UITextTone = .primary
    ) {
        self.text = text
        self.level = level
        self.tone = tone
    }
    
    public var body: some View {
        let font: Font = {
            switch level {
            case 1: return UITokens.Typography.heading1
            case 2: return UITokens.Typography.heading2
            case 3: return UITokens.Typography.heading3
            case 4: return UITokens.Typography.heading4
            default: return UITokens.Typography.heading2
            }
        }()
        
        Text(text)
            .font(font)
            .foregroundColor(tone.color)
            .lineSpacing(4)
    }
}

// MARK: - Image Component
public struct UIImage: View {
    public let name: String
    public let radius: RadiusValue
    public let contentMode: ContentMode
    
    public init(
        _ name: String,
        radius: RadiusValue = .container,
        contentMode: ContentMode = .fill
    ) {
        self.name = name
        self.radius = radius
        self.contentMode = contentMode
    }
    
    public var body: some View {
        // If image exists in asset catalog we render it, otherwise render a visual fallback mockup
        if let _ = PlatformImage(named: name) {
            Image(name)
                .resizable()
                .aspectRatio(contentMode: contentMode)
                .clipShape(RoundedRectangle(cornerRadius: radius.value))
        } else {
            // Premium gradient graphic placeholder
            ZStack {
                LinearGradient(
                    gradient: Gradient(colors: [
                        UITokens.Colors.componentWrappedBg.opacity(0.8),
                        UITokens.Colors.bgSurface
                    ]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                
                VStack(spacing: 8) {
                    Image(systemName: "photo.on.rectangle.angled")
                        .font(.system(size: 28))
                        .foregroundColor(UITokens.Colors.componentWrappedText)
                    
                    Text("Image: \(name)")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(UITokens.Colors.componentWrappedText.opacity(0.8))
                }
            }
            .frame(minHeight: 180)
            .clipShape(RoundedRectangle(cornerRadius: radius.value))
            .overlay(
                RoundedRectangle(cornerRadius: radius.value)
                    .stroke(UITokens.Colors.componentWrappedBg.opacity(0.4), lineWidth: 1)
            )
        }
    }
}

// Cross-platform Image Helper
#if canImport(UIKit)
typealias PlatformImage = UIImage
#elseif canImport(AppKit)
typealias PlatformImage = NSImage
#endif

// MARK: - Icon Name Map
public enum UIIconName: String, CaseIterable {
    case menu
    case home
    case search
    case bell
    case user
    case settings
    case plus
    case close
    case chevronDown
    case check
    case alert
    
    public var sfSymbolName: String {
        switch self {
        case .menu: return "line.3.horizontal"
        case .home: return "house.fill"
        case .search: return "magnifyingglass"
        case .bell: return "bell.fill"
        case .user: return "person.fill"
        case .settings: return "gearshape.fill"
        case .plus: return "plus"
        case .close: return "xmark"
        case .chevronDown: return "chevron.down"
        case .check: return "checkmark"
        case .alert: return "exclamationmark.triangle.fill"
        }
    }
}

// MARK: - Icon Component
public struct UIIcon: View {
    public let symbolName: String
    public let size: CGFloat
    
    public init(_ name: UIIconName, size: CGFloat = 18.0) {
        self.symbolName = name.sfSymbolName
        self.size = size
    }
    
    public init(systemName: String, size: CGFloat = 18.0) {
        self.symbolName = systemName
        self.size = size
    }
    
    public var body: some View {
        Image(systemName: symbolName)
            .resizable()
            .scaledToFit()
            .frame(width: size, height: size)
    }
}

// MARK: - Avatar Component
public struct UIAvatar: View {
    public let src: String?
    public let name: String
    public let size: CGFloat
    
    @Environment(\.colorScheme) var colorScheme
    
    public init(src: String? = nil, name: String = "User", size: CGFloat = 40.0) {
        self.src = src
        self.name = name
        self.size = size
    }
    
    public var body: some View {
        let initials: String = {
            let parts = name.split(separator: " ")
            let first = parts.first?.first.map(String.init) ?? ""
            let last = parts.count > 1 ? parts.last?.first.map(String.init) ?? "" : ""
            return (first + last).uppercased()
        }()
        
        ZStack {
            if let src = src, !src.isEmpty, PlatformImage(named: src) != nil {
                Image(src)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } else {
                LinearGradient(
                    gradient: Gradient(colors: [
                        UITokens.Colors.componentWrappedBg,
                        UITokens.Colors.componentWrappedBg.opacity(0.8)
                    ]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                
                Text(initials)
                    .font(.system(size: size * 0.4, weight: .bold))
                    .foregroundColor(UITokens.Colors.componentWrappedText)
            }
        }
        .frame(width: size, height: size)
        .clipShape(Circle())
        .uiShadow(UITokens.Effects.softShadow(for: colorScheme))
    }
}

// MARK: - Namespace API Mapping Helper
public enum UI {
    public typealias Box = UIBox
    public typealias HStack = UIHStack
    public typealias VStack = UIVStack
    public typealias ZStack = UIZStack
    public typealias Grid = UIGrid
    public typealias ScrollView = UIScrollView
    public typealias Spacer = UISpacer
    public typealias Divider = UIDivider
    public typealias Text = UIText
    public typealias Heading = UIHeading
    public typealias Image = UIImage
    public typealias Icon = UIIcon
    public typealias Avatar = UIAvatar
}
