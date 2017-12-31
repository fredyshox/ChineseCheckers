//
//  LoginInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct LoginInfo: Codable {
    private let _username: String
    private let _gameID: Int
    
    enum CodingKeys: String, CodingKey {
        case _username = "username"
        case _gameID = "gameID"
    }
    
    init(username: String, gameID: Int) {
        self._gameID = gameID
        self._username = username
    }
    
    var username: String {
        return _username
    }
    
    var gameID: Int {
        return _gameID
    }
}
