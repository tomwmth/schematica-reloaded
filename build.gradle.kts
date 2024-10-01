import org.apache.commons.lang3.SystemUtils

plugins {
    id("java")
    id("gg.essential.loom") version "1.6.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.gradleup.shadow") version ("8.3.2")
    id("net.kyori.blossom") version ("1.3.1")
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
        forge("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}-${minecraftVersion}")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    }
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1/")
}

dependencies {
    shade(project(":core"))
    shade(project(":api"))

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
            if (SystemUtils.IS_OS_LINUX) {
                environmentVariable("__GL_THREADED_OPTIMIZATIONS", "0")
            }
        }
        remove(getByName("server"))
    }
}

blossom {
    replaceToken("\${modId}", modId)
    replaceToken("\${modName}", modName)
    replaceToken("\${modVersion}", modVersion)
    replaceToken("\${minecraftVersion}", minecraftVersion)
    replaceToken("\${forgeVersion}", forgeVersion)
}

sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    inputs.property("modId", modId)
    inputs.property("modName", modName)
    inputs.property("modVersion", modVersion)
    inputs.property("minecraftVersion", minecraftVersion)
    inputs.property("forgeVersion", forgeVersion)

    filesMatching("mcmod.info") {
        expand(
            "modId" to modId,
            "modName" to modName,
            "modVersion" to modVersion,
            "minecraftVersion" to minecraftVersion,
            "forgeVersion" to forgeVersion
        )
    }
}

base {
    archivesName.set("${modId}-mc${minecraftVersion}")
}

tasks.jar {
    manifest.attributes(
        "FMLAT" to "${modId}_at.cfg",
        "ModSide" to "CLIENT",
        "ForceLoadAsMod" to "true",
    )
}

tasks.shadowJar {
    archiveClassifier.set("")

    from(rootProject.file("LICENSE")) {
        into("META-INF/")
    }

    from(rootProject.file("NOTICE")) {
        into("META-INF/")
    }

    configurations.clear()
    configurations.add(shade)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.assemble {
    dependsOn(tasks.remapJar)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
