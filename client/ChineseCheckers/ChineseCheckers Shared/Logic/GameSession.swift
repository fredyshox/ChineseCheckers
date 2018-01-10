//
//  GameSession.swift
//  ChineseCheckers macOS
//
//  Created by Kacper Raczy on 28.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

/**
 An object being main class for game logic.
 It's generating board from provided info and has references to players.
 It can also provide possible moves and info about current turn.
 */

class GameSession {
    private let _board: Board
    private let _players: [Player]
    private var _delegate: GameSessionDelegate?
    private var _currentPlayer: Player!
    
    init? (binfo: BoardInfo, players: [Player]) {
        do {
            self._players = players
            self._board = Board(startID: binfo.startId)
            try generateBoard(info: binfo)
        } catch let e as BoardInfoError{
            print("Error caught: \(e.localizedDescription)")
            return nil
        }catch {
            return nil
        }
    }
    
    var board: Board {
        return _board
    }
    
    //MARK: Delegate
    
    var delegate: GameSessionDelegate? {
        get{
            return self._delegate
        }
        set{
            self._delegate = newValue
        }
    }
    
    //MARK: Players
    
    var players: [Player] {
        return _players
    }
    
    var currentPlayer: Player {
        get {
            return _currentPlayer
        }
        set{
            _currentPlayer = newValue
            self._delegate?.turnChanges(session: self, player: newValue)
        }
    }
    
    public func findPlayer(zoneID: Int) -> Player? {
        for player in _players {
            if player.zoneID == zoneID {
                return player
            }
        }
        
        return nil
    }
    
    public func findPlayer(id: Int) -> Player? {
        for player in _players {
            if player.id == id {
                return player
            }
        }
        
        return nil
    }
    
    //MARK: Board generation
    
    private func generateBoard(info: BoardInfo) throws {
        let infoDict = info.infos
        let startId = info.startId
        guard let startFieldInfo = infoDict[startId]
            else {
                throw BoardInfoError()
        }
        
        var player: Player? = nil
        if let playerID = startFieldInfo.player  {
            player = findPlayer(id: playerID)
        }
        let startField = Field(startFieldInfo.id, player: player)
        
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
        if let f = self._board.fields[info.id] {
            field = f
        }else {
            field = Field(id: info.id)
        }
        
        var player: Player? = nil
        if let playerID = info.player  {
            player = findPlayer(id: playerID)
        }
        field.player = player
        
        self.board.fields[info.id] = field
        
        for(key, val) in info.neighbours {
            if let neighbourId = Int(val) {
                guard let dir = Field.Direction(rawValue: key)
                    else {
                        print("Error while parsing server board")
                        return []
                }
                
                if let neighbour = self._board.fields[neighbourId] {
                    field.setNeighbour(dir: dir, field: neighbour)
                }else {
                    let neighbour = Field(id: neighbourId)
                    field.setNeighbour(dir: dir, field: neighbour)
                    
                    createdFields.append(neighbour)
                    self._board.fields[neighbourId] = neighbour
                }
            }
        }
        
        return createdFields
    }
    
    //MARK: Possible moves, move validation
    
    func possibleMoves(from field: Field) -> [Field] {
        var result: [Field] = []
        guard field.player != nil else {return []}
        
        for (index, n) in field.neighbours.enumerated() {
            if let direction = Field.Direction(rawValue: index),
                let possible = possibleMove(direction: direction, fromField: n, jump: false) {
                result.append(possible)
            }
        }
        
        
        return result
    }
    
    func possibleMove(direction: Field.Direction, fromField: Field?, jump: Bool) -> Field? {
        if let field = fromField {
            if !field.isOccupied {
                return field
            }
            
            if (!jump) {
                return possibleMove(direction: direction, fromField: field.getNeighbour(dir: direction), jump: true)
            }
        }
        
        return nil
    }
    
    //should be used only by server service
    func performMove(info: GameInfo) {
        guard let fromField = self.board.fields[info.oldFieldID],
              let player = fromField.player,
              let toField = self.board.fields[info.newFieldID]
        else {return}
        
        print("moving")
        fromField.player = nil
        toField.player = player
        
        self._delegate?.boardChanged(session: self, info: info)
    }
    
}
