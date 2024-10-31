import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.window.WindowPlacement
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.concurrent.Worker
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Dimension

@Composable
fun WebViewComponent(url: String, windowPlacement: WindowPlacement) {

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentUrl by remember { mutableStateOf(url) }
    // 记住WebView和Panel的引用
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    val jfxPanelRef = remember { mutableStateOf<JFXPanel?>(null) }

    // 监听窗口状态变化
    LaunchedEffect(windowPlacement) {
        if (windowPlacement == WindowPlacement.Maximized) {
            webViewRef.value?.engine?.let { engine ->
                Platform.runLater {
                    // 保存当前URL
                    currentUrl = engine.location
                    // 重新加载
                    engine.reload()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SwingPanel(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    // 当Compose容器大小改变时，更新JavaFX组件大小
                    Platform.runLater {
                        webViewRef.value?.let { webView ->
                            webView.prefWidth = size.width.toDouble()
                            webView.prefHeight = size.height.toDouble()
                        }
                        jfxPanelRef.value?.let { jfxPanel ->
                            jfxPanel.preferredSize = Dimension(size.width, size.height)
                            jfxPanel.size = Dimension(size.width, size.height)
                            jfxPanel.revalidate()
                            jfxPanel.repaint()
                        }
                    }
                },
            factory = {
                JPanel(BorderLayout()).apply {
                    val jfxPanel = JFXPanel()
                    jfxPanelRef.value = jfxPanel
                    add(jfxPanel, BorderLayout.CENTER)

                    Platform.runLater {
                        try {
                            val webView = WebView()
                            webViewRef.value = webView
                            val webEngine = webView.engine

                            // 配置WebView
                            webView.apply {
                                isContextMenuEnabled = true // 启用右键菜单
                                // 允许WebView自动调整大小
//                                isResizable = true
                                // 移除WebView的默认边距
//                                style = "-fx-background-color: transparent;"
                            }

                            // 配置WebEngine
                            webEngine.apply {
                                isJavaScriptEnabled = true

                                // 监听加载状态
                                loadWorker.stateProperty().addListener { _, _, newState ->
                                    when (newState) {
                                        Worker.State.SUCCEEDED -> {
                                            isLoading = false
                                            errorMessage = null
                                            // 注入JavaScript来修复可能的布局问题
//                                            webEngine.executeScript("""
//                                                document.body.style.margin = '0';
//                                                document.body.style.padding = '0';
//                                                document.documentElement.style.margin = '0';
//                                                document.documentElement.style.padding = '0';
//                                            """.trimIndent())
                                        }
                                        Worker.State.FAILED -> {
                                            isLoading = false
                                            errorMessage = "加载失败，请检查网络连接"
                                        }
                                        Worker.State.RUNNING -> {
                                            isLoading = true
                                            errorMessage = null
                                        }
                                        else -> {}
                                    }
                                }

                                // 设置用户样式表
//                                userStyleSheetLocation = """
//                                    data:text/css;charset=utf-8,
//                                    html, body {
//                                        margin: 0 !important;
//                                        padding: 0 !important;
//                                        width: 100vw !important;
//                                        height: 100vh !important;
//                                        overflow: auto !important;
//                                    }
//                                    * {
//                                        box-sizing: border-box !important;
//                                    }
//                                """.trimIndent()

                                // 加载网页
                                load(url)
                            }
                            val scene = Scene(webView).apply {
                                // 设置场景背景为透明
                                fill = null
                                // 移除场景的默认样式
                                stylesheets.clear()
                            }

                            jfxPanel.scene = scene


                        } catch (e: Exception) {
                            errorMessage = "发生错误: ${e.message}"
                            isLoading = false
                        }
                    }
                }
            }
        )

        // 显示加载指示器
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 显示错误消息
        errorMessage?.let { error ->
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("错误") },
                text = { Text(error) },
                confirmButton = {
                    Button(onClick = { errorMessage = null }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}