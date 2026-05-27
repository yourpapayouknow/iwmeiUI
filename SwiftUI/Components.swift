import SwiftUI

// MARK: - Button Configuration
public enum UIButtonVariant {
    case primary
    case secondary
    case ghost
}

public enum UIButtonSize {
    case sm
    case md
    case lg
    
    public var height: CGFloat {
        switch self {
        case .sm: return 34
        case .md: return 40
        case .lg: return 46
        }
    }
    public var paddingHorizontal: CGFloat {
        switch self {
        case .sm: return 12
        case .md: return 16
        case .lg: return 18
        }
    }
    public var fontSize: CGFloat {
        switch self {
        case .sm: return 13
        case .md: return 15
        case .lg: return 16
        }
    }
}

// MARK: - Button Component
public struct UIButton<LabelText: View, Leading: View, Trailing: View>: View {
    public let variant: UIButtonVariant
    public let size: UIButtonSize
    public let leading: Leading
    public let trailing: Trailing
    public let action: () -> Void
    public let labelText: LabelText
    
    @Environment(\.colorScheme) var colorScheme
    
    public init(
        variant: UIButtonVariant = .primary,
        size: UIButtonSize = .md,
        @ViewBuilder leading: () -> Leading,
        @ViewBuilder trailing: () -> Trailing,
        action: @escaping () -> Void,
        @ViewBuilder label: () -> LabelText
    ) {
        self.variant = variant
        self.size = size
        self.leading = leading()
        self.trailing = trailing()
        self.action = action
        self.labelText = label()
    }
    
    public var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if !(leading is EmptyView) {
                    leading
                }
                labelText
                    .font(.system(size: size.fontSize, weight: .bold))
                if !(trailing is EmptyView) {
                    trailing
                }
            }
            .padding(.horizontal, size.paddingHorizontal)
            .frame(height: size.height)
            .contentShape(RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl))
        }
        .buttonStyle(QuantumButtonStyle(variant: variant, size: size, colorScheme: colorScheme))
    }
}

// Convenience Button Initializers
extension UIButton where LabelText == Text, Leading == EmptyView, Trailing == EmptyView {
    public init(
        _ text: String,
        variant: UIButtonVariant = .primary,
        size: UIButtonSize = .md,
        action: @escaping () -> Void
    ) {
        self.init(
            variant: variant,
            size: size,
            leading: { EmptyView() },
            trailing: { EmptyView() },
            action: action,
            label: { Text(text) }
        )
    }
}

extension UIButton where LabelText == Text, Trailing == EmptyView {
    public init(
        _ text: String,
        variant: UIButtonVariant = .primary,
        size: UIButtonSize = .md,
        leading: Leading,
        action: @escaping () -> Void
    ) {
        self.init(
            variant: variant,
            size: size,
            leading: { leading },
            trailing: { EmptyView() },
            action: action,
            label: { Text(text) }
        )
    }
}

// MARK: - Button Style (Premium Lift and Glow)
public struct QuantumButtonStyle: ButtonStyle {
    public let variant: UIButtonVariant
    public let size: UIButtonSize
    public let colorScheme: ColorScheme
    
    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundColor(foregroundColor)
            .background(backgroundView(isPressed: configuration.isPressed))
            .clipShape(RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl))
            .overlay(
                RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl)
                    .stroke(borderColor, lineWidth: 1)
            )
            .uiShadow(shadowStyle(isPressed: configuration.isPressed))
            .scaleEffect(configuration.isPressed ? 0.985 : 1.0)
            .offset(y: configuration.isPressed ? 0 : -1)
            .animation(UITokens.Effects.transitionQuick, value: configuration.isPressed)
    }
    
    private var foregroundColor: Color {
        switch variant {
        case .primary:
            return UITokens.Colors.textOnAction
        case .secondary, .ghost:
            return UITokens.Colors.textPrimary
        }
    }
    
    @ViewBuilder
    private func backgroundView(isPressed: Bool) -> some View {
        switch variant {
        case .primary:
            let baseColor = isPressed ? UITokens.Colors.actionPrimaryHover : UITokens.Colors.actionPrimary
            LinearGradient(
                gradient: Gradient(colors: [
                    baseColor.opacity(0.88),
                    baseColor
                ]),
                startPoint: .top,
                endPoint: .bottom
            )
        case .secondary:
            LinearGradient(
                gradient: Gradient(colors: [
                    UITokens.Colors.bgSurface.opacity(0.85),
                    UITokens.Colors.bgSurface
                ]),
                startPoint: .top,
                endPoint: .bottom
            )
        case .ghost:
            UITokens.Colors.bgSurface.opacity(isPressed ? 0.6 : 0.4)
        }
    }
    
    private var borderColor: Color {
        switch variant {
        case .primary:
            return UITokens.Colors.actionPrimary.opacity(0.4)
        case .secondary:
            return UITokens.Colors.componentWrappedBg.opacity(0.44)
        case .ghost:
            return UITokens.Colors.componentWrappedBg.opacity(0.42)
        }
    }
    
    private func shadowStyle(isPressed: Bool) -> ShadowStyle {
        if isPressed {
            return ShadowStyle(color1: .clear, radius1: 0, x1: 0, y1: 0, color2: .clear, radius2: 0, x2: 0, y2: 0)
        }
        switch variant {
        case .primary:
            return UITokens.Effects.glowShadow(for: colorScheme)
        case .secondary:
            return UITokens.Effects.softShadow(for: colorScheme)
        case .ghost:
            return ShadowStyle(color1: .clear, radius1: 0, x1: 0, y1: 0, color2: .clear, radius2: 0, x2: 0, y2: 0)
        }
    }
}

