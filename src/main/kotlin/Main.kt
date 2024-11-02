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

fun main() = application {
    val url = "https://www.baidu.com"
    val title = "网易云音乐"
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        width = 1200.dp,
        height = 800.dp
    )
    Window(
        onCloseRequest = {
            exitApplication()
        },
        title = title,
        state = windowState,
        undecorated = true
    ) {
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
                        exitApplication()
                    }) {
                        Text("×", color = Color.White)
                    }
                }

                // W内容
                Box(modifier = Modifier.weight(1f)) {
                    JCefBrowserComponent(url = url, windowPlacement = windowState.placement)
                }

            }
        }
    }
}


