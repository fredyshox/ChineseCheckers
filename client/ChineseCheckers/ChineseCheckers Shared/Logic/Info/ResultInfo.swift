//
//  ResultInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct ResultInfo {
    
    enum Result: Int {
        case won
        case lost
    }
    
    private let _playerID: Int
    private let _result: Result
    
    init(playerID: Int, result: Result) {
        self._playerID = playerID
        self._result = result
    }
    
    init?(dict: [String:Any]) {
        guard let playerID = dict["playerID"] as? Int,
              let resultVal = dict["result"] as? Int,
              let result = Result(rawValue: resultVal)
        else { return nil }
        
        self.init(playerID: playerID, result: result)
    }
    
    var playerID: Int {
        return _playerID
    }
    
    var result: Result {
        return _result
    }
    
}