// MARK: - IconButton Component
public struct UIIconButton<IconContent: View>: View {
    public let variant: UIButtonVariant
    public let size: CGFloat
    public let icon: IconContent
    public let action: () -> Void
    
    @Environment(\.colorScheme) var colorScheme
    
    public init(
        variant: UIButtonVariant = .ghost,
        size: CGFloat = 38,
        @ViewBuilder icon: () -> IconContent,
        action: @escaping () -> Void
    ) {
        self.variant = variant
        self.size = size
        self.icon = icon()
        self.action = action
    }
    
    public var body: some View {
        Button(action: action) {
            icon
                .frame(width: size, height: size)
        }
        .buttonStyle(QuantumIconButtonStyle(variant: variant, size: size, colorScheme: colorScheme))
    }
}

public struct QuantumIconButtonStyle: ButtonStyle {
    let variant: UIButtonVariant
    let size: CGFloat
    let colorScheme: ColorScheme
    
    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundColor(foregroundColor)
            .background(backgroundView(isPressed: configuration.isPressed))
            .clipShape(Circle())
            .overlay(
                Circle()
                    .stroke(borderColor, lineWidth: 1)
            )
            .uiShadow(shadowStyle(isPressed: configuration.isPressed))
            .scaleEffect(configuration.isPressed ? 0.96 : 1.0)
            .animation(UITokens.Effects.transitionQuick, value: configuration.isPressed)
    }
    
    private var foregroundColor: Color {
        switch variant {
        case .primary:
            return UITokens.Colors.textOnAction
        default:
            return UITokens.Colors.textPrimary
        }
    }
    
    @ViewBuilder
    private func backgroundView(isPressed: Bool) -> some View {
        switch variant {
        case .primary:
            UITokens.Colors.actionPrimary
        case .secondary:
            UITokens.Colors.bgSurface.opacity(0.9)
        case .ghost:
            UITokens.Colors.bgSurface.opacity(isPressed ? 0.5 : 0.3)
        }
    }
    
    private var borderColor: Color {
        switch variant {
        case .primary:
            return UITokens.Colors.actionPrimary.opacity(0.6)
        case .secondary:
            return UITokens.Colors.componentWrappedBg.opacity(0.5)
        case .ghost:
            return UITokens.Colors.componentWrappedBg.opacity(0.4)
        }
    }
    
    private func shadowStyle(isPressed: Bool) -> ShadowStyle {
        if isPressed || variant == .ghost {
            return ShadowStyle(color1: .clear, radius1: 0, x1: 0, y1: 0, color2: .clear, radius2: 0, x2: 0, y2: 0)
        }
        return UITokens.Effects.softShadow(for: colorScheme)
    }
}

extension UIIconButton where IconContent == UIIcon {
    public init(
        _ iconName: UIIconName,
        variant: UIButtonVariant = .ghost,
        size: CGFloat = 38,
        action: @escaping () -> Void
    ) {
        self.init(variant: variant, size: size, icon: { UIIcon(iconName, size: size * 0.48) }, action: action)
    }
    
    public init(
        systemName: String,
        variant: UIButtonVariant = .ghost,
        size: CGFloat = 38,
        action: @escaping () -> Void
    ) {
        self.init(variant: variant, size: size, icon: { UIIcon(systemName: systemName, size: size * 0.48) }, action: action)
    }
}

