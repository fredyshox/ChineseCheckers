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
    
    //properties
    fileprivate static let serviceData: [String:Any] = readPlist()
    fileprivate var _session: GameSession! {
        didSet {
            if let turnInfo = _currentTurn {
                processTurnInfo(turnInfo: turnInfo)
            }
        }
    }
    fileprivate var _socket: GCDAsyncSocket!
    
    //utility
    fileprivate let encoder: JSONEncoder = JSONEncoder()
    fileprivate let decoder: JSONDecoder = JSONDecoder()
    
    fileprivate let separator: Data = "\r\n".data(using: .utf8)!
    
    //
    fileprivate var _delegate: GameServiceDelegate?
    fileprivate var _playerID: Int?
    fileprivate var _currentTurn: TurnInfo? {
        didSet{
            if let turnInfo = _currentTurn {
                processTurnInfo(turnInfo: turnInfo)
            }
        }
    }
    
    enum SocketTags: Int {
        case login
        case waiting
        case inProgress
    }
    
    override init() {
        super.init()
        
        let queue = DispatchQueue.main
        let socket = GCDAsyncSocket(delegate: self, delegateQueue: queue)
    
        self._socket = socket
        
        setUpCoders()
    }
    
    func connect() {
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
        encoder.dataEncodingStrategy = .deferredToData
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
    
    var playerID: Int? {
        return _playerID
    }
    
    //MARK: Writing
    
    func login(username: String, gameID: Int) {
        let loginInfo = LoginInfo(username: username, gameID: gameID)
        
        do {
            let jsonData = try encoder.encode(loginInfo)
            let str = String(data: jsonData, encoding: .utf8)
            log.info(str)
            self._socket.write(jsonData, withTimeout: -1, tag: SocketTags.login.rawValue)
            self._socket.readData(to: separator, withTimeout: -1, tag: SocketTags.login.rawValue)
        }catch {
            log.error(error.localizedDescription)
        }
    }
    
    func sendMove(gameInfo: GameInfo) {
        do {
            let jsonData = try encoder.encode(gameInfo)
            self._socket.write(jsonData, withTimeout: -1, tag: SocketTags.inProgress.rawValue)
        }catch {
            log.error(error.localizedDescription)
        }
    }
    
    private func processTurnInfo(turnInfo: TurnInfo) {
        if let session = _session,
           let player = session.findPlayer(id: turnInfo.playerID) {
            self._session.currentPlayer = player
        }
    }
 
    
}

extension GameService: GCDAsyncSocketDelegate {
    func socket(_ sock: GCDAsyncSocket, didConnectToHost host: String, port: UInt16) {
        log.info("Connected server: host - " + host + ", port: " + port.description)
    }
    
    func socket(_ sock: GCDAsyncSocket, didWriteDataWithTag tag: Int) {
        
    }
    
    func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int) {
        if let sockTag = SocketTags(rawValue: tag) {
            switch sockTag {
            case .login:
                self._socket.readData(to: separator, withTimeout: -1, tag: SocketTags.waiting.rawValue)
            case .waiting:
                self._socket.readData(to: separator, withTimeout: -1, tag: SocketTags.inProgress.rawValue)
            case .inProgress:
                self._socket.readData(to: separator, withTimeout: -1, tag: SocketTags.inProgress.rawValue)
            }
            
            guard let jsonData = try? JSONSerialization.jsonObject(with: data, options: []),
                let json = jsonData as? [String:Any],
                let type = json["type"] as? String
                else { return }

            log.info("Reveiced json.")
            
            switch type {
            case "init":
                var players = [Player]()
                if let session = json["session"] as? [String:Any],
                    let playersJson = session["players"] as? [[String: Any]],
                    let boardInfo = BoardInfo(dict: session) {
                    for dict in playersJson {
                        if let player = Player(dict: dict){
                            players.append(player)
                        }
                    }
                    
                    self._session = GameSession(binfo: boardInfo, players: players)
                    log.info("Logged in")
                    self._delegate?.service(self, gameDidStarted: self._session)
                }
            case "info":
                if let infoJson = json["info"] as? [String:Any], let gameInfo = GameInfo(dict: infoJson) {
                    log.info("Got move data: ")
                    self._session.performMove(info: gameInfo)
                }
            case "turn":
                if let turnInfo = TurnInfo(dict: json) {
                    self._currentTurn = turnInfo
                    log.info("Turn info: player with id: \(turnInfo.playerID)")
                }
            case "result":
                if let resultInfo = ResultInfo(dict: json) {
                    self._delegate?.service(self, didReceiveResult: resultInfo)
                }
            case "error":
                if let errorInfo = ErrorInfo(dict: json) {
                    log.info("Got error: \(errorInfo.cause)")
                    self._delegate?.service(self, didReceiveError: errorInfo)
                }
            case "player":
                if let id = json["id"] as? Int {
                    log.info("Got player id: \(id)")
                    self._playerID = id
                }
            default:
                log.error("Unknown response from socket")
                break
            }
        }
        
    }
}
