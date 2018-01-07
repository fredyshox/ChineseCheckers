//
//  GamesViewController.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit

class GamesTableViewController: UITableViewController {
    
    var games: [SessionInfo] = [] {
        didSet {
            tableView.reloadData()
        }
    }
    let service: DataService = DataService.shared

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Available games"
        
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
        //perform segue
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
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
    

}
