import com.android.build.gradle.api.AndroidBasePlugin
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream


fun String.execute() = runCatching {
    ByteArrayOutputStream().also {
        exec {
            commandLine(this@execute.split(" "))
            standardOutput = it
        }
    }.toString().trim().takeIf { it.isNotBlank() }
}.getOrNull()

val commitCount by lazy { "git rev-list --count HEAD".execute()?.toInt() }
val recentTag by lazy { "git describe --abbrev=0 --tags".execute() }
val sdks by extra { 30 to 34 }
val versions by extra { (recentTag?.replace("v", "") ?: "1.0") to (commitCount ?: 0) }
val packageName by extra("com.chillyblaze.autonight")

plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.kotlin) apply false
}

subprojects {
    plugins.withType<AndroidBasePlugin>().whenObjectAdded {
        extensions.configure<BaseAppModuleExtension> {
            composeOptions.kotlinCompilerExtensionVersion = libs.versions.kotlin.compiler.get()
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
            applicationVariants.all {
                outputs.all {
                    val filename = "${rootProject.name}_${versions.first}_${buildType.name}.apk"
                    (this as BaseVariantOutputImpl).outputFileName = filename
                }
            }
        }
        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = JvmTarget.JVM_17.target
        }
    }
}