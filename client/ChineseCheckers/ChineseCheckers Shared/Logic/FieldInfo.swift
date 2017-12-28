//
//  FieldInfo.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct FieldInfo {
    private let _id: Int
    private let _neighbours: [Int: String]
    private let _player: Int?
    
    init(id: Int, dict: [Int: String], player: Int?) {
        self._id = id
        self._neighbours = dict
        self._player = player
    }
    
    var id: Int {
        return _id
    }
    
    var neighbours: [Int:String] {
        return _neighbours
    }
    
    var player: Int? {
        return _player
    }
    
}
