package io.github.pascalklassen.fungalf

import io.github.pascalklassen.fungalf.command.Context
import io.github.pascalklassen.fungalf.command.PokeCordCommand
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

class FungalfBot(args: Array<String>): ListenerAdapter() {

    private val client = JDABuilder
        .createDefault(args.first())
        .build()

    private val command = PokeCordCommand()

    fun start() {
        LOGGER.info { "$BOT_NAME is starting!" }
        client.addEventListener(this)
    }

    fun stop() {
        LOGGER.info { "$BOT_NAME is shutting down!" }
        client.shutdown()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.channel.type != ChannelType.TEXT) return
        if (!event.message.contentRaw.startsWith(PREFIX)) return

        val tokens = tokensOf(event.message.contentRaw.lowercase())

        try {
            command.execute(Context(event, tokens.copyOfRange(1, tokens.size), client))
        } catch (ex: IllegalArgumentException) {
            event.channel.sendMessageEmbeds(
                with (EmbedBuilder()) {
                    setTitle("Ungültige Eingabe")
                    setDescription(ex.message)
                    setColor(Color.RED)
                    return@with build()
                }
            ).queue()
        }
    }

    private fun tokensOf(input: String) =
        input
            .replace("\\s+", " ")
            .split(" ")
            .toTypedArray()
}

