import ProjectVersions.rlVersion

version = "1.0.2"

project.extra["PluginName"] = "[F] Server Variance"
project.extra["PluginDescription"] = "Checks the ping of the server"

dependencies {
    annotationProcessor(Libraries.lombok)
    annotationProcessor(Libraries.pf4j)

    compileOnly("com.openosrs:runelite-api:$rlVersion")
    compileOnly("com.openosrs:runelite-client:$rlVersion")

    compileOnly(Libraries.guice)
    compileOnly(Libraries.lombok)
    compileOnly(Libraries.pf4j)
    compileOnly(Libraries.rxjava)
    compileOnly(Libraries.apacheCommonsText)
    compileOnly(Libraries.annotations)
}

tasks {
    jar {
        manifest {
            attributes(mapOf(
                "Plugin-Version" to project.version,
                "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                "Plugin-Provider" to project.extra["PluginProvider"],
                "Plugin-Description" to project.extra["PluginDescription"],
                "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}