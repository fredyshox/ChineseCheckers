//
//  AppDelegate.swift
//  ChineseCheckers macOS
//
//  Created by Kacper Raczy on 25.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Cocoa
import XCGLogger

let log: XCGLogger = {
    let log = XCGLogger(identifier: "macOSLogger", includeDefaultDestinations: false)
    
    let systemDestination = AppleSystemLogDestination(owner: log, identifier: "macOSLogger.systemDestination")
    systemDestination.outputLevel = .debug
    systemDestination.showDate = true
    systemDestination.showLogIdentifier = false
    systemDestination.showLineNumber = true
    systemDestination.showFunctionName = true
    systemDestination.showFileName = true
    systemDestination.showLevel = true
    
    log.add(destination: systemDestination)
    
    log.logAppDetails()
    
    return log
}()


@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {


    func applicationDidFinishLaunching(_ aNotification: Notification) {
        //init logger
        let _ = log
    }

    func applicationWillTerminate(_ aNotification: Notification) {
        // Insert code here to tear down your application
    }

    func applicationShouldTerminateAfterLastWindowClosed(_ sender: NSApplication) -> Bool {
        return true
    }


}

