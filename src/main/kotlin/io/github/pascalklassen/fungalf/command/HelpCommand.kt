package io.github.pascalklassen.fungalf.command

import io.github.pascalklassen.fungalf.PREFIX
import io.github.pascalklassen.fungalf.dsl.message.createMessage
import java.awt.Color
import java.time.Instant

class HelpCommand(private val commandMap: Map<String, Command>): Command(
    name = "help",
    description = "",
    usage = "") {

    override fun execute(context: Context) {
        context.event.channel.sendMessage(
            createMessage {
                embed {
                    for (command in commandMap.values) {
                        +"""
                       > ${command.name}
                       ${command.description}
                       `$PREFIX${command.name} ${command.usage}`
                       
                    """.trimIndent()
                    }
                    color = Color.BLUE
                    timestamp = Instant.now()
                }
            }
        ).queue()
    }
}
