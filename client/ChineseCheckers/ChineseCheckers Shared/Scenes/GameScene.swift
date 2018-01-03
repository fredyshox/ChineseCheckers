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
    fileprivate var _service: GameService!
    fileprivate var _state: GameSceneState = ListeningState.shared
    fileprivate var player: Player!
    
    fileprivate var playerNodes: [HexagonNode] = []
    
    fileprivate static let hexSize: CGFloat = 40
    fileprivate static let hexOffset: CGFloat = 5
    public static let selectionLineWidth: CGFloat = 2.0
    public static let moveDuration: TimeInterval = 0.3
    

    public var board: Board {
        return session.board
    }
    
    public var state: GameSceneState {
        get {
            return _state
        }
        set {
            _state = newValue
        }
    }
    
    public var session: GameSession {
        return _service.session
    }
    
    public var service: GameService {
        get{
            return _service
        }
        set{
            _service = newValue
        }
    }
    
    class func newScene(service: GameService) -> GameScene {
        // Load 'GameScene.sks' as an SKScene.
        guard let scene = SKScene(fileNamed: "GameScene") as? GameScene else {
            print("Failed to load GameScene.sks")
            abort()
        }
        
        scene._service = service
    
        let id = service.playerID!
        scene.player = service.session.findPlayer(id: id)!
        service.session.delegate = scene
        
        // Set the scale mode to scale to fit the window
        scene.scaleMode = .aspectFill
        
        return scene
    }
    
    func setUpScene() {
        setUpBoard()
    }
    
    //MARK: Board creation & drawing
    
    func setUpBoard() {
        let startId = self.board.startID
        let startField = self.board.fields[startId]!
        
        let center = CGPoint(x: self.frame.midX,
                             y: self.frame.midY)
        let node = createFieldHex(id: hexName(id: startId))
        node.position = center
        self.addChild(node)
        
        self.playerNodes = []
        
        createNeighbours(arr: [startField])
        createCheckers(fields: self.board.fields.map({$0.value}))
    }
    
    func createNeighbours(arr: [Field]) {
        if arr.count == 0 {
            return
        }
        
        var created: [Field] = []
        for field in arr {
            created.append(contentsOf: createNeighboursHex(field: field))
        }
        
        createNeighbours(arr: created)
    }
    
    func createNeighboursHex(field: Field) -> [Field] {
        var createdFields: [Field] = []
        
        let hexName: String = self.hexName(id: field.id)
        guard let hexNode = self.childNode(withName: hexName) as? HexagonNode
        else {return [] /*ERROR*/}
        
        var tempName: String
        
        for direction in Field.Direction.allValues {
            if let neighbour = field.getNeighbour(dir: direction) {
                tempName = self.hexName(id: neighbour.id)
                if self.childNode(withName: tempName) == nil {
                    let newNode = createFieldHex(id: tempName)
                    newNode.position = offsetPosition(hex: hexNode, dir: direction)
                    self.addChild(newNode)
                    
                    createdFields.append(neighbour)
                }
            }
        }
        
        return createdFields
    }
    
    func offsetPosition(hex: HexagonNode, dir: Field.Direction) -> CGPoint {
        let angleDeg: CGFloat = CGFloat(dir.rawValue) * 60
        let angleRad: CGFloat = CGFloat.pi * angleDeg / 180
        
        let result = CGPoint(x: 2 * hex.radius * cos(angleRad) + hex.position.x,
                             y: 2 * hex.radius * sin(angleRad) + hex.position.y )
        
        return result
    }
    
    func createCheckers(fields: [Field]) {
        for f in fields {
            if let player = f.player {
                let hexStr = hexName(id: f.id)
                if let hexNode = self.childNode(withName: hexStr) {
                    let checkerHex = createChecker(player: player)
                    checkerHex.position = hexNode.position
                    self.addChild(checkerHex)
                }
            }
        }
    }
    
    func createChecker(player: Player) -> HexagonNode {
        let node = HexagonNode(size: GameScene.hexSize)
        node.name = checkerName(player: player)
        node.fillColor = checkerColor(zone: player.zoneID)
        
        if self.player == player {
            self.playerNodes.append(node)
        }

        return node
    }
    
    func createFieldHex(id: String) -> HexagonNode{
        let node = HexagonNode(size: GameScene.hexSize)
        node.name = id
        node.fillColor = GameScene.fieldColor
        return node
    }
    
    //MARK: Utility functions
    
    func findCheckerHex(point: CGPoint) -> HexagonNode? {
        let nodes = self.nodes(at: point)
        var hex: HexagonNode? = nil
        for f in nodes {
            if let hexNode = f as? HexagonNode, let name = hexNode.name {
                if name.starts(with: "ch_") {
                    hex = hexNode
                    break
                }
            }
        }
        
        return hex
    }
 
    func findFieldHex(point: CGPoint) -> HexagonNode? {
        let nodes = self.nodes(at: point)
        var hex: HexagonNode? = nil
        for f in nodes {
            if let hexNode = f as? HexagonNode, let name = hexNode.name {
                if name.starts(with: "h_") {
                    hex = hexNode
                    break
                }
            }
        }
        
        return hex
    }
    
    func hexName(id: Int) -> String {
        return "h_\(id)"
    }
    
    func checkerName(player: Player) -> String {
        return "ch_\(player.zoneID)" //TODO
    }
    
    func hexIndex(checkerName: String) -> Int? {
        let scanner = Scanner(string: checkerName)
        var result: Int? = nil
        var value: Int = 0
        
        scanner.scanUpToCharacters(from: CharacterSet.decimalDigits, into: nil)
        if scanner.scanInt(&value) {
            result = value
        }
        
        return result
    }
    
    //MARK: Colors & Graphics
    
    #if os(iOS)
    public static let fieldColor: UIColor = UIColor.hexStringToUIColor(hex: "#EBEBEB")
    
    func checkerColor(zone: Int) -> UIColor{
    let colors = ["#00077A", "#03BD5B", "#BF0A46", "#FF9947", "#A939B9", "#000000"]
    let hexString = colors[zone % 6]
    
    return UIColor.hexStringToUIColor(hex: hexString)
    }
    #elseif os(OSX)
    public static let fieldColor: NSColor = NSColor.hexStringToNSColor(hex: "#EBEBEB")
    
    func checkerColor(zone: Int) -> NSColor {
        let colors = ["#00077A", "#03BD5B", "#BF0A46", "#FF9947", "#A939B9", "#000000"]
        let hexString = colors[zone % 6]
        
        return NSColor.hexStringToNSColor(hex: hexString)
    }
    #endif
    
    
    //MARK: Board manipulation
    
    func updateBoard(checker: HexagonNode, to field: HexagonNode) {
        let action = SKAction.move(to: field.position, duration: GameScene.moveDuration)
        checker.run(action)
    }
    
    override func didMove(to view: SKView) {
        self.setUpScene()
    }
    
    override func update(_ currentTime: TimeInterval) {
        // Called before each frame is rendered
    }
}

extension GameScene: HexagonNodeDelegate {
    func hexNodeClicked(_ node: HexagonNode) {
        guard let nodeName = node.name else {return}
        
        if nodeName.starts(with: "ch_") {
            self.state.setPossibleVisibility(scene: self, hex: node)
        }else if nodeName.starts(with: "h_") {
            self.state.performMove(scene: self, to: node)
        }
        
    }
}

extension GameScene: GameSessionDelegate {
    func turnChanges(session: GameSession, player: Player) {
        if player == self.player {
            for node in playerNodes {
                node.delegate = self
            }
        }else {
            for node in playerNodes {
                node.delegate = nil
            }
        }
    }
    
    func boardChanged(session: GameSession, info: GameInfo) {
        let fromFieldName = hexName(id: info.oldFieldID)
        let destFieldName = hexName(id: info.newFieldID)
        
        guard let fromField = self.childNode(withName: fromFieldName) as? HexagonNode,
              let destField = self.childNode(withName: destFieldName) as? HexagonNode,
              let checker = self.findCheckerHex(point: fromField.position)
        else {return}
        
        updateBoard(checker: checker, to: destField)
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

