//
//  HexagonNode.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import SpriteKit

class HexagonNode: SKShapeNode {
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Not implemented")
    }
    
    init(size: CGFloat) {
        super.init()
        
        let path = hexagonPath(radius: size/2)
        
        self.path = path
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
