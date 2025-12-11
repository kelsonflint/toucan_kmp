import SwiftUI
import ToucanKMP

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        let lockedOrientation = OrientationHelper.shared.lockedOrientation
        if let locked = lockedOrientation {
            return UIInterfaceOrientationMask(rawValue: UInt(locked.intValue))
        }
        return .all
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}