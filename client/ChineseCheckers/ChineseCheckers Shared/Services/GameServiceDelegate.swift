//
//  GameServiceDelegate.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation


protocol GameServiceDelegate {
    func service(_ service: GameService, didReceiveError error: ErrorInfo)
    func service(_ service: GameService, didReceiveResult result: ResultInfo)
    func service(_ service: GameService, gameDidStarted session: GameSession)
}
