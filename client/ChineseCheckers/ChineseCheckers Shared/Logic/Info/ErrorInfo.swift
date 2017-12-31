//
//  ErrorInfo.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

struct ErrorInfo {
    
    private let _cause: String
    
    init(cause: String) {
        self._cause = cause
    }
    
    init?(dict: [String:Any]) {
        guard let cause = dict["cause"] as? String
            else { return nil }
        
        self.init(cause: cause)
    }
    
    var cause: String {
        return _cause
    }
    
}