// MARK: - Floating Action Button (FAB) Component
public struct UIFAB<Content: View>: View {
    public let content: Content
    public let action: () -> Void
    
    @Environment(\.colorScheme) var colorScheme
    
    public init(action: @escaping () -> Void, @ViewBuilder content: () -> Content) {
        self.action = action
        self.content = content()
    }
    
    public var body: some View {
        Button(action: action) {
            content
                .frame(width: 56, height: 56)
        }
        .buttonStyle(QuantumFABStyle(colorScheme: colorScheme))
    }
}

public struct QuantumFABStyle: ButtonStyle {
    let colorScheme: ColorScheme
    
    public func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundColor(UITokens.Colors.textOnAction)
            .background(
                LinearGradient(
                    gradient: Gradient(colors: [
                        UITokens.Colors.actionPrimary.opacity(0.9),
                        UITokens.Colors.actionPrimary
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .clipShape(Circle())
            .uiShadow(UITokens.Effects.glowShadow(for: colorScheme))
            .scaleEffect(configuration.isPressed ? 0.94 : 1.0)
            .offset(y: configuration.isPressed ? 0 : -2)
            .animation(UITokens.Effects.transitionQuick, value: configuration.isPressed)
    }
}

// MARK: - Menu Options
public struct MenuOption {
    public let label: String
    public let value: String
    
    public init(label: String, value: String) {
        self.label = label
        self.value = value
    }
}

// MARK: - Menu Selection Component
public struct UIMenu: View {
    public let label: String?
    public let options: [MenuOption]
    @Binding public var selection: String
    
    public init(label: String? = nil, options: [MenuOption], selection: Binding<String>) {
        self.label = label
        self.options = options
        self._selection = selection
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            if let label = label {
                Text(label)
                    .font(.system(size: 14, weight: .regular))
                    .foregroundColor(UITokens.Colors.textSecondary)
            }
            
            Menu {
                Picker("", selection: $selection) {
                    ForEach(options, id: \.value) { option in
                        Text(option.label).tag(option.value)
                    }
                }
                .pickerStyle(.inline)
            } label: {
                HStack {
                    Text(options.first(where: { $0.value == selection })?.label ?? selection)
                        .foregroundColor(UITokens.Colors.textPrimary)
                        .font(.system(size: 15))
                    Spacer()
                    Image(systemName: "chevron.down")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(UITokens.Colors.componentWrappedText)
                }
                .padding(.horizontal, 12)
                .frame(height: 42)
                .background(
                    LinearGradient(
                        gradient: Gradient(colors: [
                            UITokens.Colors.bgSurface.opacity(0.8),
                            UITokens.Colors.bgSurface
                        ]),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .cornerRadius(UITokens.Layout.radiusControl)
                .overlay(
                    RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl)
                        .stroke(UITokens.Colors.componentWrappedBg.opacity(0.46), lineWidth: 1)
                )
            }
        }
    }
}

// MARK: - TextField Component
public struct UITextField: View {
    public let label: String?
    public let placeholder: String
    @Binding public var text: String
    public let prefix: AnyView?
    public let error: String?
    
    @FocusState private var isFocused: Bool
    
    public init<Prefix: View>(
        label: String? = nil,
        placeholder: String = "",
        text: Binding<String>,
        prefix: Prefix? = nil,
        error: String? = nil
    ) {
        self.label = label
        self.placeholder = placeholder
        self._text = text
        self.prefix = prefix != nil ? AnyView(prefix) : nil
        self.error = error
    }
    
    public init(
        label: String? = nil,
        placeholder: String = "",
        text: Binding<String>,
        error: String? = nil
    ) {
        self.label = label
        self.placeholder = placeholder
        self._text = text
        self.prefix = nil
        self.error = error
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            if let label = label {
                Text(label)
                    .font(.system(size: 14, weight: .regular))
                    .foregroundColor(UITokens.Colors.textSecondary)
            }
            
            HStack(spacing: 8) {
                if let prefix = prefix {
                    prefix
                        .foregroundColor(UITokens.Colors.componentWrappedText.opacity(0.5))
                }
                
                TextField("", text: $text)
                    .textFieldStyle(.plain)
                    .font(.system(size: 15))
                    .foregroundColor(UITokens.Colors.textPrimary)
                    .focused($isFocused)
                    .placeholder(when: text.isEmpty) {
                        Text(placeholder)
                            .foregroundColor(UITokens.Colors.textSecondary.opacity(0.5))
                            .font(.system(size: 15))
                    }
            }
            .padding(.horizontal, 12)
            .frame(height: 40)
            .background(
                LinearGradient(
                    gradient: Gradient(colors: [
                        UITokens.Colors.bgSurface.opacity(0.84),
                        UITokens.Colors.bgSurface
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .cornerRadius(UITokens.Layout.radiusControl)
            .overlay(
                RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl)
                    .stroke(
                        error != nil ? UITokens.Colors.systemError : (isFocused ? UITokens.Colors.actionPrimary.opacity(0.72) : UITokens.Colors.componentWrappedBg.opacity(0.4)),
                        lineWidth: 1
                    )
            )
            
            if let error = error {
                Text(error)
                    .font(.system(size: 12))
                    .foregroundColor(UITokens.Colors.systemError)
            }
        }
    }
}

// Placeholder Helper
extension View {
    func placeholder<Content: View>(
        when shouldShow: Bool,
        alignment: Alignment = .leading,
        @ViewBuilder placeholder: () -> Content
    ) -> some View {
        ZStack(alignment: alignment) {
            placeholder().opacity(shouldShow ? 1 : 0)
            self
        }
    }
}

// MARK: - TextArea Component
public struct UITextArea: View {
    public let label: String?
    public let placeholder: String
    @Binding public var text: String
    public let error: String?
    
    @FocusState private var isFocused: Bool
    
    public init(
        label: String? = nil,
        placeholder: String = "",
        text: Binding<String>,
        error: String? = nil
    ) {
        self.label = label
        self.placeholder = placeholder
        self._text = text
        self.error = error
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            if let label = label {
                Text(label)
                    .font(.system(size: 14, weight: .regular))
                    .foregroundColor(UITokens.Colors.textSecondary)
            }
            
            ZStack(alignment: .topLeading) {
                if text.isEmpty {
                    Text(placeholder)
                        .foregroundColor(UITokens.Colors.textSecondary.opacity(0.5))
                        .font(.system(size: 15))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 12)
                }
                
                TextEditor(text: $text)
                    .font(.system(size: 15))
                    .foregroundColor(UITokens.Colors.textPrimary)
                    .focused($isFocused)
                    .padding(8)
                    .background(Color.clear)
                    .onAppear {
                        #if os(iOS)
                        UITextView.appearance().backgroundColor = .clear
                        #endif
                    }
            }
            .frame(minHeight: 120)
            .background(
                LinearGradient(
                    gradient: Gradient(colors: [
                        UITokens.Colors.bgSurface.opacity(0.84),
                        UITokens.Colors.bgSurface
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .cornerRadius(UITokens.Layout.radiusControl)
            .overlay(
                RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl)
                    .stroke(
                        error != nil ? UITokens.Colors.systemError : (isFocused ? UITokens.Colors.actionPrimary.opacity(0.72) : UITokens.Colors.componentWrappedBg.opacity(0.4)),
                        lineWidth: 1
                    )
            )
            
            if let error = error {
                Text(error)
                    .font(.system(size: 12))
                    .foregroundColor(UITokens.Colors.systemError)
            }
        }
    }
}

// MARK: - Toggle Component
public struct QuantumToggleStyle: ToggleStyle {
    public func makeBody(configuration: Configuration) -> some View {
        HStack(spacing: 10) {
            Button {
                withAnimation(UITokens.Effects.transitionNormal) {
                    configuration.isOn.toggle()
                }
            } label: {
                RoundedRectangle(cornerRadius: UITokens.Layout.radiusFull)
                    .fill(configuration.isOn ? UITokens.Colors.componentWrappedBg : UITokens.Colors.componentWrappedBg.opacity(0.44))
                    .frame(width: 46, height: 27)
                    .overlay(
                        Circle()
                            .fill(LinearGradient(gradient: Gradient(colors: [.white, Color(hex: "#f0fffc")]), startPoint: .top, endPoint: .bottom))
                            .frame(width: 21, height: 21)
                            .shadow(color: Color.black.opacity(0.28), radius: 3, x: 0, y: 2)
                            .offset(x: configuration.isOn ? 9.5 : -9.5)
                    )
            }
            .buttonStyle(.plain)
            
            configuration.label
                .font(.system(size: 15))
                .foregroundColor(UITokens.Colors.textPrimary)
        }
    }
}

public struct UIToggle<LabelContent: View>: View {
    @Binding public var isOn: Bool
    public let label: LabelContent
    
    public init(isOn: Binding<Bool>, @ViewBuilder label: () -> LabelContent) {
        self._isOn = isOn
        self.label = label()
    }
    
    public var body: some View {
        Toggle(isOn: $isOn) {
            label
        }
        .toggleStyle(QuantumToggleStyle())
    }
}

extension UIToggle where LabelContent == Text {
    public init(_ title: String, isOn: Binding<Bool>) {
        self.init(isOn: isOn) {
            Text(title)
        }
    }
}

// MARK: - Checkbox Component
public struct UICheckbox<LabelContent: View>: View {
    @Binding public var isOn: Bool
    public let indeterminate: Bool
    public let label: LabelContent
    
    public init(isOn: Binding<Bool>, indeterminate: Bool = false, @ViewBuilder label: () -> LabelContent) {
        self._isOn = isOn
        self.indeterminate = indeterminate
        self.label = label()
    }
    
    public var body: some View {
        Button {
            withAnimation(UITokens.Effects.transitionQuick) {
                isOn.toggle()
            }
        } label: {
            HStack(spacing: 8) {
                ZStack {
                    RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl - 4)
                        .fill(
                            (isOn || indeterminate) ?
                            LinearGradient(gradient: Gradient(colors: [UITokens.Colors.actionPrimary.opacity(0.7), UITokens.Colors.actionPrimary]), startPoint: .top, endPoint: .bottom) :
                            LinearGradient(gradient: Gradient(colors: [UITokens.Colors.bgSurface.opacity(0.72), UITokens.Colors.bgSurface]), startPoint: .top, endPoint: .bottom)
                        )
                        .frame(width: 20, height: 20)
                        .overlay(
                            RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl - 4)
                                .stroke(
                                    (isOn || indeterminate) ? UITokens.Colors.actionPrimary : UITokens.Colors.componentWrappedBg.opacity(0.54),
                                    lineWidth: 1
                                )
                        )
                    
                    if isOn || indeterminate {
                        Image(systemName: indeterminate ? "minus" : "checkmark")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(UITokens.Colors.textOnAction)
                    }
                }
                
                label
                    .font(.system(size: 15))
                    .foregroundColor(UITokens.Colors.textPrimary)
            }
        }
        .buttonStyle(.plain)
    }
}

extension UICheckbox where LabelContent == Text {
    public init(_ title: String, isOn: Binding<Bool>, indeterminate: Bool = false) {
        self.init(isOn: isOn, indeterminate: indeterminate) {
            Text(title)
        }
    }
}

// MARK: - RadioGroup Component
public struct UIRadioGroup: View {
    public let label: String?
    @Binding public var selection: String
    public let options: [MenuOption]
    public let horizontal: Bool
    
    public init(
        label: String? = nil,
        selection: Binding<String>,
        options: [MenuOption],
        horizontal: Bool = false
    ) {
        self.label = label
        self._selection = selection
        self.options = options
        self.horizontal = horizontal
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let label = label {
                Text(label)
                    .font(.system(size: 14, weight: .regular))
                    .foregroundColor(UITokens.Colors.textSecondary)
            }
            
            let itemsView = ForEach(options, id: \.value) { option in
                Button {
                    withAnimation(UITokens.Effects.transitionQuick) {
                        selection = option.value
                    }
                } label: {
                    HStack(spacing: 8) {
                        Circle()
                            .strokeBorder(selection == option.value ? UITokens.Colors.actionPrimary : UITokens.Colors.componentWrappedBg.opacity(0.58), lineWidth: 1)
                            .background(
                                Circle()
                                    .fill(selection == option.value ? UITokens.Colors.actionPrimary : Color.clear)
                                    .padding(selection == option.value ? 4.5 : 9)
                            )
                            .frame(width: 18, height: 18)
                        
                        Text(option.label)
                            .font(.system(size: 15))
                            .foregroundColor(UITokens.Colors.textPrimary)
                    }
                }
                .buttonStyle(.plain)
            }
            
            if horizontal {
                HStack(spacing: 18) {
                    itemsView
                }
            } else {
                VStack(alignment: .leading, spacing: 10) {
                    itemsView
                }
            }
        }
    }
}

// MARK: - Slider Component
public struct UISlider: View {
    public let label: String?
    @Binding public var value: Double
    public let bounds: ClosedRange<Double>
    
    public init(label: String? = nil, value: Binding<Double>, bounds: ClosedRange<Double> = 0...100) {
        self.label = label
        self._value = value
        self.bounds = bounds
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                if let label = label {
                    Text(label)
                        .font(.system(size: 14, weight: .regular))
                        .foregroundColor(UITokens.Colors.textSecondary)
                }
                Spacer()
                Text("\(Int(value))")
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(UITokens.Colors.textPrimary)
            }
            
            Slider(value: $value, in: bounds)
                .accentColor(UITokens.Colors.actionPrimary)
        }
    }
}

// MARK: - Custom Segmented Control Component
public struct UICustomSegmented: View {
    public let options: [MenuOption]
    @Binding public var selection: String
    
