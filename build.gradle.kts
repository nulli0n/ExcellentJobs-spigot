plugins {
    id("xyz.jpenilla.run-paper") version "2.3.0"
    java
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        url = uri("https://repo.nightexpressdev.com/releases")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }

    maven {
        url = uri("https://mvn.lumine.io/repository/maven-snapshots/")
    }

    maven {
        url = uri("https://repo.momirealms.net/releases")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven(url="https://central.sonatype.com/")
    maven("https://repo.codemc.io/repository/EvenMoreFish/")
}

dependencies {
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.su.nightexpress.nightcore.main)
    compileOnly(libs.su.nightexpress.economybridge.economy.bridge)
    compileOnly(libs.com.comphenix.protocol.protocollib)
    compileOnly(libs.com.github.retrooper.packetevents.spigot)
    compileOnly(libs.com.github.milkbowl.vaultapi) {
        // Breaks paper api if enabled
        isTransitive=false
    }
    compileOnly(libs.me.clip.placeholderapi)
    compileOnly(libs.io.lumine.mythic.dist)
    compileOnly(libs.io.github.arcaneplugins.levelledmobs.levelledmobs)
    compileOnly(libs.com.oheers.fish.evenmorefish)
    compileOnly(libs.net.momirealms.custom.fishing)
    compileOnly(libs.net.momirealms.custom.crops)
}

group = "su.nightexpress.excellentjobs"
version = "1.13.1"
description = "ExcellentJobs"
// java.sourceCompatibility = JavaVersion.VERSION_1_8

/*
publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
*/

tasks.runServer {
    minecraftVersion("1.21.4")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
