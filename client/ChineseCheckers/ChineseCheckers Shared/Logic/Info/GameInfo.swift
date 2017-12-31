//
//  GameInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 29.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct GameInfo: Codable {
    private let _createdAt: Date
    private let _oldFieldID: Int
    private let _newFieldID: Int
    
    enum CodingKeys: String, CodingKey {
        case _createdAt = "createdAt"
        case _oldFieldID = "oldFieldID"
        case _newFieldID = "newFieldID"
    }
    
    init(date: Date, from: Int, to: Int) {
        self._createdAt = date
        self._oldFieldID = from
        self._newFieldID = to
    }
    
    init? (dict: [String: Any]) {
        guard let timestamp = dict["createdAt"] as? Int,
              let from = dict["oldFieldID"] as? Int,
              let to = dict["newFieldID"]as? Int
        else { return nil }
        
        let date = Date(timeIntervalSince1970: Double(timestamp))
        
        self.init(date: date, from: from, to: to)
    }
    
    var createdAr: Date {
        return _createdAt
    }
    
    var oldFieldID: Int {
        return _oldFieldID
    }
    
    var newFieldID: Int {
        return _newFieldID
    }
    
}