    public init(options: [MenuOption], selection: Binding<String>) {
        self.options = options
        self._selection = selection
    }
    
    public var body: some View {
        HStack(spacing: 0) {
            ForEach(options, id: \.value) { option in
                let selected = selection == option.value
                Button {
                    withAnimation(UITokens.Effects.transitionNormal) {
                        selection = option.value
                    }
                } label: {
                    Text(option.label)
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(selected ? UITokens.Colors.textOnAction : UITokens.Colors.textSecondary)
                        .padding(.vertical, 7)
                        .padding(.horizontal, 14)
                        .frame(maxWidth: .infinity)
                        .background(
                            RoundedRectangle(cornerRadius: UITokens.Layout.radiusFull)
                                .fill(selected ? UITokens.Colors.actionPrimary : Color.clear)
                        )
                }
                .buttonStyle(.plain)
            }
        }
        .padding(3)
        .background(UITokens.Colors.componentWrappedBg.opacity(0.22))
        .clipShape(RoundedRectangle(cornerRadius: UITokens.Layout.radiusFull))
        .overlay(
            RoundedRectangle(cornerRadius: UITokens.Layout.radiusFull)
                .stroke(UITokens.Colors.componentWrappedBg.opacity(0.44), lineWidth: 1)
        )
    }
}

// MARK: - Spinner Component (Loading Indicator)
public struct UISpinner: View {
    public let size: CGFloat
    
