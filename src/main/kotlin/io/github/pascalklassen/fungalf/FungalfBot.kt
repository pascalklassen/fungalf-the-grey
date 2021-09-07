package io.github.pascalklassen.fungalf

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import io.github.pascalklassen.fungalf.command.CommandHandler
import io.github.pascalklassen.fungalf.command.PokeCordCommand
import io.github.pascalklassen.fungalf.dsl.message.ButtonHandlerList
import io.github.pascalklassen.fungalf.persistence.Database
import io.github.pascalklassen.fungalf.persistence.trainer.TrainerRegistry
import mu.KotlinLogging
import net.dv8tion.jda.api.JDABuilder

const val BOT_NAME = "Fungalf the Grey"
const val PREFIX = "?"

private val LOGGER = KotlinLogging.logger {}

object FungalfBot {

    private val config = Config { addSpec(BotSpec) }.from.env()

    private val jda = JDABuilder
        .createDefault(config[BotSpec.token])
        .build()

    fun start() {
        LOGGER.info { "$BOT_NAME is starting!" }
        jda.addEventListener(ButtonHandlerList)
        jda.addEventListener(
            CommandHandler(
                listOf(
                    PokeCordCommand()
                )
            )
        )

        Database.init()
    }

    fun stop() {
        LOGGER.info { "$BOT_NAME is shutting down!" }
        jda.shutdown()
        TrainerRegistry.close()
        Database.close()
    }

    object BotSpec: ConfigSpec("BOT") {
        val token by required<String>(name = "TOKEN")
    }
}
