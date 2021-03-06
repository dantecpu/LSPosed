/*
 * This file is part of LSPosed.
 *
 * LSPosed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LSPosed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LSPosed.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2021 LSPosed Contributors
 */
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.nio.file.Paths;

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    val androidTargetSdkVersion: Int by extra
    val androidMinSdkVersion: Int by extra
    val androidBuildToolsVersion: String by extra
    val androidCompileSdkVersion: Int by extra
    val androidCompileNdkVersion: String by extra
    val androidSourceCompatibility: JavaVersion by extra
    val androidTargetCompatibility: JavaVersion by extra

    compileSdkVersion(androidCompileSdkVersion)
    ndkVersion = androidCompileNdkVersion
    buildToolsVersion(androidBuildToolsVersion)

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId("io.github.lsposed.manager")
        minSdkVersion(androidMinSdkVersion)
        targetSdkVersion(androidTargetSdkVersion)
        versionCode(extra["versionCode"] as Int)
        versionName(extra["versionName"] as String)
        resConfigs("en", "zh-rCN", "zh-rTW", "zh-rHK", "ru", "uk", "nl", "ko", "fr")
        resValue("string", "versionName", extra["versionName"] as String)
    }

    compileOptions {
        targetCompatibility(androidTargetCompatibility)
        sourceCompatibility(androidSourceCompatibility)
    }

    lintOptions {
        disable("MissingTranslation")
        disable("ExtraTranslation")
        isAbortOnError = true
        isCheckReleaseBuilds = true
    }

    packagingOptions {
        exclude("META-INF/**")
        exclude("kotlin/**")
        exclude("org/**")
        exclude("**.properties")
        exclude("**.bin")
    }

    dependenciesInfo.includeInApk = false

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        named("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    applicationVariants.all {
        outputs.map { it as BaseVariantOutputImpl }.forEach { output ->
            output.outputFileName = "LSPosedManager-${defaultConfig.versionName}-${defaultConfig.versionCode}-${buildType.name}.apk"
        }
    }
}

val optimizeReleaseResources = task("optimizeReleaseResources").doLast {
    val aapt2 = Paths.get(project.android.sdkDirectory.path, "build-tools", project.android.buildToolsVersion, "aapt2")
    val zip = Paths.get(project.buildDir.path, "intermediates", "processed_res", "release", "out", "resources-release.ap_")
    val optimized = File("${zip}.opt")
    val cmd = exec {
        commandLine(aapt2, "optimize", "--collapse-resource-names", "--shorten-resource-paths", "-o", optimized, zip)
        isIgnoreExitValue = false
    }
    if (cmd.exitValue == 0) {
        delete(zip)
        optimized.renameTo(zip.toFile())
    }
}

tasks.whenTaskAdded {
    if (name == "processReleaseResources") {
        finalizedBy(optimizeReleaseResources)
    }
}

dependencies {
    val glideVersion = "4.12.0"
    val markwonVersion = "4.6.2"
    val okhttpVersion = "4.9.1"
    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")
    implementation("androidx.activity:activity:1.2.0")
    implementation("androidx.browser:browser:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.fragment:fragment:1.3.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.caverock:androidsvg-aar:1.4")
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glideVersion")
    implementation("com.github.jinatonic.confetti:confetti:1.1.2")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.takisoft.preferencex:preferencex:1.1.0")
    implementation("com.takisoft.preferencex:preferencex-colorpicker:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:$okhttpVersion")
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:ext-strikethrough:$markwonVersion")
    implementation("io.noties.markwon:ext-tables:$markwonVersion")
    implementation("io.noties.markwon:ext-tasklist:$markwonVersion")
    implementation("io.noties.markwon:html:$markwonVersion")
    implementation("io.noties.markwon:image-glide:$markwonVersion")
    implementation("io.noties.markwon:linkify:$markwonVersion")
    implementation("rikka.appcompat:appcompat:1.2.0-rc01")
    implementation("rikka.core:core:1.3.0")
    implementation("rikka.insets:insets:1.0.1")
    implementation("rikka.material:material:1.6.0")
    implementation("rikka.recyclerview:recyclerview-utils:1.2.0")
    implementation("rikka.widget:borderview:1.0.0")
    implementation("rikka.widget:switchbar:1.0.2")
    implementation("rikka.layoutinflater:layoutinflater:1.0.1")
    implementation("tech.rectifier.preferencex-android:preferencex-simplemenu:88f93154b2")
    implementation("me.zhanghai.android.appiconloader:appiconloader-glide:1.2.0")
    implementation("me.zhanghai.android.fastscroll:library:1.1.5")
    implementation(files("libs/WeatherView-2.0.3.aar"))
    implementation(project(":manager-service"))
}

configurations {
    compile.get().exclude(group = "org.jetbrains", module = "annotations")
    compile.get().exclude(group = "androidx.appcompat", module = "appcompat")
//    cleanedAnnotations()
}

