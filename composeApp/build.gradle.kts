import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val allureVersion = "2.29.1"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    id("io.kotest.multiplatform") version "6.0.0.M4"

    id("org.jetbrains.kotlinx.kover") version "0.9.1"
//    id("io.gitlab.arturbosch.detekt") version "1.23.8"

//    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"

//    id("io.qameta.allure")
    id("io.qameta.allure") version "2.8.1"
}

// Define the version of AspectJ
// val aspectJVersion = "1.9.21"

// Define configuration for AspectJ agent
// val agent1: Configuration by configurations.creating {
//    isCanBeConsumed = true
//    isCanBeResolved = true
// }

// val agent1 = agent("org.aspectj:aspectjweaver:${aspectJVersion}")
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
//            implementation("io.kotest:kotest-framework-api:6.0.0.M5")
            implementation("io.kotest:kotest-framework-engine:6.0.0.M5")
            implementation("io.kotest:kotest-assertions-core:6.0.0.M5")
            implementation("io.kotest:kotest-property:6.0.0.M5")

            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
            implementation("org.jetbrains.kotlin:kotlin-test-junit5:2.2.0")

//            implementation("io.qameta.allure:allure-junit5:2.29.0")
            implementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")

            implementation("org.slf4j:slf4j-simple:2.0.16")

//            implementation("io.kotest:kotest-runner-junit5:5.3.2")
            implementation("io.kotest.extensions:kotest-extensions-allure:1.4.0")

            implementation(project.dependencies.platform("io.qameta.allure:allure-bom:$allureVersion"))
            // Add necessary Allure dependencies to dependencies section
            implementation("io.qameta.allure:allure-junit5")
//            agent1
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

// allure {
//    adapter.autoconfigure.set(false)
//    version.set("2.13.1")
// }
//
// tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs = listOf("-Xjsr305=strict")
//        jvmTarget = "1.8"
//    }
// }

// ktlint {
// }

// detekt {
//    // Version of detekt that will be used. When unspecified the latest detekt
//    // version found will be used. Override to stay on the same version.
//    toolVersion = "1.23.8"
//
//    // The directories where detekt looks for source files.
//    // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
//    source.setFrom(
//        "src/main/java",
//        "src/main/kotlin",
//        "composeApp/src/desktopMain/kotlin/io/qmpu842/labs",
//        "composeApp/src/desktopTest/kotlin/io/qmpu842/labs",
//        "src/desktopMain/kotlin/io/qmpu842/labs",
//        "src/desktopTest/kotlin/io/qmpu842/labs",
//    )
//
//    // Builds the AST in parallel. Rules are always executed in parallel.
//    // Can lead to speedups in larger projects. `false` by default.
//    parallel = false
//
//    // Define the detekt configuration(s) you want to use.
//    // Defaults to the default detekt configuration.
//    config.setFrom("../detekt.yml")
// //    config.setFrom(rootProject.file("detekt.yml"))
//
//    autoCorrect = true
//
//    // Applies the config files on top of detekt's default config file. `false` by default.
// //    buildUponDefaultConfig = false
//    buildUponDefaultConfig = true
//
//    // Turns on all the rules. `false` by default.
//    allRules = false
//
//    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
// //    baseline = file("path/to/baseline.xml")
//
//    // Disables all default detekt rulesets and will only run detekt with custom rules
//    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
//    disableDefaultRuleSets = false
//
//    // Adds debug output during task execution. `false` by default.
//    debug = false
//
//    // If set to `true` the build does not fail when there are any issues.
//    // Defaults to `false`.
//    ignoreFailures = false
//
//    // Android: Don't create tasks for the specified build types (e.g. "release")
//    ignoredBuildTypes = listOf("release")
//
//    // Android: Don't create tasks for the specified build flavor (e.g. "production")
//    ignoredFlavors = listOf("production")
//
//    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
//    ignoredVariants = listOf("productionRelease")
//
//    // Specify the base path for file paths in the formatted reports.
//    // If not set, all file paths reported will be absolute file path.
// //    basePath.set(projectDir)
// }

allure {
    autoconfigure = false
    version = "2.13.1"
}
