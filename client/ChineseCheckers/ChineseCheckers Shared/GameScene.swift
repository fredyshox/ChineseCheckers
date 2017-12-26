//
//  GameScene.swift
//  ChineseCheckers Shared
//
//  Created by Kacper Raczy on 25.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import SpriteKit

class GameScene: SKScene {
    
    
    fileprivate var label : SKLabelNode?
    fileprivate var spinnyNode : SKShapeNode?
    fileprivate var board: Board!
    
    fileprivate static let hexSize: CGFloat = 40

    
    class func newGameScene() -> GameScene {
        // Load 'GameScene.sks' as an SKScene.
        guard let scene = SKScene(fileNamed: "GameScene") as? GameScene else {
            print("Failed to load GameScene.sks")
            abort()
        }
        
        // Set the scale mode to scale to fit the window
        scene.scaleMode = .aspectFill
        
        return scene
    }
    
    func setUpScene() {
        let node = HexagonNode(size: 40)
        node.position = CGPoint(x: self.frame.midX,
                                y: self.frame.midY)
        node.fillColor = NSColor.red
        self.addChild(node)
    }
    
    func setUpBoard() {
        let startId = self.board.boardInfo.startId
        let startField = self.board.fields[startId]
        
        var center = CGPoint(x: self.frame.midX,
                             y: self.frame.midY)
        var node = createFieldHex(id: hexName(id: startId))
        node.position = center
        
        
        
    }
    
    func createNeighbours(field: Field) {
        let hexName: String = self.hexName(id: field.id)
        guard let hexNode = self.childNode(withName: hexName)
        else {return}
        
        var tempName: String
        
        for (index, n) in field.neighbours.enumerated() {
            if let neighbour = n{
                tempName = self.hexName(id: neighbour.id)
                if self.childNode(withName: tempName) == nil {
                    let newNode = createFieldHex(id: tempName)
                    
                }
            }
        }
    }
    
    
    func hexName(id: Int) -> String {
        return "h_\(id)"
    }
    
    func createFieldHex(id: String) -> HexagonNode{
        let node = HexagonNode(size: GameScene.hexSize)
        node.name = id
        node.fillColor = NSColor.blue
        
        return node
    }
    
    
    
    #if os(watchOS)
    override func sceneDidLoad() {
        self.setUpScene()
    }
    #else
    override func didMove(to view: SKView) {
        self.setUpScene()
    }
    #endif
    
    override func update(_ currentTime: TimeInterval) {
        // Called before each frame is rendered
    }
}

#if os(iOS) || os(tvOS)
// Touch-based event handling
extension GameScene {

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        
    }
    
    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        
    }
    
   
}
#endif

#if os(OSX)
// Mouse-based event handling
extension GameScene {

    override func mouseDown(with event: NSEvent) {
        
    }
    
    override func mouseDragged(with event: NSEvent) {
     
    }
    
    override func mouseUp(with event: NSEvent) {
       
    }

}
#endif

