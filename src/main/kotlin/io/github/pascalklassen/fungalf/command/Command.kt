package io.github.pascalklassen.fungalf.command
import io.github.pascalklassen.fungalf.ILLEGAL_ARGUMENTS
import io.github.pascalklassen.fungalf.PREFIX
import io.github.pascalklassen.fungalf.WRONG_CHANNEL_TYPE
import io.github.pascalklassen.fungalf.dsl.message.createMessage
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

abstract class Command(
    val name: String,
    val description: String,
    val usage: String? = "",
    val channelTypes: List<ChannelType> = listOf(ChannelType.TEXT, ChannelType.PRIVATE)) {

    abstract fun execute(context: Context)

    fun fail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }
}

class Context(val event: MessageReceivedEvent, val args: List<String>)

class CommandHandler(commands: List<Command>): ListenerAdapter() {
    private val commandMap = commands.associateByTo(mutableMapOf(), Command::name)
    private val helpCommand = HelpCommand(commandMap)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (!event.message.contentRaw.startsWith(PREFIX)) return

        val tokens = tokensOf(event.message.contentRaw)
        val commandName = tokens.first().lowercase()
        val context = Context(event, tokens.drop(1))

        try {
            val command = commandMap.getOrElse(commandName) { helpCommand }

            if (event.channelType !in command.channelTypes) {
                event.channel.sendMessage(WRONG_CHANNEL_TYPE);
                return;
            }

            command.execute(context)

        } catch (ex: IllegalArgumentException) {
            val message = createMessage {
                embed(ILLEGAL_ARGUMENTS) {
                    +ex.message!!
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
}
