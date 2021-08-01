package io.github.pascalklassen

import mu.KotlinLogging
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

private const val BOT_NAME = "Fungalf the Grey"

private val logger = KotlinLogging.logger {  }

class FungalfBot(args: Array<String>) {

    private val instance = JDABuilder
        .createDefault(args.first())
        .build()

    fun start() {
        logger.info { "$BOT_NAME is starting!" }
    }

    fun stop() {
        logger.info { "$BOT_NAME is shutting down!" }
        instance.shutdown()
    }
}

class Context(val event: MessageReceivedEvent, val args: Array<String>)

abstract class Command(val name: String, val description: String, val usage: String? = "") {
    abstract fun execute(context: Context)
}

class CommandHandler(val commands: List<Command>)