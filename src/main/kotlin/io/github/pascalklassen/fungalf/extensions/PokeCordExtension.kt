package io.github.pascalklassen.fungalf.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatGroupCommand
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

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
                    val name = arguments.name.lowercase()

                    transaction {
                        val pokemon = Pokemon.select { Pokemon.name eq name }.first()
                    }
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

object Pokemon: Table() {
    val id = integer("id")
    val name = varchar("name", 255)
    val baseExperience = integer("base_experience")
    val weight = integer("weight")
    val height = integer("height")
    val maleSprite = varchar("male_sprite", 255)
    val femaleSprite = varchar("female_sprite", 255)
    val artwork = varchar("artwork", 255)

    override val primaryKey = PrimaryKey(id)
}

object Trainers: Table() {
    val id = integer("id")
    override val primaryKey = PrimaryKey(id)
}

object PokemonStats: Table() {
    val pokemonId = Pokemon.id
    val trainerId = Trainers.id
    val experience = integer("experience")
    val rarity = enumeration("rarity", Rarity::class)
    override val primaryKey = PrimaryKey(pokemonId, trainerId)
}

enum class Rarity {
    COMMON,
    LEGENDARY
}
