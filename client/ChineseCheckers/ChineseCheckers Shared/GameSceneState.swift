//
//  GameSceneState.swift
//  ChineseCheckers macOS
//
//  Created by Kacper Raczy on 28.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import SpriteKit

/**
 Enables to create objects for maintaining checker interactivity.
 
 It's using state design pattern.
 */

protocol GameSceneState {
    func performMove(scene: GameScene, to hex: HexagonNode)
    func setPossibleVisibility(scene: GameScene, hex: HexagonNode);
    func changeState(scene: GameScene, state: GameSceneState);
    static var shared: GameSceneState {get}
}

extension GameSceneState {
    func changeState(scene: GameScene, state: GameSceneState) {
        scene.state = state
    }
}

// TODO implementation
class WaitingState: GameSceneState {
    private static var _shared: WaitingState?
    public static var shared: GameSceneState {
        if _shared == nil {
            _shared = WaitingState()
        }
        
        return _shared!
    }
    
    func performMove(scene: GameScene, to hex: HexagonNode) { }
    func setPossibleVisibility(scene: GameScene, hex: HexagonNode) { }
}

/**
 State object which is used when Scene is waiting for user to
 select checker.
 */

class ListeningState: GameSceneState {
    private static var _shared: ListeningState?
    public static var shared: GameSceneState {
        if _shared == nil {
            _shared = ListeningState()
        }
        
        return _shared!
    }
    
    func performMove(scene: GameScene, to hex: HexagonNode) {
        //nothing
    }
    
    func setUpPossibleHex(scene: GameScene, hex: HexagonNode) {
        hex.delegate = scene
        hex.lineWidth = GameScene.selectionLineWidth
        hex.strokeColor = GameScene.checkerColor(zone: scene.player.zoneID)
    }
    
    func setPossibleVisibility(scene: GameScene, hex: HexagonNode) {
        guard let fieldHex = scene.findFieldHex(point: hex.position) else {return}
        if let idx = scene.hexIndex(checkerName: fieldHex.name!),
            let field = scene.session.board.fields[idx] {
            let possible = scene.session.possibleMoves(from: field)
            for p in possible {
                if let node = scene.childNode(withName: scene.hexName(id: p.id)) as? HexagonNode {
                    setUpPossibleHex(scene: scene, hex: node)
                }
            }
        }
        
        (SelectedHexState.shared as! SelectedHexState).checker = hex
        self.changeState(scene: scene, state: SelectedHexState.shared)
    }
}

/**
 State object which is used when user's choosen checker, and it enables
 to perform move or clear selection.
 */

class SelectedHexState: GameSceneState {
    private static var _shared: SelectedHexState?
    private var _checker: HexagonNode?
    
    public static var shared: GameSceneState {
        if _shared == nil {
            _shared = SelectedHexState()
        }
        
        return _shared!
    }
    
    public var checker: HexagonNode? {
        get{
            return _checker
        }
        set{
            _checker = newValue
        }
    }
    
    func performMove(scene: GameScene, to hex: HexagonNode) {
        if let checker = checker {
            let checkerFieldName = scene.findFieldHex(point: checker.position)?.name
            
            let service = scene.service
            if  let fieldName = hex.name,
                let fieldIndex = scene.hexIndex(checkerName: fieldName),
                let checkerFieldName = checkerFieldName,
                let checkerFieldIndex = scene.hexIndex(checkerName: checkerFieldName) {
                let info = GameInfo(date: Date(), from: checkerFieldIndex, to: fieldIndex)
                
                service.sendMove(gameInfo: info)
            }
        }
        
        setPossibleVisibility(scene: scene, hex: hex)
    }
    
    func setPossibleVisibility(scene: GameScene, hex: HexagonNode) {
        for childNode in scene.children {
            if let hex = childNode as? HexagonNode, let name = hex.name {
                if name.starts(with: "h_") {
                    resetHexNode(hex: hex)
                }
            }
        }
        
        changeState(scene: scene, state: ListeningState.shared)
    }
    
    func changeState(scene: GameScene, state: GameSceneState) {
        self._checker = nil
        scene.state = state
    }
    
    private func resetHexNode(hex: HexagonNode) {
        hex.delegate = nil
        hex.lineWidth = 0.0
        hex.strokeColor = SKColor.clear
    }
}
