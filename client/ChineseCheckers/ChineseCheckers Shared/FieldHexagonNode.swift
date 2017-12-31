//
//  FieldHexagonNode.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 29.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import SpriteKit

class FieldHexagonNode: HexagonNode {
    private var _strokeWidth: CGFloat = 0
    private var _childHex: HexagonNode!
    
    #if os(OSX)
    private var _lineColor: NSColor?
    #elseif os(iOS)
    private var _lineColor: UIColor?
    #endif
    
    init(size: CGFloat, strokeWidth: CGFloat) {
        let childSize = size - 2*strokeWidth
        
        self._strokeWidth = strokeWidth
        self._childHex = HexagonNode(size: childSize)
        super.init(size: size)
        
        initializeChildHex(childNode: self._childHex)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Not implemented")
    }
    
    private func initializeChildHex(childNode: HexagonNode) {
        self.addChild(childNode)
        childNode.position = self.position
    }
    
    #if os(OSX)
    var lineColor: NSColor? {
        get{
            return self._lineColor
        }
        set{
            if lineColor != nil {
                super.fillColor = lineColor!
            }else {
                super.fillColor = self._childHex.fillColor
            }
            
            self._lineColor = newValue
        }
    }
    
    override var fillColor: NSColor {
        get{
            return super.fillColor
        }
        set{
            super.fillColor = newValue
            self._childHex.fillColor = newValue
        }
    }
    #elseif os(iOS)
    var lineColor: UIColor? {
        get{
            return self._lineColor
        }
        set{
            if lineColor != nil {
                super.fillColor = lineColor!
            }else {
                super.fillColor = self._childHex.fillColor
            }
            self._lineColor = newValue
        }
    }
    
    override var fillColor: UIColor {
        get{
            return super.fillColor
        }
        set{
            super.fillColor = newValue
            self._childHex.fillColor = newValue
        }
    }
    #endif
    
}
