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

repositories {
    mavenCentral()
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("de.oceanlabs.mcp:mcp_stable:${mcpVersion}-${minecraftVersion}")
    forge("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")

    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")
}

loom {
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
    runConfigs {
        getByName("client") {
            property("devauth.enabled", "true")
        }
        remove(getByName("server"))
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}
