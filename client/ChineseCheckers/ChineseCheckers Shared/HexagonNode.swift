//
//  HexagonNode.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import SpriteKit

class HexagonNode: SKShapeNode {
    fileprivate let _radius: CGFloat
    fileprivate var _delegate: HexagonNodeDelegate?
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Not implemented")
    }
    
    init(size: CGFloat) {
        self._radius = size/2
        super.init()
        
        let path = hexagonPath(radius: _radius)
        
        self.path = path
    }
    
    var radius: CGFloat {
        return _radius
    }
    
    var delegate: HexagonNodeDelegate? {
        get{
            return _delegate
        }
        set{
            self._delegate = newValue
            self.isUserInteractionEnabled = (newValue != nil)
        }
    }

    func hexagonPath(radius: CGFloat) -> CGPath {
        let path = CGMutablePath()
        
        path.move(to: hexCorner(i: 0, radius: radius))
        for i in 1..<6 {
            path.addLine(to: hexCorner(i: i, radius: radius))
        }
        path.closeSubpath()
        
        return path
    }
    
    func hexCorner(i: Int, radius: CGFloat) -> CGPoint {
        let center = self.position
        
        let angleDeg: CGFloat = 60.0 * CGFloat(i) + 30.0
        let angleRad: CGFloat = CGFloat.pi * angleDeg / 180
        
        let result = CGPoint(x: center.x + radius * cos(angleRad),
                             y: center.y + radius * sin(angleRad))
        
        return result
    }
    
}

#if os(iOS)
extension HexagonNode {
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self._delegate?.hexNodeClicked(self)
        log.info("Touched: \(self.name)")
    }
}
#endif

#if os(OSX)
extension HexagonNode {
    override func mouseDown(with event: NSEvent) {
        self._delegate?.hexNodeClicked(self)
        log.info("Clicked: \(self.name!)")
    }
}
#endif
