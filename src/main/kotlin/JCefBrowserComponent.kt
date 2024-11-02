import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowPlacement
import org.cef.CefApp

@Composable
fun JCefBrowserComponent(url: String, windowPlacement: WindowPlacement) {
    // 初始化CEF
    val cefApp = remember { CefApp.getInstance() }
    // Build a CefApp instance
    val client = remember { cefApp.createClient() }
    // 创建浏览器
    val browser = remember {
        client.createBrowser(url, false, false)
    }


    // 监听窗口状态变化
    LaunchedEffect(windowPlacement) {
        if (windowPlacement == WindowPlacement.Maximized) {
            browser.reload()
        }
    }
    // 创建Swing面板来承载CEF浏览器
//    val panel = remember {
//        JPanel(BorderLayout()).apply {
//            add(browser.uiComponent, BorderLayout.CENTER)
//        }
//    }
//    // 将Swing面板集成到Compose中
//    androidx.compose.ui.awt.SwingPanel(
//        modifier = Modifier.fillMaxSize(),
//        factory = { panel }
//    )
}