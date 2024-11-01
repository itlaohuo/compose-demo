import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.panteleyev.jpackage.ImageType
import org.panteleyev.jpackage.JPackageTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.panteleyev.jpackageplugin") version "1.6.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
//    maven ("https://maven.aliyun.com/repository/jcenter")
//    maven ("https://maven.aliyun.com/repository/google")
//    maven ("https://maven.aliyun.com/repository/gradle-plugin")
//    maven ("https://maven.aliyun.com/repository/public")
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.desktop:desktop:1.6.0") // 请使用最新版本
    implementation("org.jetbrains.compose.ui:ui:1.6.0") // 请使用最新版本
    implementation("org.jetbrains.compose.material:material:1.6.0") // 请使
}
javafx {
    version = "17.0.13"
    modules = listOf("javafx.controls", "javafx.web", "javafx.swing",  "javafx.graphics","javafx.fxml")
//    The plugin will include JavaFX dependencies for the current platform. However, a different target platform can also be specified.
//    Supported targets are:
//
//    linux
//    linux-aarch64
//    win or windows
//    osx or mac or macos
//    osx-aarch64 or mac-aarch64 or macos-aarch64 (support added in JavaFX 11.0.12 LTS and JavaFX 17 GA)
    // JavaFX application require native binaries for each platform to run. By default, the plugin will include these binaries for the target platform.
    // Native dependencies can be avoided by declaring the dependency configuration as compileOnly.
//    configuration = "compileOnly"
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
//            includeAllModules = true
            modules( "java.instrument", "java.net.http", "jdk.jfr", "jdk.jsobject", "jdk.unsupported", "jdk.unsupported.desktop", "jdk.xml.dom")
            packageName = "compose-demo"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("src/main/resources/")

            linux {
                iconFile.set(iconsRoot.resolve("icons8-64.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("icons8-64.ico"))
            }

//            macOS {
//                iconFile.set(iconsRoot.resolve("icons8-64.ico"))
//            }
        }
    }
}