    public init(size: CGFloat = 22.0) {
        self.size = size
    }
    
    @State private var isAnimating = false
    
    public var body: some View {
        Circle()
            .stroke(UITokens.Colors.componentWrappedBg.opacity(0.36), lineWidth: 2)
            .overlay(
                Circle()
                    .trim(from: 0.0, to: 0.25)
                    .stroke(UITokens.Colors.actionPrimary, lineWidth: 2)
            )
            .frame(width: size, height: size)
            .rotationEffect(Angle(degrees: isAnimating ? 360 : 0))
            .onAppear {
                withAnimation(Animation.linear(duration: 0.9).repeatForever(autoreverses: false)) {
                    isAnimating = true
                }
            }
    }
}

// MARK: - ProgressBar Component
public struct UIProgressBar: View {
    public let value: Double
    public let max: Double
    
    public init(value: Double, max: Double = 100.0) {
        self.value = value
        self.max = max
    }
    
    public var body: some View {
        let percent = max == 0 ? 0 : CGFloat(Swift.max(0.0, Swift.min(max, value)) / max)
        
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: UITokens.Layout.radiusFull)
                    .fill(UITokens.Colors.componentWrappedBg.opacity(0.28))
                    .frame(height: 10)
                
                RoundedRectangle(cornerRadius: UITokens.Layout.radiusFull)
                    .fill(
                        LinearGradient(
                            gradient: Gradient(colors: [
                                UITokens.Colors.actionPrimary,
                                UITokens.Colors.actionPrimaryHover
                            ]),
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .frame(width: geometry.size.width * percent, height: 10)
            }
        }
        .frame(height: 10)
    }
}

