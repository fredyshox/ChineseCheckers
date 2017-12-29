//
//  GameSceneState.swift
//  ChineseCheckers macOS
//
//  Created by Kacper Raczy on 28.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import SpriteKit

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



class OffState: GameSceneState {
    private static var _shared: OffState?
    public static var shared: GameSceneState {
        if _shared == nil {
            _shared = OffState()
        }
        
        return _shared!
    }
    
    func performMove(scene: GameScene, to hex: HexagonNode) {
        //nothing
    }
    
    func setUpPossibleHex(scene: GameScene, hex: HexagonNode) {
        hex.delegate = scene
        hex.lineWidth = 4.0
        hex.strokeColor = NSColor.red
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
        
        (OnState.shared as! OnState).checker = hex
        self.changeState(scene: scene, state: OnState.shared)
    }
}

class OnState: GameSceneState {
    private static var _shared: OnState?
    private var _checker: HexagonNode?
    
    public static var shared: GameSceneState {
        if _shared == nil {
            _shared = OnState()
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
            let moveAction = SKAction.move(to: hex.position, duration: 0.3)
            let checkerFieldName = scene.findFieldHex(point: checker.position)?.name
            
            checker.run(moveAction, completion: {
                let session = scene.session
                if  let fieldName = hex.name,
                    let fieldIndex = scene.hexIndex(checkerName: fieldName),
                    let checkerFieldName = checkerFieldName,
                    let checkerFieldIndex = scene.hexIndex(checkerName: checkerFieldName) {
                    let info = GameInfo(date: Date(), from: checkerFieldIndex, to: fieldIndex)
                    
                    //should send to server
                    session.performMove(info: info)
                }
            })
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
        
        changeState(scene: scene, state: OffState.shared)
    }
    
    func changeState(scene: GameScene, state: GameSceneState) {
        self._checker = nil
        scene.state = state
    }
    
    private func resetHexNode(hex: HexagonNode) {
        hex.delegate = nil
        hex.lineWidth = 0.0
        hex.strokeColor = NSColor.clear
    }
}
