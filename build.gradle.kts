import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "3.1.1"
    //Plugin SpringBoot
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.xedric_tech"
version = "1.0.1"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.xedric_tech.password_manager")
    mainClass.set("com.xedric_tech.password_manager.MainFxApp")
}

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}


dependencies {

    //Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    //H2 Database
    implementation("com.h2database:h2:2.3.232")

    //SpringBoot Starter Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.4")


    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    //AtlantaFX CSS Styling
    implementation("io.github.mkpaz:atlantafx-base:2.0.1")

    //IconPack from Ikonli
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    //Ikonli Material2 Pack
    implementation("org.kordamp.ikonli:ikonli-material2-pack:12.4.0")

    implementation("jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1")
    //implementation("jakarta.activation:jakarta.activation-api:2.1.3")

}

tasks.withType<Test> {
    useJUnitPlatform()
}


jlink {

    mergedModule {
        uses("org.hibernate.boot.registry.classloading.spi.ClassLoaderService")
        uses("org.hibernate.bytecode.spi.BytecodeProvider")
        uses("org.springframework.beans.factory.FactoryBean")
    }

    options.set(listOf(
        "--strip-debug",
        "--compress", "2",
        "--no-header-files",
        "--no-man-pages"
    ))

    forceMerge("jakarta.enterprise.cdi")

    launcher {
        name = "PasswordXManager"
    }

    jpackage {
        imageName = "PasswordXManager" // Nome dell'app
        installerType = "exe" // Crea un .exe

        // Specifica l'icona e le opzioni per Windows
        installerOptions.addAll(
            listOf(
                "--win-shortcut",
                "--win-menu",
                "--icon", "src/main/resources/icons/app.ico"
            )
        )
    }
}

val bootJar = tasks.named<BootJar>("bootJar")

tasks.register<Exec>("makeAppImage") {
    group = "packaging"
    dependsOn("bootJar") // Assicura che il fatjar sia creato prima

    val outputDir = layout.buildDirectory.dir("jpackage")
    commandLine = listOf(
        "jpackage",
        "--type", "app-image",
        "--name", "PasswordXManager",
        "--input", "build/libs",
        "--main-jar", bootJar.get().archiveFileName.get(), // <-- nome ESATTO del fatjar
        "--main-class", "com.xedric_tech.password_manager.MainFxApp",
        "--icon", "src/main/resources/icons/app.ico",
        "--runtime-image", "C:/Program Files/Java/jdk-22",
        "--dest", outputDir.get().asFile.absolutePath
        // puoi aggiungere altre opzioni se vuoi
    )
    doLast {
        val jarName = bootJar.get().archiveFileName.get()
        val imageRoot = outputDir.get().asFile.resolve("PasswordXManager")
        val bat = imageRoot.resolve("runPasswordXManager.bat")
        bat.writeText("""
            @echo off
            start "" ".\runtime\bin\javaw.exe" -jar ".\app\$jarName"
            exit
        """.trimIndent())
    }
}