// MARK: - Badge Component
public enum UIBadgeTone {
    case `default`
    case info
    case error
    case success
    
    public var background: Color {
        switch self {
        case .`default`: return UITokens.Colors.componentWrappedBg.opacity(0.26)
        case .info: return UITokens.Colors.componentWrappedBg.opacity(0.34)
        case .error: return UITokens.Colors.systemError.opacity(0.28)
        case .success: return Color(hex: "#0baa65").opacity(0.28)
        }
    }
    
    public var foreground: Color {
        switch self {
        case .success: return Color(hex: "#0baa65")
        case .error: return UITokens.Colors.systemError
        default: return UITokens.Colors.textPrimary
        }
    }
}

public struct UIBadge: View {
    public let text: String
    public let tone: UIBadgeTone
    
    public init(_ text: String, tone: UIBadgeTone = .`default`) {
        self.text = text
        self.tone = tone
    }
    
    public var body: some View {
        Text(text)
            .font(.system(size: 11, weight: .bold))
            .foregroundColor(tone.foreground)
            .padding(.horizontal, 10)
            .padding(.vertical, 3)
            .background(tone.background)
            .clipShape(Capsule())
            .overlay(
                Capsule()
                    .stroke(UITokens.Colors.componentWrappedBg.opacity(0.48), lineWidth: 1)
            )
    }
}

