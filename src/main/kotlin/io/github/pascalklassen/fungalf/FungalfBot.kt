package io.github.pascalklassen.fungalf

import io.github.pascalklassen.fungalf.command.PokeCommand
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

private const val BOT_NAME = "Fungalf the Grey"

private val LOGGER = KotlinLogging.logger {  }

class FungalfBot(args: Array<String>): ListenerAdapter() {

    private val client = JDABuilder
        .createDefault(args.first())
        .build()

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
        if (!event.message.contentRaw.startsWith("?poke")) return

        val tokens = tokensOf(event.message.contentRaw.lowercase())

        PokeCommand().execute(Context(event, tokens.copyOfRange(1, tokens.size), client))
    }

    private fun tokensOf(input: String) =
        input
            .replace("\\s+", " ")
            .split(" ")
            .toTypedArray()
}

class Context(val event: MessageReceivedEvent, val args: Array<String>, val client: JDA) {

    fun respond(message: MessageEmbed) = event.channel.sendMessageEmbeds(message).queue()
    fun respond(message: String) = event.channel.sendMessage(message).queue()
}

abstract class Command(val name: String, val description: String, val usage: String? = "") {
    abstract fun execute(context: Context)
}

class CommandHandler(val commands: List<Command>)