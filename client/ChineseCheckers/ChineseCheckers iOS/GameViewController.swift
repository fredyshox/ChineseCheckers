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

/**
 A subclass of UIViewController responsible for presenting GameScene
 and manipulating UI elements.
 */

class GameViewController: UIViewController {
    var service: GameService!
    
    @IBOutlet weak var navigationBar: UINavigationBar!
    
    private var roundLabel: RoundLabel!
    private var ssView: SlidingSideView!
    private var infoView: InfoView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        service.delegate = self
        let scene = GameScene.newScene(service: service)

        // Present the scene
        let skView = self.view as! SKView
        skView.presentScene(scene)
        
        skView.ignoresSiblingOrder = true
        skView.showsFPS = true
        skView.showsNodeCount = true
        
        // Sliding Side View
        ssView = SlidingSideView(.bottom, withNormalHeight: 70.0)
        ssView.bottomLayoutGuide = self.bottomLayoutGuide
        
        // Info View
        infoView = InfoView(frame: CGRect.zero)
        ssView.addSubview(infoView)
        configure(superV: ssView, subV: infoView)
        
        
        self.view.addSubview(ssView)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        setUpNavigationBar()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        roundLabel.text = turnText(self.service.session.currentPlayer)
    }
    
    func setUpNavigationBar() {
        navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationBar.shadowImage = UIImage()
        navigationBar.isTranslucent = true
        navigationBar.backgroundColor = UIColor.clear
        
        let optionsButton = UIBarButtonItem(image: UIImage(named:"options"),
                                            style: .plain,
                                            target: self,
                                            action: #selector(disconnect(sender:)))
        self.navigationItem.leftBarButtonItem = optionsButton
        
        let roundLabel = RoundLabel()
        roundLabel.backgroundColor = UIColor.hexStringToUIColor(hex: "#ebebeb")
        roundLabel.textColor = UIColor.hexStringToUIColor(hex: "#636363")
        roundLabel.cornerRadius = 8.0
        roundLabel.font = UIFont.systemFont(ofSize: 18.0, weight: .medium)
        roundLabel.textAlignment = .center
        roundLabel.clipsToBounds = true
        self.navigationItem.titleView = roundLabel
        self.roundLabel = roundLabel
        
        self.navigationBar.items = [self.navigationItem]
    }
    
    private func turnText(_ player: Player) -> String {
        let text: String
        if player.id == service.playerID {
            text = "Your turn"
        }else {
            text = "\(player.username) turn"
        }
        
        return text
    }
    
    lazy var disconnectAlert: UIAlertController = {
        let disconnectAlert = UIAlertController(title: "Disconnect",
                                                message: "Are you sure?",
                                                preferredStyle: .alert)
        disconnectAlert.addAction(UIAlertAction(title: "No",
                                                style: .cancel,
                                                handler: nil))
        disconnectAlert.addAction(UIAlertAction(title: "Yes",
                                                style: .default) {(action) in
            self.service.disconnect()
            self.dismiss(animated: true, completion: nil)
        })
        
        return disconnectAlert
    }()
    
    @objc
    func disconnect(sender: Any) {
        let disconnectAlert = self.disconnectAlert
        present(disconnectAlert, animated: true, completion: nil)
    }
    
    
    func configure(superV: UIView,subV: UIView) {
        subV.translatesAutoresizingMaskIntoConstraints = false
        subV.topAnchor.constraint(equalTo: superV.topAnchor).isActive = true
        subV.bottomAnchor.constraint(equalTo: superV.bottomAnchor).isActive = true
        subV.rightAnchor.constraint(equalTo: superV.rightAnchor).isActive = true
        subV.leftAnchor.constraint(equalTo: superV.leftAnchor).isActive = true
    }
    
    func closeSlidingView() {
        Timer.scheduledTimer(withTimeInterval: 3.0, repeats: false) { (timer) in
            self.ssView.currentState = .collapsed
        }
    }
    
    // MARK: UIViewController methods
    
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
    func service(_ service: GameService, didReceiveTurnInfo turnInfo: TurnInfo) {
        guard let player = service.session.findPlayer(id: turnInfo.playerID)
            else {
                log.error("Wrong")
                return
        }
        log.info("yes")
        
        self.infoView.state = .custom
        self.infoView.color = GameScene.checkerColor(zone: player.zoneID)
        
        let text: String = turnText(player)
        self.infoView.textLabel.text = text
        self.roundLabel.text = text
        
        self.ssView.currentState = .normal
        
        closeSlidingView()
    }
    
    func service(_ service: GameService, didReceiveError error: ErrorInfo) {
        self.infoView.state = .error
        self.infoView.textLabel.text = "Error: \(error.cause)"
        self.ssView.currentState = .normal
        
        closeSlidingView()
    }
    
    func service(_ service: GameService, didReceiveResult result: ResultInfo) {
        switch result.result {
        case .lost:
            self.infoView.state = .info
            self.infoView.textLabel.text = "Game has ended. You can now leave."
            self.roundLabel.text = "Game has ended"
        case .won:
            self.infoView.state = .success
            self.infoView.textLabel.text = "You won"
        }
        
        self.ssView.currentState = .normal
        
        closeSlidingView()
    }
    
    
}
