import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val allureVersion = "2.29.1"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    id("io.kotest.multiplatform") version "6.0.0.M4"

    id("org.jetbrains.kotlinx.kover") version "0.9.1"

    id("io.qameta.allure") version "2.8.1"
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2")
            implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")

            implementation("io.kotest:kotest-framework-engine:6.0.0.M5")
            implementation("io.kotest:kotest-assertions-core:6.0.0.M5")
            implementation("io.kotest:kotest-property:6.0.0.M5")

            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
            implementation("org.jetbrains.kotlin:kotlin-test-junit5:2.2.0")

            implementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")

            implementation("org.slf4j:slf4j-simple:2.0.16")

            implementation("io.kotest.extensions:kotest-extensions-allure:1.4.0")

            implementation(project.dependencies.platform("io.qameta.allure:allure-bom:$allureVersion"))
            implementation("io.qameta.allure:allure-junit5")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.qmpu842.labs.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.qmpu842.labs"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
    systemProperty("allure.results.directory", project.buildDir.toString() + "/allure-results")
}

allure {
    autoconfigure = false
    version = allureVersion
}

task("AllTheReporting") {
    dependsOn("allTests")
    dependsOn("koverBinaryReport")
    dependsOn("koverXmlReport")
    dependsOn("koverHtmlReport")
}
