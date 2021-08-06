package io.github.pascalklassen.fungalf.command
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Context(val event: MessageReceivedEvent, val args: Array<String>, val jda: JDA) {

    fun respond(message: MessageEmbed) =
        event
            .channel
            .sendMessageEmbeds(message)

    fun respond(message: String) =
        event
            .channel
            .sendMessage(message)
}

abstract class Command(val name: String, val description: String, val usage: String? = "") {
    abstract fun execute(context: Context)

    fun fail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }
}

class CommandHandler(val commands: List<Command>)
