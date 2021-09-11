package io.github.pascalklassen.fungalf.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatGroupCommand
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.rest.builder.message.create.embed
import io.vertx.kotlin.coroutines.await
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import io.github.pascalklassen.pokefuture.pokemon.Pokemon as PokemonData

private val LOGGER = KotlinLogging.logger {}

class PokeCordExtension: Extension() {
    override val name = "pokecord"
    override val bundle = "fungalf.strings"

    private val validStarter = listOf("bisasam", "glumanda", "shiggy")

    override suspend fun setup() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Pokemon)
        }

        chatGroupCommand {
            name = "extensions.pokecord.commandName"
            aliasKey = "extensions.pokecord.commandAliases"
            description = "extensions.pokecord.commandDescription"

            chatCommand(::ClaimArguments) {
                name = "extensions.pokecord.claim.commandName"
                description = "extensions.pokecord.claim.commandDescription"

                action {
                    val pokemonName = arguments.name.lowercase()

                    var result = transaction { Pokemon.select { Pokemon.name eq pokemonName }.singleOrNull() }

                    if (result == null) {
                        LOGGER.debug { "Could not find Pokémon '$pokemonName' in Database!" }

                        try {
                            val data = PokemonData.fetch(pokemonName).await()

                            result = transaction {
                                val id = Pokemon.insert {
                                    it[id] = data.id
                                    it[name] = data.name
                                    it[baseExperience] = data.baseExperience
                                    it[weight] = data.weight
                                    it[height] = data.height
                                    it[maleSprite] = data.sprites.frontDefault
                                    it[femaleSprite] = data.sprites.frontFemale
                                    it[artwork] = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${data.id}.png"
                                } get Pokemon.id

                                Pokemon.select { Pokemon.id eq id }.single()
                            }
                        } catch (ex: Exception) {
                            LOGGER.error { "Something went wrong while retrieving Pokémon Data for '$pokemonName'." }
                        }
                    }

                    result?.let {
                        message.respond {
                            embed {
                                title = "${it[Pokemon.name]} #${it[Pokemon.id]}"
                                image = it[Pokemon.artwork]

                                description = """
                                    Gewicht: ${it[Pokemon.weight] / 10}kg
                                    Höhe:    ${it[Pokemon.height] / 10}m
                                    
                                    Basis-Erfahrung: ${it[Pokemon.baseExperience]}-XP
                                """.trimIndent()
                            }
                        }
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
    val maleSprite = varchar("male_sprite", 255).nullable()
    val femaleSprite = varchar("female_sprite", 255).nullable()
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
