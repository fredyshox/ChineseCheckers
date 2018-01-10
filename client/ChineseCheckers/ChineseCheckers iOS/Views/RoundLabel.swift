//
//  RoundLabel.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 03.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit

/**
 UILabel with rounded corners.
 */

class RoundLabel: UILabel {
    
    private var _padding = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    
    convenience init() {
        self.init(frame: CGRect.zero)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    var cornerRadius: CGFloat {
        get {
            return self.layer.cornerRadius
        }
        set{
            padding = newValue
            self.layer.cornerRadius = newValue
        }
    }
    
    private var padding: CGFloat {
        get {
            return _padding.top
        }
        set{
            _padding.top = newValue
            _padding.bottom = newValue
            _padding.left = newValue
            _padding.right = newValue
        }
    }
    
    override var text: String? {
        get {
            return super.text
        }
        set{
            super.text = newValue
            self.sizeToFit()
        }
    }
    
    override var intrinsicContentSize: CGSize {
        return sizeWithPadding(size: super.intrinsicContentSize)
    }
    
    override func sizeThatFits(_ size: CGSize) -> CGSize {
        return sizeWithPadding(size: super.sizeThatFits(size))
    }
    
    private func sizeWithPadding(size: CGSize) -> CGSize {
        return CGSize(width: size.width + _padding.left + _padding.right, height: size.height + _padding.bottom + _padding.top)
    }
    
    override func drawText(in rect: CGRect) {
        super.drawText(in: UIEdgeInsetsInsetRect(rect, _padding))
    }
    
}
