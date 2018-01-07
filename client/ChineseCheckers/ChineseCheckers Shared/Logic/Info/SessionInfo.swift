//
//  SessionInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import Foundation

struct SessionInfo {
    private var _id: Int?
    private let _title: String
    private let _playerCount: Int
    private var _currentPlayerCount: Int?
    
    init(title: String, playersNo: Int) {
        self._title = title
        self._playerCount = playersNo
    }
    
    init?(dict: [String: Any]) {
        guard let id = dict["gameID"] as? Int?,
              let title = dict["title"] as? String,
              let playerNo = dict["playerCount"] as? Int,
              let currentPlayerNo = dict["currentPlayerCount"] as? Int?
            else {return nil}
        
        self._id = id
        self._title = title
        self._playerCount = playerNo
        self._currentPlayerCount = currentPlayerNo
    }
    
    var values: [String:Any] {
        var dict = [String:Any]()
        dict["gameID"] = _id
        dict["title"] = _title
        dict["playerCount"] = _playerCount
        dict["currentPlayerCount"] = _currentPlayerCount
        
        return dict
    }
    
    var id: Int? {
        get {
            return _id
        }
        set {
            _id = newValue
        }
    }
    
    var currentPlayerCount: Int? {
        get{
            return _currentPlayerCount
        }
        set {
            _currentPlayerCount = newValue
        }
    }
    
    var title: String {
        return _title
    }
    
    var playerCount: Int {
        return _playerCount
    }
}
