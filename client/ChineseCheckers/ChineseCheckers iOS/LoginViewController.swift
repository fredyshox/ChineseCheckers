//
//  LoginViewController.swift
//  ChineseCheckers iOS
//
//  Created by Kacper Raczy on 02.01.2018.
//  Copyright © 2018 Kacper Raczy. All rights reserved.
//

import UIKit
import MBProgressHUD

class LoginViewController: UIViewController {
    private var service: GameService!
    
    @IBOutlet weak var usernameTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.service = GameService()
        self.service.delegate = self
        self.service.connect();
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func logIn(_ sender: UIButton) {
        usernameTextField.resignFirstResponder()
        if let text = usernameTextField.text {
            if text.count != 0 {
                self.service.login(username: text, gameID: 0)
                let hud = MBProgressHUD.showAdded(to: self.view, animated: true)
            }
        }
    }
    
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        for touch in touches {
            if !usernameTextField.point(inside: touch.location(in: nil), with: event) {
                usernameTextField.resignFirstResponder()
            }
        }
    }
    
    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else{return}
        switch identifier {
        case "showGameVC":
            if let destVC = segue.destination as? GameViewController {
                destVC.service = self.service
                service.delegate = nil
            }
        default:
            break
        }
    }

}

extension LoginViewController: GameServiceDelegate {
    func service(_ service: GameService, didReceiveError error: ErrorInfo) {
        
    }
    
    func service(_ service: GameService, gameDidStarted session: GameSession) {
        MBProgressHUD.hide(for: self.view, animated: true)
        performSegue(withIdentifier: "showGameVC", sender: nil)
    }
}
