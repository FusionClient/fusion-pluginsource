import ProjectVersions.rlVersion

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    java
    checkstyle
}

apply<BootstrapPlugin>()
apply<VersionPlugin>()
apply<UpdateVersionsPlugin>()

allprojects {
    group = "com.openosrs"
    version = ProjectVersions.rlVersion
    apply<MavenPublishPlugin>()
}

subprojects {
    group = "com.openosrs.externals"

    project.extra["PluginProvider"] = "Fusion Plugins"
    project.extra["ProjectUrl"] = "https://discord.gg/UBMDQ6WjYq"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    repositories {
         jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
                excludeGroupByRegex("com\\.runelite.*")
            }
        }

        exclusiveContent {
            forRepository {
                 maven {
                    url = uri("https://repo.runelite.net")
                }
            }
            filter {
                includeModule("net.runelite", "discord")
            }
        }

        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://raw.githubusercontent.com/open-osrs/hosting/master")
                }
            }
            filter {
                includeModule("com.openosrs.rxrelay3", "rxrelay")
            }
        }

        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://raw.githubusercontent.com/open-osrs/hosting/master")
                }
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }

    apply<JavaPlugin>()
    dependencies {
        annotationProcessor(Libraries.lombok)
        annotationProcessor(Libraries.pf4j)

        compileOnly("com.openosrs:http-api:$rlVersion+")
        compileOnly("com.openosrs:runelite-api:$rlVersion+")
        compileOnly("com.openosrs:runelite-client:$rlVersion+")
        compileOnly("com.openosrs.rs:runescape-api:$rlVersion+")

        compileOnly(Libraries.apacheCommonsText)
        compileOnly(Libraries.gson)
        compileOnly(Libraries.guice)
        compileOnly(Libraries.lombok)
        compileOnly(Libraries.okhttp3)
        compileOnly(Libraries.pf4j)
        compileOnly(Libraries.rxjava)
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("$buildDir/repo")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        /*  withType<Jar> {
              doLast {
                  copy {
                      from("./build/libs/")
                      into("../release/")
                  }
              }
          } */

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"

            exclude("**/ScriptVarType.java")
            exclude("**/LayoutSolver.java")
            exclude("**/RoomType.java")
        }

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}
    tasks {
        register<Delete>("bootstrapClean") {
            delete("release/")
    }
}
