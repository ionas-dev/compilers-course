plugins {
    java
    antlr
    application
}

group = "edu.kit.kastel.logic"
version = "1.0-SNAPSHOT"

application {
    mainModule = "edu.kit.kastel.vads.compiler"
    mainClass = "edu.kit.kastel.vads.compiler.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jspecify:jspecify:1.0.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    antlr("org.antlr:antlr4:4.13.2")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(23)
}

tasks.generateGrammarSource {
    outputDirectory = file("build/generated/sources/antlr/main/edu/kit/kastel/vads/compiler/antlr")
    arguments = arguments + listOf("-visitor", "-package", "edu.kit.kastel.vads.compiler.antlr")
}

sourceSets["main"].java.srcDir("build/generated/sources/antlr/main")


tasks.test {
    useJUnitPlatform()
}