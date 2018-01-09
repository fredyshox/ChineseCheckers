//
//  GamesViewController.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit
import MBProgressHUD

class GamesTableViewController: UITableViewController {
    
    var games: [SessionInfo] = [] {
        didSet {
            tableView.reloadData()
        }
    }
    let service: DataService = DataService.shared
    
    fileprivate var currentGameService: GameService?

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Available games"
        
        self.tableView.rowHeight = UITableViewAutomaticDimension
        
        refresh(refreshControl!)
    }
    
    
    @IBAction func refresh(_ sender: UIRefreshControl) {
        sender.beginRefreshing()
        service.getCurrentGames { (error, sessions) in
            if error != nil {
                //error happend
            }
            
            sender.endRefreshing()
            self.games = sessions
        }
    }
    
    @IBAction func addGame(_ sender: UIBarButtonItem) {
        performSegue(withIdentifier: "addGameSegue", sender: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func joinGame(username: String, session: SessionInfo) {
        let service = GameService()
        service.delegate = self
        service.connect()
        
        service.login(username: username, gameID: session.id!)
        currentGameService = service
        
        //adding to navigation controller view to lock user interaction
        let hud = MBProgressHUD.showAdded(to: self.navigationController!.view, animated: true)
        hud.label.text = "Waiting for players"
        hud.button.setTitle("Cancel", for: .normal)
        hud.button.addTarget(self, action: #selector(cancelJoin(sender:)), for: .touchUpInside)
    }
    
    @objc
    func cancelJoin(sender: Any) {
        MBProgressHUD.hide(for: self.navigationController!.view, animated: true)
        if let service = currentGameService {
            service.delegate = nil
            service.disconnect()
            currentGameService = nil
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return 64.0
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return games.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "gameCell") as? GameTableViewCell {
            cell.sessionInfo = games[indexPath.row]
            
            return cell
        }
        
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let session = games[indexPath.row]
        let alert = UIAlertController(title: session.title, message: "Type in your username to join.", preferredStyle: .alert)
        alert.addTextField { (textField) in
            textField.placeholder = "username"
        }
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        alert.addAction(UIAlertAction(title: "Join", style: .default, handler: { (action) in
            if let username = alert.textFields?.first?.text {
                if username.count != 0 {
                    self.joinGame(username: username, session: session)
                }
                //show prompt
            }
        }))
        
        present(alert, animated: true, completion: nil)
        
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier,
              let gameService = sender as? GameService
        else { return }
        switch identifier {
        case "showGameVC":
            if let destVC = segue.destination as? GameViewController {
                destVC.service = gameService
                gameService.delegate = nil
                currentGameService = nil
            }
        default:
            break
        }
    }
}

extension GamesTableViewController: GameServiceDelegate {
    func service(_ service: GameService, didReceiveError error: ErrorInfo) {
        MBProgressHUD.hide(for: self.navigationController!.view, animated: true)
        
    }
    
    func service(_ service: GameService, gameDidStarted session: GameSession) {
        MBProgressHUD.hide(for: self.navigationController!.view, animated: true)
        performSegue(withIdentifier: "showGameVC", sender: service)
    }
}
