# Chinese Checkers

Online multiplayer game with Java server and iOS/macOS client.

Created as university project for 'Programming technology' class.

## Game Logic

Independed module which handles game-state manipulation and player interaction validation.
Designed to be easily expandable with additional game-modes or board designs.
Uses multiple design patterns like: Strategy, Builder, Observer etc.

### Structure 

[UML class diagram][class-uml]

## Server 

Server was written in Java using Netty 4.x library.

It uses TCP Socket protocol for in game communication and HTTP for creating game sessions and listing available ones. Communication with clients is using JSON serialized message objects. 

### Libraries

Programming Language:
* Java SE 8

Libraries used: 
* Netty 4.x.x (https://github.com/netty/netty)
* gson 2.8.x (https://github.com/google/gson)
* log2j 2.x.x (https://github.com/apache/logging-log4j2)

### Structure

* [Game session login activity UML][game-uml]

* [Actual game management UML][login-uml]

## Client

Client is written for iOS platform (macOS version not yet optimized) using SpriteKit framework.

### Libraries 

Programming Langugage: 
* Swift 4.x

Libraries:
* SpriteKit
* CocoaAsyncSocket (https://github.com/robbiehanson/CocoaAsyncSocket)
* Alamofire (https://github.com/Alamofire/Alamofire)
* UI libs -> see [podfile](https://github.com/fredyshox/TP-Project/blob/master/client/ChineseCheckers/Podfile) 

[login-uml]: ./img/login-activity-uml.png
[game-uml]: ./img/game-activity-uml.png
[class-uml]: ./img/class-uml.png