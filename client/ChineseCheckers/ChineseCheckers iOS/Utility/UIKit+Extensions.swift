//
//  dfsd.swift
//  UtilityKit
//
//  Created by Kacper Raczy on 07.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import UIKit

extension UISearchBar {
    func findBarTextField() -> UITextField! {
        let contentView = self.subviews[0]
        for view in contentView.subviews {
            if let textfield = view as? UITextField {
                return textfield
            }
        }
        return nil
    }
}


extension UIView {
    func snapshotImage() -> UIImage?{
        UIGraphicsBeginImageContext(self.bounds.size)
        self.drawHierarchy(in: self.bounds, afterScreenUpdates: false)
        let snapshotImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return snapshotImage
    }
    
    func snapshotView() -> UIView?{
        if let image = snapshotImage() {
            return UIImageView(image: image)
        }else {
            return nil
        }
    }
}


extension UINavigationBar {
    func setGradientBackground(colors: [UIColor]) {
        var frame = self.bounds
        frame.size.height += 20 //size of status bar
        setBackgroundImage(UIImage.gradient(size: frame.size, colors: colors), for: UIBarPosition.topAttached, barMetrics: UIBarMetrics.default)
    }
}


extension UIImage {
    static func gradient(size: CGSize, colors: [UIColor]) -> UIImage? {
        let cgColors = colors.map{$0.cgColor}
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        
        guard let context = UIGraphicsGetCurrentContext() else {return nil}
        defer {
            UIGraphicsEndImageContext()
        }
        
        let locations: [CGFloat] = [0.25, 1.0]
        guard let gradient = CGGradient(colorsSpace: CGColorSpaceCreateDeviceRGB(), colors: cgColors as CFArray, locations: locations)
            else {return nil}
        context.drawLinearGradient(gradient, start: CGPoint(x: 0.0,y:0.0), end: CGPoint(x:0.0,y:size.height), options: [])
        
        return UIGraphicsGetImageFromCurrentImageContext()
    }
    
    static func from(color: UIColor, size: CGSize) -> UIImage {
        UIGraphicsBeginImageContext(size)
        let context = UIGraphicsGetCurrentContext()
        context?.setFillColor(color.cgColor)
        context?.fill(CGRect(x: 0, y: 0, width: size.width, height: size.height))
        let image = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return image
    }
}


extension UIColor {
    static func hexStringToUIColor (hex:String) -> UIColor {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        
        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }
        
        if ((cString.count) != 6) {
            return UIColor.gray
        }
        
        var rgbValue:UInt32 = 0
        Scanner(string: cString).scanHexInt32(&rgbValue)
        
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}

extension UIWindow {
    public var visibleViewController: UIViewController? {
        get {
            return getDeepestPresentedViewController(from: rootViewController)
        }
    }
    
    public func getDeepestPresentedViewController(from vc: UIViewController?) -> UIViewController? {
        if let nc = vc as? UINavigationController {
            return getDeepestPresentedViewController(from: nc.visibleViewController)
        }else if let tbc = vc as? UITabBarController {
            return getDeepestPresentedViewController(from: tbc.selectedViewController)
        }else {
            if let pvc = vc?.presentedViewController {
                return getDeepestPresentedViewController(from: pvc)
            }else {
                return vc
            }
        }
    }
}

