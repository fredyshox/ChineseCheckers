//
//  BoardInfo.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 26.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct BoardInfo {
    private let _startId: Int
    private let _infos: [Int: FieldInfo]
    
    init? (dict: [String: Any]) {
        guard let start = dict["start"] as? Int,
              let fields = dict["fields"] as? [Int: [String: Any]]
        else { return nil }
        
        self._startId = start
        
        var infoDict = [Int: FieldInfo]()
        for (key, val) in fields {
            if let neighbours = val["neighbours"] as? [Int: String]{
                let fieldInfo = FieldInfo(id: key , dict: neighbours)
                infoDict[key] = fieldInfo
            }
        }
        self._infos = infoDict
    }
    
    var startId: Int {
        return _startId
    }
    
    var infos: [Int: FieldInfo] {
        return _infos
    }
    
}
