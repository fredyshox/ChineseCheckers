//
//  TurnInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct TurnInfo{
    private let _playerID: Int
    
    init(playerID: Int) {
        self._playerID = playerID
    }
    
    init?(dict: [String:Any]) {
        guard let id = dict["playerID"] as? Int
            else { return nil }
        
        self.init(playerID: id)
    }
    
    var playerID: Int {
        return _playerID
    }
    
}
