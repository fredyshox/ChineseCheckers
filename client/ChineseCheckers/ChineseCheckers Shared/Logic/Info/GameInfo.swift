//
//  GameInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 29.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct GameInfo: Codable {
    fileprivate static let dateFormatter = DateFormatter.iso8601
    
    private let _createdAt: Date
    private let _oldFieldID: Int
    private let _newFieldID: Int
    
    enum CodingKeys: String, CodingKey {
        case _createdAt = "createdAt"
        case _oldFieldID = "oldFieldID"
        case _newFieldID = "newFieldID"
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(_oldFieldID, forKey: ._oldFieldID)
        try container.encode(_newFieldID, forKey: ._newFieldID)
        
        let dateStr = GameInfo.dateFormatter.string(from: self._createdAt)
        try container.encode(dateStr, forKey: ._createdAt)
    }
    
    init(date: Date, from: Int, to: Int) {
        self._createdAt = date
        self._oldFieldID = from
        self._newFieldID = to
    }
    
    
    init? (dict: [String: Any]) {
        guard let dateStr = dict["createdAt"] as? String,
              let date = GameInfo.dateFormatter.date(from: dateStr),
              let from = dict["oldFieldID"] as? Int,
              let to = dict["newFieldID"]as? Int
        else {
            log.error("Unable to parse gameinfo");
            return nil
        }
        
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
