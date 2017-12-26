//
//  Field.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

class Field {
    enum Direction: Int {
        case topRight = 0
        case right
        case bottomRight
        case bottomLeft
        case left
        case topLeft
        
        
        func opposite() ->Direction {
            let val = (self.rawValue + 3) % 6
            let dir = Direction(rawValue: val)!
            
            return dir
        }
    }
    
    private let _id: Int
    private var _neighbours: [Field?]
    
    init(id: Int) {
        self._id = id
        self._neighbours = [Field?](repeating: nil, count: 6)
    }
    
    var id: Int {
        return self._id
    }
    
    var neighbours: [Field?] {
        return self._neighbours
    }
    
    func getNeighbour(dir: Direction) -> Field? {
        return self._neighbours[dir.rawValue]
    }
    
    func setNeighbour(dir: Direction, field: Field?) {
        self._neighbours[dir.rawValue] = field
    }
    
    
}
