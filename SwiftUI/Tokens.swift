import SwiftUI

#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

// MARK: - Color Hex Initialization Helpers
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue:  Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
    
    static func dynamicColor(lightHex: String, darkHex: String) -> Color {
        let lightColor = Color(hex: lightHex)
        let darkColor = Color(hex: darkHex)
        
        #if canImport(UIKit)
        return Color(UIColor { traitCollection in
            return traitCollection.userInterfaceStyle == .dark ? UIColor(darkColor) : UIColor(lightColor)
        })
        #elseif canImport(AppKit)
        return Color(NSColor(name: nil) { appearance in
            return appearance.bestMatch(from: [.darkAqua, .aqua]) == .darkAqua ? NSColor(darkColor) : NSColor(lightColor)
        })
        #else
        return lightColor
        #endif
    }
}

// MARK: - Design Tokens Namespace
public enum UITokens {
    
    // MARK: - Typography Levels
    public enum Typography {
        public static let fontName = "System"
        
        public static var heading1: Font {
            .system(size: 34, weight: .bold, design: .default)
        }
        public static var heading2: Font {
            .system(size: 26, weight: .bold, design: .default)
        }
        public static var heading3: Font {
            .system(size: 20, weight: .bold, design: .default)
        }
        public static var heading4: Font {
            .system(size: 17, weight: .semibold, design: .default)
        }
        public static var bodyPrimary: Font {
            .system(size: 15, weight: .medium, design: .default)
        }
        public static var bodySecondary: Font {
            .system(size: 14, weight: .regular, design: .default)
        }
        public static var caption: Font {
            .system(size: 12, weight: .regular, design: .default)
        }
    }
    
    // MARK: - Color Tokens
    public enum Colors {
        // Theme Primitives & Semantics Map
        public static let bgBase = Color.dynamicColor(lightHex: "#FAFAFA", darkHex: "#0E1722")
        public static let bgSurface = Color.dynamicColor(lightHex: "#FFFFFF", darkHex: "#152131")
        
        public static let textPrimary = Color.dynamicColor(lightHex: "#212121", darkHex: "#FAFAFA")
        public static let textSecondary = Color.dynamicColor(lightHex: "#616161", darkHex: "#EEEEEE")
        
        public static let componentWrappedBg = Color.dynamicColor(lightHex: "#C8FFF8", darkHex: "#00CDBD")
        public static let componentWrappedText = Color.dynamicColor(lightHex: "#00756D", darkHex: "#EAFFFC")
        
        public static let actionPrimary = Color.dynamicColor(lightHex: "#E65100", darkHex: "#FF6D00")
        public static let actionPrimaryHover = Color.dynamicColor(lightHex: "#BF360C", darkHex: "#E65100")
        public static let textOnAction = Color.dynamicColor(lightHex: "#FFFFFF", darkHex: "#212121")
        
        public static let systemError = Color.dynamicColor(lightHex: "#EF5350", darkHex: "#CF6679")
        
        // Static Primitives
        public static let blackOled = Color(hex: "#121212")
        public static let whitePure = Color(hex: "#FFFFFF")
        
        // Gray palette helper
        public static let gray50 = Color(hex: "#FAFAFA")
        public static let gray100 = Color(hex: "#F5F5F5")
        public static let gray200 = Color(hex: "#EEEEEE")
        public static let gray700 = Color(hex: "#616161")
        public static let gray800 = Color(hex: "#424242")
        public static let gray900 = Color(hex: "#212121")
        
        // Teal palette helper
        public static let teal50 = Color(hex: "#EAFFFC")
        public static let teal100 = Color(hex: "#C8FFF8")
        public static let teal500 = Color(hex: "#0FF7E5")
        public static let teal700 = Color(hex: "#00CDBD")
        public static let teal900 = Color(hex: "#00756D")
    }
    
    // MARK: - Layout Tokens
    public enum Layout {
        public static let spacingCompact: CGFloat = 8.0
        public static let spacingNormal: CGFloat = 16.0
        public static let spacingLoose: CGFloat = 24.0
        
        public static let radiusControl: CGFloat = 10.0
        public static let radiusContainer: CGFloat = 15.0
        public static let radiusFull: CGFloat = 9999.0
    }
    
    // MARK: - Effect & Animation Tokens
    public enum Effects {
        // Blur metrics
        public static func panelBlur(for scheme: ColorScheme) -> CGFloat {
            scheme == .dark ? 22.0 : 18.0
        }
        
        public static func panelOpacity(for scheme: ColorScheme) -> Double {
            scheme == .dark ? 0.72 : 0.84
        }
        
        // Dynamic Glow Shadows
        public static func glowShadow(for scheme: ColorScheme) -> ShadowStyle {
            if scheme == .dark {
                return ShadowStyle(
                    color1: Color(hex: "#FF6D00").opacity(0.34), radius1: 38, x1: 0, y1: 14,
                    color2: Color(hex: "#00756D").opacity(0.24), radius2: 14, x2: 0, y2: 4
                )
            } else {
                return ShadowStyle(
                    color1: Color(hex: "#0FF7E5").opacity(0.24), radius1: 26, x1: 0, y1: 10,
                    color2: Color(hex: "#00756D").opacity(0.12), radius2: 12, x2: 0, y2: 4
                )
            }
        }
        
