//
//  Utility.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import Foundation

class Utility {
    static func readPlist(name: String) -> [String:Any] {
        let plistPath = Bundle.main.path(forResource: name, ofType: "plist")!
        let plistXML = FileManager.default.contents(atPath: plistPath)!
        var plistFormat = PropertyListSerialization.PropertyListFormat.xml
        var plistData = [String:Any]()
        
        do {
            
            plistData = try PropertyListSerialization.propertyList(from: plistXML, options: .mutableContainersAndLeaves, format: &plistFormat) as! [String:Any]
        }catch {
            log.error(error.localizedDescription)
        }
        
        return plistData
    }
}
