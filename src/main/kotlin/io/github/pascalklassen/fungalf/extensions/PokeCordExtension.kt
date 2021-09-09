package io.github.pascalklassen.fungalf.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatGroupCommand
import com.kotlindiscord.kord.extensions.utils.respond

class PokeCordExtension: Extension() {
    override val name = "pokecord"
    override val bundle = "fungalf.strings"

    private val validStarter = listOf("bisasam", "glumanda", "shiggy")

    override suspend fun setup() {
        chatGroupCommand {
            name = "extensions.pokecord.commandName"
            aliasKey = "extensions.pokecord.commandAliases"
            description = "extensions.pokecord.commandDescription"

            chatCommand(::ClaimArguments) {
                name = "extensions.pokecord.claim.commandName"
                description = "extensions.pokecord.claim.commandDescription"

                action {
                    if (arguments.name.lowercase() !in validStarter) {
                        message.respond {
                            content = "Bitte wähle ein gültiges Starter-Pokémon aus der Liste aus:\n"
                            validStarter.forEach { content += "**» ${it}**\n" }
                        }
                        return@action
                    }

                    message.respond("Du hast ein **${arguments.name}** gefangen!")
                }
            }
        }
    }

    inner class ClaimArguments: Arguments() {
        val name by string(
            "pokemon-name",
            "extensions.pokecord.claim.commandArguments.pokemonName"
        )
    }
}
