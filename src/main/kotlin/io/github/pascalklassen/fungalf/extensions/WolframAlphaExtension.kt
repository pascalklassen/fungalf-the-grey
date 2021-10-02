package io.github.pascalklassen.fungalf.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import mu.KotlinLogging
import java.lang.Exception

private val LOGGER = KotlinLogging.logger {}

object WolframSpec: ConfigSpec("WOLFRAMALPHA") {
    val appId by required<String>("APPID")
}

class WolframAlphaExtension: Extension() {
    override val name = "wolframalpha"
    override val bundle = "fungalf.strings"

    val config = Config {
        addSpec(WolframSpec)
    }.from.env()

    val client = HttpClient(CIO) {
        install(Logging) {
            logger = object: Logger {
                override fun log(message: String) {
                    LOGGER.trace { message }
                }
            }

            level = LogLevel.ALL
        }

        install(JsonFeature) {
            serializer = JacksonSerializer()
        }

        defaultRequest {
            host = "api.wolframalpha.com"
            parameter("appid", config[WolframSpec.appId])
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::ShortArguments) {
            name = "extensions.wolframalpha.short.commandName"
            description = "extensions.wolframalpha.short.commandDescription"

            action {
                respond {
                    content = try {
                        client.get<String>(path ="/v1/result") {
                            parameter("i", arguments.query)
                        }
                    } catch (ex: Exception) {
                        translate("extensions.wolframalpha.short.error.noAnswer")
                    }
                }
            }
        }

        publicSlashCommand(::SimpleArguments) {
            name = "extensions.wolframalpha.simple.commandName"
            description = "extensions.wolframalpha.simple.commandDescription"

            action {
                respond {
                    try {
                        addFile("result.gif", client.get(path = "/v1/simple") {
                            parameter("fontsize", "32")
                            parameter("layout", "labelbar")
                            parameter("i", arguments.query)
                        })
                    } catch (ex: Exception) {
                        content = translate("extensions.wolframalpha.simple.error.noAnswer")
                    }
                }
            }
        }
    }

    inner class ShortArguments: Arguments() {
        val query by coalescedString(
            "query",
            "extensions.wolframalpha.short.commandArguments.query"
        )
    }

    inner class SimpleArguments: Arguments() {
        val query by coalescedString(
            "query",
            "extensions.wolframalpha.simple.commandArguments.query"
        )
    }
}
