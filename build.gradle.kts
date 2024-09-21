plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    kotlin("jvm") version "2.1.0-Beta1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "de.cypdashuhn"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
    maven("https://repo.extendedclip.com/content/repositories/gradle-plugins/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.google.code.gson:gson:2.10.1")

    // exposed
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-crypt:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.49.0")
    implementation("org.jetbrains.exposed:exposed-json:0.49.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.49.0")
    implementation("org.jetbrains.exposed:exposed-money:0.49.0")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    //implementation("com.github.CypDasHuhn:Rooster:3bfecdc7a1")
    implementation("io.github.classgraph:classgraph:4.8.170")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("com.github.seeseemelk:MockBukkit-v1.21:3.127.1")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

bukkit {
    name = "ExtendedInventoryPlugin"
    main = "de.cypdashuhn.extendedInventoryPlugin.Main"
    apiVersion = "1.21"

    commands {
        register("ei")
    }
}
tasks {
    runServer {
        minecraftVersion("1.21.1")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}