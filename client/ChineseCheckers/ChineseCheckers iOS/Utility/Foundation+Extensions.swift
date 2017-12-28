//
//  Foundation+Extensions.swift
//  UtilityKit
//
//  Created by Kacper Raczy on 07.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

extension DateFormatter {
    static let iso8601: DateFormatter = {
        var df = DateFormatter()
        df.calendar = Calendar(identifier: .iso8601)
        df.locale = Locale(identifier: "en_US_POSIX")
        df.timeZone = TimeZone(secondsFromGMT: 0)
        df.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
        
        return df
    }()
}

extension Decimal {
    static func fromString(string: String) -> Decimal? {
        let locale = Locale(identifier: "en_US_POSIX")
        let dec = Decimal(string: string, locale: locale)
        
        return dec
    }
}

extension Double {
    var toDate: Date {
        return Date(timeIntervalSince1970: self)
    }
}
