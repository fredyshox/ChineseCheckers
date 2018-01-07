//
//  GameViewController.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 25.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import UIKit
import SpriteKit
import GameplayKit
import SlidingSideView

class GameViewController: UIViewController {
    var service: GameService!
    
    private var roundLabel: RoundLabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let scene = GameScene.newScene(service: service)

        // Present the scene
        let skView = self.view as! SKView
        skView.presentScene(scene)
        
        skView.ignoresSiblingOrder = true
        skView.showsFPS = true
        skView.showsNodeCount = true
        
        // Navigation bar
        setUpNavigationBar()
        
        // Sliding Side View
        let ssv = SlidingSideView(.top, withNormalHeight: 100.0)
        ssv.topLayoutGuide = self.topLayoutGuide
        
        self.view.addSubview(ssv)
    }
    
    func setUpNavigationBar() {
        let roundLabel = RoundLabel()
        
        self.navigationItem.titleView = roundLabel
        self.roundLabel = roundLabel
    }

    override var shouldAutorotate: Bool {
        return true
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        if UIDevice.current.userInterfaceIdiom == .phone {
            return .allButUpsideDown
        } else {
            return .all
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Release any cached data, images, etc that aren't in use.
    }

    override var prefersStatusBarHidden: Bool {
        return true
    }
}

extension GameViewController: GameServiceDelegate {
    
}
