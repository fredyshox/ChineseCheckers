//
//  Player.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 28.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

/**
 Data class representing player.
 */

class Player: Equatable {
    private let _id: Int
    private let _username: String
    private let _zoneID: Int
    
    init(id: Int, username: String, zoneID: Int) {
        self._id =  id
        self._username = username
        self._zoneID = zoneID
    }
    
    convenience init?(dict: [String:Any]) {
        guard let id = dict["id"] as? Int,
              let username = dict["username"] as? String,
              let zoneID = dict["zoneID"] as? Int
        else{ return nil }
        
        self.init(id: id, username: username, zoneID: zoneID)
    }
    
    var id: Int {
        return _id
    }
    
    var username: String {
        return _username
    }
    
    var zoneID: Int {
        return _zoneID
    }
    
    public static func == (lhs: Player, rhs: Player) -> Bool {
        return lhs.id == rhs.id && lhs.username == rhs.username && lhs.zoneID == rhs.zoneID
    }
}
