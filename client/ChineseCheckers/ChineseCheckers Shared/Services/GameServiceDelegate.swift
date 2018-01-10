//
//  GameServiceDelegate.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

/**
 The delegate of GameService can be notified about server responding by implementing
 these methods.
 */

protocol GameServiceDelegate {
    func service(_ service: GameService, didReceiveError error: ErrorInfo)
    func service(_ service: GameService, didReceiveResult result: ResultInfo)
    func service(_ service: GameService, gameDidStarted session: GameSession)
    func service(_ service: GameService, didReceiveTurnInfo turnInfo: TurnInfo)
}

extension GameServiceDelegate {
    func service(_ service: GameService, didReceiveError error: ErrorInfo) {}
    func service(_ service: GameService, didReceiveResult result: ResultInfo) {}
    func service(_ service: GameService, gameDidStarted session: GameSession) {}
    func service(_ service: GameService, didReceiveTurnInfo turnInfo: TurnInfo) {}
}
