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
        
        static let allValues: [Direction] = [topRight, right, bottomRight, bottomLeft, left ,topLeft]
    }
    
    private let _id: Int
    private var _neighbours: [Field?]
    private var _player: Player?
    
    init(id: Int) {
        self._id = id
        self._neighbours = [Field?](repeating: nil, count: 6)
    }
    
    convenience init(_ id: Int, player: Player?) {
        self.init(id: id)
        self.player = player
    }
    
    var id: Int {
        return self._id
    }
    
    var neighbours: [Field?] {
        return self._neighbours
    }
    
    var player: Player? {
        get{
            return _player
        }
        set{
            self._player = newValue
        }
    }
    
    var isOccupied: Bool {
        return self._player != nil
    }
    
    func getNeighbour(dir: Direction) -> Field? {
        return self._neighbours[dir.rawValue]
    }
    
    func setNeighbour(dir: Direction, field: Field?) {
        self._neighbours[dir.rawValue] = field
    }
    
    
}