// MARK: - Toast Configuration & Viewport
public enum UIToastTone {
    case info
    case success
    case error
    
    public var color: Color {
        switch self {
        case .info: return UITokens.Colors.componentWrappedText
        case .success: return Color(hex: "#0baa65")
        case .error: return UITokens.Colors.systemError
        }
    }
}

public struct UIToastItem: Identifiable, Equatable {
    public let id: UUID
    public let title: String
    public let description: String?
    public let tone: UIToastTone
    
    public init(title: String, description: String? = nil, tone: UIToastTone = .info) {
        self.id = UUID()
        self.title = title
        self.description = description
        self.tone = tone
    }
}

public class UIToastManager: ObservableObject {
    @Published public var items: [UIToastItem] = []
    
    public init() {}
    
    public func show(title: String, description: String? = nil, tone: UIToastTone = .info) {
        let item = UIToastItem(title: title, description: description, tone: tone)
        items.append(item)
        
        // Dismiss automatically after 3 seconds
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) { [weak self] in
            withAnimation(UITokens.Effects.transitionNormal) {
                self?.items.removeAll { $0.id == item.id }
            }
        }
    }
    
    public func dismiss(id: UUID) {
        items.removeAll { $0.id == id }
    }
}

public struct UIToastViewport: View {
    @ObservedObject public var manager: UIToastManager
    
    public init(manager: UIToastManager) {
        self.manager = manager
    }
    
    public var body: some View {
        VStack(spacing: 10) {
            ForEach(manager.items) { item in
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(item.title)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(UITokens.Colors.textPrimary)
                        if let desc = item.description {
                            Text(desc)
                                .font(.system(size: 12))
                                .foregroundColor(UITokens.Colors.textSecondary)
                        }
                    }
                    Spacer()
                    
                    Button {
                        withAnimation(UITokens.Effects.transitionQuick) {
                            manager.dismiss(id: item.id)
                        }
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(UITokens.Colors.textSecondary)
                    }
                    .buttonStyle(.plain)
                }
                .padding(12)
                .background(
                    Color.clear.background(.ultraThinMaterial)
                )
                .cornerRadius(UITokens.Layout.radiusControl)
                .overlay(
                    RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl)
                        .stroke(item.tone.color.opacity(0.4), lineWidth: 1.5)
                )
                .frame(maxWidth: 320)
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .padding()
    }
}

// MARK: - Dialog Component
public struct UIDialog: View {
    public let title: String
    public let description: String?
    public let confirmText: String
    public let cancelText: String
    public let onConfirm: () -> Void
    public let onCancel: () -> Void
    
    public init(
        title: String,
        description: String? = nil,
        confirmText: String = "Confirm",
        cancelText: String = "Cancel",
        onConfirm: @escaping () -> Void,
        onCancel: @escaping () -> Void
    ) {
        self.title = title
        self.description = description
        self.confirmText = confirmText
        self.cancelText = cancelText
        self.onConfirm = onConfirm
        self.onCancel = onCancel
    }
    
    public var body: some View {
        UIBox(padding: .normal, radius: .container, surface: true, border: true) {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 6) {
                    Text(title)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(UITokens.Colors.textPrimary)
                    
                    if let desc = description {
                        Text(desc)
                            .font(.system(size: 14))
                            .foregroundColor(UITokens.Colors.textSecondary)
                    }
                }
                
                HStack(spacing: 12) {
                    Spacer()
                    UIButton(cancelText, variant: .ghost) {
                        onCancel()
                    }
                    UIButton(confirmText, variant: .primary) {
                        onConfirm()
                    }
                }
            }
        }
        .frame(maxWidth: 340)
        .shadow(color: Color.black.opacity(0.4), radius: 30, x: 0, y: 15)
    }
}

// MARK: - Shimmer Skeleton Loader Component
public struct ShimmerModifier: ViewModifier {
    @State private var phase: CGFloat = 0
    
    public func body(content: Content) -> some View {
        content
            .overlay(
                GeometryReader { geometry in
                    let width = geometry.size.width
                    LinearGradient(
                        gradient: Gradient(colors: [
                            Color.clear,
                            Color.white.opacity(0.18),
                            Color.clear
                        ]),
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                    .frame(width: width * 1.5)
                    .offset(x: -width + (width * 2 * phase))
                }
            )
            .onAppear {
                withAnimation(Animation.linear(duration: 1.5).repeatForever(autoreverses: false)) {
                    phase = 1
                }
            }
    }
}

public struct UISkeleton: View {
    public let width: CGFloat?
    public let height: CGFloat
    public let rounded: Bool
    
