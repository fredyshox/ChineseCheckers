//
//  GameViewController.swift
//  ChineseCheckers macOS
//
//  Created by Kacper Raczy on 25.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Cocoa
import SpriteKit
import GameplayKit

class GameViewController: NSViewController {
    
    private var service: GameService!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let service = GameService()
        service.connect()
        
        let scene = MenuScene.newScene(service: service)
        
        // Present the scene
        let skView = self.view as! SKView
        skView.presentScene(scene)
        
        skView.ignoresSiblingOrder = true
        
        skView.showsFPS = true
        skView.showsNodeCount = true
    }

}

