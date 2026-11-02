plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.fabric.loom.plugin)
    implementation(libs.foojay.resolver.convention.plugin)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
}
