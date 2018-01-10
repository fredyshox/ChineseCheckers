//
//  HexagonNodeDelegate.swift
//  ChineseCheckers macOS
//
//  Created by Kacper Raczy on 28.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation

/**
 To be notified about interaction events of HexagonNode object,
 conform this protocol and implement provided methods.
 */

protocol HexagonNodeDelegate {
    func hexNodeClicked(_ node: HexagonNode)
}