        // Dynamic Surface Shadows
        public static func surfaceShadow(for scheme: ColorScheme) -> ShadowStyle {
            if scheme == .dark {
                return ShadowStyle(
                    color1: Color(hex: "#040C18").opacity(0.52), radius1: 40, x1: 0, y1: 18,
                    color2: Color.white.opacity(0.06), radius2: 0, x2: 0, y2: 1 // inner shadow simulation
                )
            } else {
                return ShadowStyle(
                    color1: Color(hex: "#00756D").opacity(0.16), radius1: 30, x1: 0, y1: 14,
                    color2: Color.white.opacity(0.92), radius2: 0, x2: 0, y2: 1 // inner shadow simulation
                )
            }
        }
        
        // Dynamic Soft Shadows
        public static func softShadow(for scheme: ColorScheme) -> ShadowStyle {
            if scheme == .dark {
                return ShadowStyle(
                    color1: Color(hex: "#081626").opacity(0.38), radius1: 24, x1: 0, y1: 10,
                    color2: .clear, radius2: 0, x2: 0, y2: 0
                )
            } else {
                return ShadowStyle(
                    color1: Color(hex: "#00756D").opacity(0.12), radius1: 18, x1: 0, y1: 8,
                    color2: .clear, radius2: 0, x2: 0, y2: 0
                )
            }
        }
        
        // Transitions
        public static let transitionQuick = Animation.timingCurve(0.22, 1, 0.36, 1, duration: 0.18)
        public static let transitionNormal = Animation.timingCurve(0.22, 1, 0.36, 1, duration: 0.30)
        public static let transitionSlow = Animation.timingCurve(0.22, 1, 0.36, 1, duration: 0.46)
    }
}

// MARK: - Shadow Style Struct Helper
public struct ShadowStyle {
    public let color1: Color
    public let radius1: CGFloat
    public let x1: CGFloat
    public let y1: CGFloat
    
    public let color2: Color
    public let radius2: CGFloat
    public let x2: CGFloat
    public let y2: CGFloat
}

// MARK: - Shadow Modifier
public struct ShadowModifier: ViewModifier {
    let shadow: ShadowStyle
    
    public func body(content: Content) -> some View {
        content
            .shadow(color: shadow.color1, radius: shadow.radius1, x: shadow.x1, y: shadow.y1)
            .shadow(color: shadow.color2, radius: shadow.radius2, x: shadow.x2, y: shadow.y2)
    }
}

extension View {
    public func uiShadow(_ shadow: ShadowStyle) -> some View {
        self.modifier(ShadowModifier(shadow: shadow))
    }
}

// MARK: - Quantum Background (Vibrant Glass & Blur)
public struct QuantumBackground: View {
    @Environment(\.colorScheme) var colorScheme
    
    public init() {}
    
    public var body: some View {
        ZStack {
            // Base Linear Gradient
            if colorScheme == .dark {
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color(hex: "#0D151F"),
                        Color(hex: "#0E1722")
                    ]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            } else {
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color(hex: "#FAFBFB"),
                        Color(hex: "#FAFAFA")
                    ]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
            }
            
            // Overlapping Radial Gradients
            GeometryReader { geometry in
                let width = geometry.size.width
                let height = geometry.size.height
                let maxLen = max(width, height)
                
                ZStack {
                    // Top-right component-wrapped-bg glow
                    RadialGradient(
                        gradient: Gradient(colors: [
                            UITokens.Colors.componentWrappedBg.opacity(colorScheme == .dark ? 0.32 : 0.54),
                            .clear
                        ]),
                        center: .init(x: 1.0, y: 0.0),
                        startRadius: 0,
                        endRadius: maxLen * (colorScheme == .dark ? 0.46 : 0.48)
                    )
                    
                    // Left middle-top glow
                    RadialGradient(
                        gradient: Gradient(colors: [
                            UITokens.Colors.componentWrappedBg.opacity(colorScheme == .dark ? 0.24 : 0.42),
                            .clear
                        ]),
                        center: .init(x: 0.0, y: 0.25),
                        startRadius: 0,
                        endRadius: maxLen * (colorScheme == .dark ? 0.42 : 0.40)
                    )
                    
                    // Bottom-right action-primary glow
                    RadialGradient(
                        gradient: Gradient(colors: [
                            UITokens.Colors.actionPrimary.opacity(colorScheme == .dark ? 0.18 : 0.14),
                            .clear
                        ]),
                        center: .init(x: 0.88, y: 0.92),
                        startRadius: 0,
                        endRadius: maxLen * (colorScheme == .dark ? 0.36 : 0.32)
                    )
                }
            }
        }
        .ignoresSafeArea()
    }
}

// MARK: - View Background Modifier Helper
public struct QuantumBackgroundModifier: ViewModifier {
    public func body(content: Content) -> some View {
        ZStack {
            QuantumBackground()
            content
        }
    }
}

extension View {
    public func quantumBackground() -> some View {
        self.modifier(QuantumBackgroundModifier())
    }
}
