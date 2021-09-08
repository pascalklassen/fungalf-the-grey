package io.github.pascalklassen.fungalf

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import io.github.pascalklassen.fungalf.extensions.PokeCordExtension
import io.github.pascalklassen.fungalf.extensions.TestExtension

object BotSpec: ConfigSpec("BOT") {
    val token by required<String>(name = "TOKEN")
}

suspend fun main() {
    val config = Config { addSpec(BotSpec) }.from.env()

    val bot = ExtensibleBot(config[BotSpec.token]) {
        extensions {
            add(::TestExtension)
            add(::PokeCordExtension)
        }

        chatCommands {
            defaultPrefix = "?"
            enabled = true
        }

        presence {
            listening("?help")
        }
    }

    bot.start()
}

