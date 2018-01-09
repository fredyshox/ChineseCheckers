//
//  InfoView.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit

class InfoView: UIView {

    // MARK: UI elements
    @IBOutlet var contentView: UIView!
    @IBOutlet weak var textLabel: UILabel!
    @IBOutlet weak var backgroundView: RoundView!
    
    // MARK: Types
    enum InfoViewState: String {
        case info = "#0058A1"
        case warning = "#FF9947"
        case error =  "#970023"
        case success = "#03BD5B"
        case custom = "#000000"
    }
    
    // MARK: Properties
    private var _state: InfoViewState = InfoViewState.info {
        didSet {
            if _state != .custom {
                let color = UIColor.hexStringToUIColor(hex: _state.rawValue)
                _color = color
            }
        }
    }
    
    private var _color: UIColor = UIColor.black {
        didSet{
            backgroundView.borderColor = _color
            backgroundView.backgroundColor = UIColor.rgba2rgb(background: UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0),
                                                              color: _color.withAlphaComponent(0.8))
        }
    }
    
    
    // MARK: Accessors
    var state: InfoViewState {
        get{
            return _state
        }
        set {
            _state = newValue
        }
    }
    
    var color: UIColor {
        get{
            return _color
        }
        set{
            _color = newValue
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setUpXib()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setUpXib()
    }
    
    func setUpXib() {
        Bundle.main.loadNibNamed("InfoView", owner: self, options: nil)
        self.addSubview(contentView)
        configureView()
    }
    
    func configureView() {
        contentView.translatesAutoresizingMaskIntoConstraints = false
        let topCon = NSLayoutConstraint(item: contentView, attribute: .top, relatedBy: .equal, toItem: self, attribute: .top, multiplier: 1.0, constant: 0.0)
        let bottomCon = NSLayoutConstraint(item: contentView, attribute: .bottom, relatedBy: .equal, toItem: self, attribute: .bottom, multiplier: 1.0, constant: 0.0)
        let leadingCon = NSLayoutConstraint(item: contentView, attribute: .leading, relatedBy: .equal, toItem: self, attribute: .leading, multiplier: 1.0, constant: 0.0)
        let trailingCon = NSLayoutConstraint(item: contentView, attribute: .trailing, relatedBy: .equal, toItem: self, attribute: .trailing, multiplier: 1.0, constant: 0.0)
        NSLayoutConstraint.activate([topCon,bottomCon,leadingCon,trailingCon])
    }
    
}
