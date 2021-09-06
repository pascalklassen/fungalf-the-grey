package io.github.pascalklassen.fungalf

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import io.github.pascalklassen.fungalf.command.Context
import io.github.pascalklassen.fungalf.command.PokeCordCommand
import io.github.pascalklassen.fungalf.dsl.message.ButtonHandlerList
import io.github.pascalklassen.fungalf.dsl.message.createMessage
import io.github.pascalklassen.fungalf.persistence.Database
import io.github.pascalklassen.fungalf.persistence.trainer.TrainerRegistry
import io.github.pascalklassen.pokefuture.pokemon.Pokemon
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

const val BOT_NAME = "Fungalf the Grey"
const val PREFIX = "?"

private val LOGGER = KotlinLogging.logger {}

object FungalfBot: ListenerAdapter() {

    private val config = Config { addSpec(BotSpec) }.from.env()

    private val jda = JDABuilder
        .createDefault(config[BotSpec.token])
        .build()

    private val command = PokeCordCommand()

    fun start() {
        LOGGER.info { "$BOT_NAME is starting!" }
        jda.addEventListener(ButtonHandlerList)
        jda.addEventListener(this)
        Database.init()
        Pokemon.fetch("1")
            .onSuccess { LOGGER.info { it.name } }
            .onFailure { LOGGER.error { it.message } }
    }

    fun stop() {
        LOGGER.info { "$BOT_NAME is shutting down!" }
        jda.shutdown()
        TrainerRegistry.close()
        Database.close()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.channel.type != ChannelType.TEXT) return
        if (!event.message.contentRaw.startsWith(PREFIX)) return

        val tokens = tokensOf(event.message.contentRaw.lowercase())

        try {
            command.execute(Context(event, tokens.copyOfRange(1, tokens.size), jda))
        } catch (ex: IllegalArgumentException) {
            val message = createMessage {
                embed {
                    title { +"Ungültige Eingabe" }
                    +ex.message!!
                    color = Color.RED
                }
            }

            event.channel.sendMessage(message)
        }
    }

    private fun tokensOf(input: String) =
        input
            .replace("\\s+", " ")
            .split(" ")
            .toTypedArray()

    object BotSpec: ConfigSpec("BOT") {
        val token by required<String>(name = "TOKEN")
    }
}
