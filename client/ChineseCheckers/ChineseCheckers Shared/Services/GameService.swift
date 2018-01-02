//
//  GameService.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 30.12.2017.
//  Copyright © 2017 Kacper Raczy. All rights reserved.
//

import Foundation
import CocoaAsyncSocket

class GameService: NSObject{
    
    //props
    fileprivate static let serviceData: [String:Any] = readPlist()
    fileprivate var _session: GameSession!
    fileprivate var _socket: GCDAsyncSocket!
    
    //utility
    fileprivate let encoder: JSONEncoder = JSONEncoder()
    fileprivate let decoder: JSONDecoder = JSONDecoder()
    
    //
    fileprivate var _delegate: GameServiceDelegate?
    
    enum SockTags: Int {
        case login
        case gameInfo
    }
    
    override init() {
        super.init()
        
        let queue = DispatchQueue.main
        let socket = GCDAsyncSocket(delegate: self, delegateQueue: queue)
    
        self._socket = socket
        
        setUpCoders()
    }
    
    private func connect() {
        guard let port = GameService.serviceData["port"] as? UInt16, let url = GameService.serviceData["url"] as? String
        else {
            log.error("Property list doesn't contain required values")
            return
        }
        
        do {
            try self._socket.connect(toHost: url, onPort: port, withTimeout: 5.0)
        } catch {
            log.error(error.localizedDescription)
        }
    }
    
    private func setUpCoders() {
        encoder.dateEncodingStrategy = .millisecondsSince1970
    }
    
    fileprivate static func readPlist() -> [String:Any] {
        let plistPath = Bundle.main.path(forResource: "service", ofType: "plist")!
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
    
    //MARK: Getters/Setters
    
    var session: GameSession {
        return _session
    }
    
    var board: Board {
        return _session.board
    }
    
    var players: [Player] {
        return _session.players
    }
    
    var isInProgress: Bool {
        return (_session != nil)
    }
    
    var delegate: GameServiceDelegate? {
        get{
            return _delegate
        }
        set{
            _delegate = newValue
        }
    }
    
    //MARK: Writing
    
    func login(username: String, gameID: Int) {
        let loginInfo = LoginInfo(username: username, gameID: gameID)
        
        do {
            let jsonData = try encoder.encode(loginInfo)
            self._socket.write(jsonData, withTimeout: -1, tag: SockTags.login.rawValue)
        }catch {
            log.error(error.localizedDescription)
        }
    }
    
    func sendMove(gameInfo: GameInfo) {
        do {
            let jsonData = try encoder.encode(gameInfo)
            self._socket.write(jsonData, withTimeout: -1, tag: SockTags.gameInfo.rawValue)
        }catch {
            log.error(error.localizedDescription)
        }
    }
    
}

extension GameService: GCDAsyncSocketDelegate {
    func socket(_ sock: GCDAsyncSocket, didConnectToHost host: String, port: UInt16) {
        log.info("Connected server: host - " + host + ", port: " + port.description)
    }
    
    func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int) {
        guard let jsonData = try? JSONSerialization.jsonObject(with: data, options: []),
            let json = jsonData as? [String:Any],
            let type = json["type"] as? String
            else {return}
        
        switch type {
        case "init":
            var players = [Player]()
            if let playersJson = json["players"] as? [[String: Any]],
               let boardInfo = BoardInfo(dict: json) {
                for dict in playersJson {
                    if let player = Player(dict: dict){
                        players.append(player)
                    }
                }
                
                self._session = GameSession(binfo: boardInfo, players: players)
                self._delegate?.service(self, gameDidStarted: self._session)
            }
        case "info":
            if let infoJson = json["info"] as? [String:Any], let gameInfo = GameInfo(dict: infoJson) {
                self._session.performMove(info: gameInfo)
            }
        case "turn":
            if let turnInfo = TurnInfo(dict: json), let player = self._session.findPlayer(id: turnInfo.playerID) {
                self._session.currentPlayer = player
            }
        case "result":
            if let resultInfo = ResultInfo(dict: json) {
                self._delegate?.service(self, didReceiveResult: resultInfo)
            }
        case "error":
            if let errorInfo = ErrorInfo(dict: json) {
                self._delegate?.service(self, didReceiveError: errorInfo)
            }
        default:
            log.error("Unknown response from socket")
            break
        }
    }
}
