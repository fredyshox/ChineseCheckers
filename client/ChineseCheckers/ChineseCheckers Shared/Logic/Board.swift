//
//  Board.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

/**
 An Object representing game's board graph structure.
 */

class Board {
    private var _fields: [Int: Field]
    private var _startID: Int
    
    
    var fields:  [Int: Field] {
        get {
            return _fields
        }
        set {
            _fields = newValue
        }
    }
    
    var startID: Int {
        get{
            return _startID
        }
        set{
            _startID = newValue
        }
    }
    
    init(startID: Int) {
        self._startID = startID
        self._fields = [Int: Field]()
    }
    
    
}
