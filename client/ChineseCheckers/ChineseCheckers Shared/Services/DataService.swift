//
//  DataService.swift
//  ChineseCheckers
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import Foundation
import Alamofire

/**
 An Object responsible for making requests to game http server.
 It uses url & port from service.plist file.
 */

class DataService {
    
    fileprivate static let serviceInfo: [String:Any] = Utility.readPlist(name: "service")
    
    private static var _shared: DataService?
    
    static var shared: DataService {
        if _shared == nil {
            _shared = DataService()
        }
        
        return _shared!
    }
    
    private let _baseUrl: String
    private let _port: UInt16
    private let jsonEncoder: JSONEncoder = JSONEncoder()
    private let jsonDecoder: JSONDecoder = JSONDecoder()
    
    var baseUrl: String {
        return _baseUrl + ":\(_port)"
    }
    
    var gamesUrl: String {
        return baseUrl + "/games"
    }
    
    init?() {
        guard let url = DataService.serviceInfo["httpUrl"] as? String,
              let port = DataService.serviceInfo["httpPort"] as? UInt16
            else {
                log.error("Unable to read plist")
                return nil
        }
        
        self._baseUrl = url
        self._port = port
    }
    
    func getCurrentGames(completion: @escaping (Error?, [SessionInfo]) -> Void) {
        Alamofire.request(gamesUrl, method: .get).validate(statusCode: 200..<300).responseJSON { (response) in
            switch response.result {
            case .failure(let err):
                log.error(err.localizedDescription)
                completion(err, [])
            case .success(let value):
                var sessions = [SessionInfo]()
                if let dictArr = value as? [[String:Any]] {
                    for dict in dictArr {
                        if let session = SessionInfo(dict: dict) {
                            sessions.append(session)
                        }
                        completion(nil, sessions)
                    }
                }else {
                    completion(nil, [])
                }
            }
        }
    }
    
    func createGame(info: SessionInfo, completion: @escaping (Error?) -> Void) {
        Alamofire.request(gamesUrl, method: HTTPMethod.post, parameters: info.values, encoding: JSONEncoding.default, headers: nil).validate(statusCode: 200..<300).responseJSON { (respose) in
            switch respose.result {
            case .failure(let err):
                log.error(err.localizedDescription)
                completion(err)
            case .success(_):
                completion(nil)
            }
        }
    }
    
}
