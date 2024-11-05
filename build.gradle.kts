import org.gradle.internal.impldep.org.apache.sshd.common.util.OsUtils
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.6.0"
    id("de.undercouch.download") version "4.1.1"
//    id ("com.google.osdetector") version "1.7.3"
//    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

val libraryPath = "third_party/java-cef"
val hostOs = System.getProperty("os.name")
println(hostOs)
//val target = when {
//    hostOs == "Mac OS X" -> "macos"
//    hostOs == "Linux" -> "linux"
//    hostOs.startsWith("Win") -> "windows"
//    else -> throw Error("Unknown os $hostOs")
//}

val target = when {
    hostOs == "Mac OS X" -> "macos"
    hostOs == "Linux" -> "linux"
    hostOs.startsWith("Win") -> "windows-amd64"
    else -> throw Error("Unknown os $hostOs")
}


val cefDownloadZip = run {
//    val zipName = "jcef-runtime-$target.zip"
    val zipName = "$target.tar.gz"
    val zipFile = File("third_party/$zipName")

    tasks.register("downloadCef", Download::class) {
        onlyIf { !zipFile.exists() }
        // https://github.com/jcefmaven/jcefbuild/releases/download/1.0.61/windows-amd64.tar.gz
        src("https://github.com/jcefmaven/jcefbuild/releases/download/1.0.61/$zipName")
        dest(zipFile)
        onlyIfModified(true)
    }.map { zipFile }
}

val cefUnZip = run {
    val targetDir = File("third_party/java-cef").apply { mkdirs() }
    tasks.register("unzipCef", Copy::class) {
        from(cefDownloadZip.map { zipTree(it) })
        into(targetDir)
    }.map { targetDir }
}


repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    // temp
    maven("https://packages.jetbrains.team/maven/p/ui/dev")
}

dependencies {
    // compose for desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.linux_arm64)
    implementation(compose.desktop.linux_x64)
    implementation(compose.desktop.windows_x64)
//    implementation("org.jetbrains.compose.desktop:desktop:1.6.10")
//    implementation("org.jetbrains.compose.ui:ui:1.6.11")
//    implementation("org.jetbrains.compose.material:material:1.6.10")

    // JCEF
    implementation("me.friwi:jcefmaven:127.3.1")
//    implementation("org.jetbrains.jcef:jcef-skiko:0.1")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    dependsOn(cefUnZip)
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
//             modules("java.instrument", "java.prefs", "java.sql", "jdk.unsupported")
            modules("java.instrument", "java.prefs", "jdk.jfr", "jdk.unsupported", "me.friwi:jcefmaven")
            packageName = "compose-demo"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("src/main/resources/")
            jvmArgs += listOf(
                "-Djava.library.path=$libraryPath"
            )
            linux {
                iconFile.set(iconsRoot.resolve("icons8-64.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("icons8-64.ico"))
            }

            macOS {
                iconFile.set(iconsRoot.resolve("icons8-64.ico"))
            }
        }
    }
}

