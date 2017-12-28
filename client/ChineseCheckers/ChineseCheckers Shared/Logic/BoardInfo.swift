//
//  BoardInfo.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

class BoardInfoError: Error {
    var localizedDescription: String {
        return "Corrupted BoardInfo object"
    }
}

struct BoardInfo {
    private let _startId: Int
    private let _infos: [Int: FieldInfo]
    
    init? (dict: [String: Any]) {
        guard let start = dict["start"] as? Int,
              let fields = dict["fields"] as? [String: [String: Any]]
        else { return nil }
        
        self._startId = start
        
        var infoDict = [Int: FieldInfo]()
        for (key, val) in fields {
            if let neighbours = val["neighbours"] as? [String: String]{
                let player = val["player"] as? Int
                let converted = BoardInfo.convertDict(dict: neighbours)
                
                if let intKey = Int(key) {
                    let fieldInfo = FieldInfo(id: intKey , dict: converted, player: player)
                    infoDict[intKey] = fieldInfo
                }
            }
        }
        self._infos = infoDict
    }
    
    private static func convertDict(dict: [String: String]) -> [Int: String] {
        var newDict = [Int:String]()
        for(key, val) in dict {
            if let id = Int(key) {
                newDict[id] = val
            }
        }
        
        return newDict
    }
    
    var startId: Int {
        return _startId
    }
    
    var infos: [Int: FieldInfo] {
        return _infos
    }
    
}
