plugins {
    id("java")
    id("gg.essential.loom") version "1.6.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val mcpVersion: String by project

group = "dev.tomwmth"
version = modVersion

allprojects {
    apply(plugin = "gg.essential.loom")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    dependencies {
        minecraft("com.mojang:minecraft:${minecraftVersion}")
        mappings("de.oceanlabs.mcp:mcp_stable:${mcpVersion}-${minecraftVersion}")
        forge("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":api"))
    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")
}

loom {
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        accessTransformer("src/main/resources/META-INF/${modId}_at.cfg")
    }
    runConfigs {
        getByName("client") {
            property("devauth.enabled", "true")
        }
        remove(getByName("server"))
    }
}

tasks.jar {
    manifest.attributes(
        "FMLAT" to "${modId}_at.cfg",
        "ModSide" to "CLIENT",
        "ForceLoadAsMod" to "true",
    )
}
