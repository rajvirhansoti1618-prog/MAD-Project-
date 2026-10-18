plugins {
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
