//
//  GameSessionDelegate.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 29.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

/**
 The delegate of GameSession have to implement provided methods to be
 notified about game's events.
 */

protocol GameSessionDelegate {
    func boardChanged(session: GameSession, info: GameInfo)
    func turnChanges(session: GameSession, player: Player)
}
