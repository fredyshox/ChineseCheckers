//
//  AddGameViewController.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 07.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit
import Eureka
import MBProgressHUD

class AddGameViewController: FormViewController {
    
    @IBOutlet weak var addButton: UIBarButtonItem!
    
    private let service = DataService.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Add game"
        addButton.isEnabled = false
        
        form +++
            Section("Information")
                <<< TextRow("title") { row in
                    row.title = "Title"
                    row.placeholder = "Enter game title"
                    row.onChange({ (_) in
                        self.toggleAddButton()
                    })
                }
        +++ Section("Game mode")
            <<< PickerInlineRow<Int>("count") { row in
                row.title = "Player count"
                row.options = [2,3,4,6]
                row.onChange({ (_) in
                    self.toggleAddButton()
                })
            }
        
    }
    
    func toggleAddButton() {
        guard let titleRow = form.rowBy(tag: "title") as? TextRow,
            let countRow = form.rowBy(tag: "count") as? PickerInlineRow<Int>,
            let _ = titleRow.value,
            let _ = countRow.value
            else {
                addButton.isEnabled = false
                return
        }
        
        addButton.isEnabled = true
    }
    
    @IBAction func addGame(_ sender: Any) {
        guard let titleRow = form.rowBy(tag: "title") as? TextRow,
              let countRow = form.rowBy(tag: "count") as? PickerInlineRow<Int>,
              let title = titleRow.value,
              let count = countRow.value
            else { return }
        
        let hud = MBProgressHUD.showAdded(to: self.navigationController!.view, animated: true)
        hud.label.text = "Creating..."
        
        let sessionInfo = SessionInfo(title: title , playersNo: count)
        service.createGame(info: sessionInfo) { (err) in
            if err != nil {
                log.error(err!.localizedDescription)
            }
            
            MBProgressHUD.hide(for: self.navigationController!.view, animated: true)
            self.dismiss(animated: true, completion: nil)
        }
    }
    
}
