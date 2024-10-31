import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import javafx.application.Platform
import java.awt.*

fun main() = application {

//    Thread.setDefaultUncaughtExceptionHandler { _, e ->
//        Dialog(Frame(), e.message ?: "Error").apply {
//            layout = FlowLayout()
//            val label = Label(e.stackTraceToString())
//            println(e.stackTraceToString())
////            var text = Text(e.stackTraceToString()).var
//            add(label)
//            val button = Button("OK").apply {
//                addActionListener { dispose() }
//            }
//            add(button)
//            setSize(1200,600)
//            isVisible = true
//        }
//    }

    // 初始化JavaFX
    Platform.startup {}

    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        width = 800.dp,
        height = 600.dp
    )
    Window(
        onCloseRequest = {
            Platform.exit()
            exitApplication()
        },
        title = "Web App",
        state = windowState,
        undecorated = true
    ) {
        window.minimumSize = Dimension(350, 600)
        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                // 自定义标题栏
                TopAppBar(
                    backgroundColor = Color(0xFF2196F3),
                    contentColor = Color.White,
                ) {
                    Spacer(Modifier.width(16.dp))
                    Text("Web App", modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        windowState.isMinimized = !windowState.isMinimized
                    }) {
                        Text("—", color = Color.White)
                    }

                    IconButton(onClick = {
                        windowState.placement = if (windowState.placement == WindowPlacement.Maximized) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Maximized
                        }
                    }) {
                        Text("□", color = Color.White)
                    }

                    IconButton(onClick = {
                        Platform.exit()
                        exitApplication()
                    }) {
                        Text("×", color = Color.White)
                    }
                }

                // WebView内容
                Box(modifier = Modifier.weight(1f)) {
                    WebViewComponent(url = "https://www.baidu.com",windowPlacement = windowState.placement)
                }
            }
        }
    }
}