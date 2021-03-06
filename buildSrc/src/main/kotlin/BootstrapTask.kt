import com.savvasdalkitsis.jsonmerger.JsonMerger
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Paths
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


open class BootstrapTask : DefaultTask() {

    private fun formatDate(date: Date?) = with(date ?: Date()) {
        SimpleDateFormat("yyyy-MM-dd").format(this)
    }

    private fun hash(file: ByteArray): String {
        return MessageDigest.getInstance("SHA-512").digest(file).fold("", { str, it -> str + "%02x".format(it) }).toUpperCase()
    }
    

    @TaskAction
    fun boostrap() {
        if (project == project.rootProject) {
            val bootstrapDir = File("${project.projectDir}")
            val bootstrapReleaseDir = File("${project.projectDir}/release/")

            bootstrapDir.mkdirs()
            bootstrapReleaseDir.mkdirs()

            val plugins = ArrayList<JSONObject>()
            //val baseBootstrap = getBootstrap() ?: throw RuntimeException("Base bootstrap is null!")

            project.subprojects.forEach {
                if (it.project.properties.containsKey("PluginName") && it.project.properties.containsKey("PluginDescription")) {
                    var pluginAdded = false
                    val plugin = it.project.tasks.get("jar").outputs.files.singleFile

                    val releases = ArrayList<JsonBuilder>()

                    releases.add(JsonBuilder(
                        "version" to it.project.version,
                        "requires" to ProjectVersions.apiVersion,
                        "date" to formatDate(Date()),
                        //private
                        "url" to "https://raw.githubusercontent.com/FusionClient/plugin-release/master/release/${it.project.name}-${it.project.version}.jar",
                        "sha512sum" to hash(plugin.readBytes())
                    ))

                    val pluginObject = JsonBuilder(
                        "name" to it.project.extra.get("PluginName"),
                        "id" to nameToId(it.project.extra.get("PluginName") as String),
                        "description" to it.project.extra.get("PluginDescription"),
                        "provider" to it.project.extra.get("PluginProvider"),
                        "projectUrl" to it.project.extra.get("ProjectUrl"),
                        "releases" to releases.toTypedArray()
                    ).jsonObject()

                    if (!pluginAdded)
                    {
                        plugins.add(pluginObject)
                    }

                    plugin.copyTo(Paths.get(bootstrapReleaseDir.toString(), "${it.project.name}-${it.project.version}.jar").toFile())
                }
            }

            File(bootstrapDir, "plugins.json").printWriter().use { out ->
                out.println(plugins.toString())
            }
        }

    }
}
