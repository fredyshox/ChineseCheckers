//
//  Board.swift
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

class Board {
    private var _fields = [Int: Field]()
    private var _info: BoardInfo
    
    
    var fields:  [Int: Field] {
        get {
            return _fields
        }
        set {
            _fields = newValue
        }
    }
    
    var boardInfo: BoardInfo {
        return _info
    }
    
    init? (info: BoardInfo) {
        do {
            self._info = info
            try generateBoard(info: info)
        } catch let e as BoardInfoError{
            print("Error caught: \(e.localizedDescription)")
            return nil
        }catch {
            return nil
        }
    }
    
    private func generateBoard(info: BoardInfo) throws {
        let infoDict = info.infos
        let startId = info.startId
        guard let startFieldInfo = infoDict[startId]
        else {
            throw BoardInfoError()
        }
        let startField = Field(id: startFieldInfo.id)
        let fieldArr: [Field] = [startField]
        
        generateFields(arr: fieldArr, infos: infoDict)
    }
    
    private func generateFields(arr: [Field], infos: [Int: FieldInfo]) {
        if arr.count == 0 {
            return
        }
        
        var created: [Field] = []
        for field in arr {
            if let info = infos[field.id] {
                created.append(contentsOf: addField(info: info))
            }
        }
        
        generateFields(arr: created, infos: infos)
    }
    
    //Returns Field objects that have created
    private func addField(info: FieldInfo) -> [Field] {
        var createdFields: [Field] = []
        
        var field: Field
        if let f = self._fields[info.id] {
            field = f
        }else {
            field = Field(id: info.id)
        }
        
        self._fields[info.id] = field
        
        for(key, val) in info.neighbours {
            if let neighbourId = Int(val) {
                guard let dir = Field.Direction(rawValue: key)
                    else {
                        print("Error while parsing server board")
                        return []
                }
                
                if let neighbour = self._fields[neighbourId] {
                    field.setNeighbour(dir: dir, field: neighbour)
                }else {
                    let neighbour = Field(id: neighbourId)
                    field.setNeighbour(dir: dir, field: neighbour)
                    
                    createdFields.append(neighbour)
                    self._fields[neighbourId] = neighbour
                }
            }
        }
        
        return createdFields
    }
    
}