    public init(width: CGFloat? = nil, height: CGFloat = 16, rounded: Bool = true) {
        self.width = width
        self.height = height
        self.rounded = rounded
    }
    
    public var body: some View {
        RoundedRectangle(cornerRadius: rounded ? UITokens.Layout.radiusControl : 0)
            .fill(UITokens.Colors.componentWrappedBg.opacity(0.24))
            .frame(width: width, height: height)
            .modifier(ShimmerModifier())
    }
}

// MARK: - Card Component
public struct UICard<Content: View>: View {
    public let content: () -> Content
    
    @Environment(\.colorScheme) var colorScheme
    
    public init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }
    
    public var body: some View {
        UIBox(padding: .normal, radius: .container, surface: true, blurPanel: true, border: true) {
            content()
        }
        .uiShadow(UITokens.Effects.surfaceShadow(for: colorScheme))
    }
}

// MARK: - Chip Component (Filter Tag)
public struct UIChip: View {
    public let selected: Bool
    public let label: String
    public let onClick: () -> Void
    
    public init(selected: Bool, label: String, onClick: @escaping () -> Void) {
        self.selected = selected
        self.label = label
        self.onClick = onClick
    }
    
    public var body: some View {
        Button(action: onClick) {
            Text(label)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(selected ? UITokens.Colors.textOnAction : UITokens.Colors.textSecondary)
                .padding(.horizontal, 14)
                .padding(.vertical, 6)
                .background(
                    Capsule()
                        .fill(selected ? UITokens.Colors.actionPrimary : UITokens.Colors.componentWrappedBg.opacity(0.18))
                )
                .overlay(
                    Capsule()
                        .stroke(selected ? UITokens.Colors.actionPrimary : UITokens.Colors.componentWrappedBg.opacity(0.4), lineWidth: 1)
                )
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Accordion Folding Panel Component
public struct UIAccordionItem {
    public let title: String
    public let content: AnyView
    
    public init<Content: View>(title: String, content: Content) {
        self.title = title
        self.content = AnyView(content)
    }
}

public struct UIAccordion: View {
    public let items: [UIAccordionItem]
    
    public init(items: [UIAccordionItem]) {
        self.items = items
    }
    
    public var body: some View {
        UIVStack(gap: .compact) {
            ForEach(items.indices, id: \.self) { idx in
                DisclosureGroup {
                    items[idx].content
                        .padding(.top, 8)
                } label: {
                    Text(items[idx].title)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(UITokens.Colors.textPrimary)
                }
                .padding(12)
                .background(UITokens.Colors.bgSurface.opacity(0.5))
                .cornerRadius(UITokens.Layout.radiusControl)
                .overlay(
                    RoundedRectangle(cornerRadius: UITokens.Layout.radiusControl)
                        .stroke(UITokens.Colors.componentWrappedBg.opacity(0.24), lineWidth: 1)
                )
            }
        }
    }
}

// MARK: - High-performance List Layout Wrapper
public struct UIList<Content: View>: View {
    public let content: () -> Content
    
    public init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }
    
    public var body: some View {
        ScrollView {
            LazyVStack(spacing: 8) {
                content()
            }
        }
    }
}

// MARK: - Namespace UI Components mapping helper
extension UI {
    public typealias Button = UIButton
    public typealias IconButton = UIIconButton
    public typealias FAB = UIFAB
    public typealias Menu = UIMenu
    public typealias TextField = UITextField
    public typealias TextArea = UITextArea
    public typealias Toggle = UIToggle
    public typealias Checkbox = UICheckbox
    public typealias RadioGroup = UIRadioGroup
    public typealias Slider = UISlider
    public typealias Segmented = UICustomSegmented
    public typealias Spinner = UISpinner
    public typealias ProgressBar = UIProgressBar
    public typealias Badge = UIBadge
    public typealias ToastViewport = UIToastViewport
    public typealias ToastItem = UIToastItem
    public typealias ToastTone = UIToastTone
    public typealias ToastManager = UIToastManager
    public typealias Dialog = UIDialog
    public typealias Skeleton = UISkeleton
    public typealias Card = UICard
    public typealias Chip = UIChip
    public typealias Accordion = UIAccordion
    public typealias AccordionItem = UIAccordionItem
    public typealias List = UIList
}
