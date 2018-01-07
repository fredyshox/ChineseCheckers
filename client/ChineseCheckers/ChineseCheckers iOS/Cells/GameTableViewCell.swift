//
//  GameTableViewCell.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit

class GameTableViewCell: UITableViewCell {
    
    var sessionInfo: SessionInfo? {
        didSet{
            updateUI()
        }
    }

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var rightLabel: UILabel!
    
    func updateUI() {
        if let session = sessionInfo {
            titleLabel.text = session.title
            
            if let id = session.id, let currentCount = session.currentPlayerCount {
                descriptionLabel.text = "id: \(id)"
                rightLabel.text = "\(currentCount) / \(session.playerCount)"
            }
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

}
