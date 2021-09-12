package io.github.pascalklassen.fungalf.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatGroupCommand
import com.kotlindiscord.kord.extensions.utils.respond
import com.kotlindiscord.kord.extensions.utils.runSuspended
import dev.kord.rest.builder.message.create.embed
import io.github.pascalklassen.pokefuture.utility.common.APIResource
import io.vertx.kotlin.coroutines.await
import mu.KotlinLogging
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import io.github.pascalklassen.pokefuture.pokemon.Pokemon as PokemonData

private val LOGGER = KotlinLogging.logger {}

class PokeCordExtension: Extension() {
    override val name = "pokecord"
    override val bundle = "fungalf.strings"

    private val validStarter = listOf("bisasam", "glumanda", "shiggy")

    override suspend fun setup() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(PokemonTable, PokemonTranslationTable)
        }

        val list = PokemonData.fetchList(151, 0)
            .compose { APIResource.composeAll(it) }
            .await()

        list.forEach { Pokemon.findByName(it.name) }

        chatGroupCommand {
            name = "extensions.pokecord.commandName"
            aliasKey = "extensions.pokecord.commandAliases"
            description = "extensions.pokecord.commandDescription"

            chatCommand(::ClaimArguments) {
                name = "extensions.pokecord.claim.commandName"
                description = "extensions.pokecord.claim.commandDescription"

                action {
                    val pokemon = Pokemon.findByName(arguments.name.lowercase())

                    with(pokemon) {
                        message.respond {
                            embed {
                                title = "$name #${id}"
                                image = artwork

                                description = """
                                    Gewicht: ${weight / 10f}kg
                                    Höhe:    ${height / 10f}m
                                    
                                    Basis-Erfahrung: $baseExperience-XP
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

object PokemonTable: IntIdTable("pokemon") {
    val name = varchar("name", 255)
    val baseExperience = integer("base_experience")
    val weight = integer("weight")
    val height = integer("height")
    val maleSprite = varchar("male_sprite", 255).nullable()
    val femaleSprite = varchar("female_sprite", 255).nullable()
    val artwork = varchar("artwork", 255)

    override val primaryKey = PrimaryKey(id)
}

class Pokemon(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Pokemon>(PokemonTable) {

        suspend fun findByName(name: String) = newSuspendedTransaction {
            val pokeId = PokemonTranslationTable
                .select { PokemonTranslationTable.name eq name }
                .map { it[PokemonTranslationTable.pokemonId] }
                .map { it.value }
                .firstOrNull()

            pokeId?.let { findById(pokeId) } ?: runSuspended {
                val data = PokemonData.fetch(name).await()
                val species = data.species.fetch().await()
                val translations = species.names
                    .onEach { LOGGER.info { "Found translation '${it.language.name}': $name -> ${it.name.lowercase()} " } }

                Pokemon.new(data.id) {
                    this.name = data.name
                    baseExperience = data.baseExperience
                    weight = data.weight
                    height = data.height
                    maleSprite = data.sprites.frontDefault
                    femaleSprite = data.sprites.frontFemale
                    artwork = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"

                    translations.forEach { translation ->
                        PokemonTranslationTable.insert {
                            it[this.name] = translation.name.lowercase()
                            it[pokemonId] = id
                            it[language] = translation.language.name
                        }
                    }
                }
            }
        }
    }

    var name by PokemonTable.name
    var baseExperience by PokemonTable.baseExperience
    var weight by PokemonTable.weight
    var height by PokemonTable.height
    var maleSprite by PokemonTable.maleSprite
    var femaleSprite by PokemonTable.femaleSprite
    var artwork by PokemonTable.artwork
}

object PokemonTranslationTable: Table("pokemon_translation") {
    val name = varchar("name", 255)
    val pokemonId = reference("pokemon", PokemonTable)
    val language = varchar("language", 255)
    override val primaryKey = PrimaryKey(pokemonId, language)
}

object Trainers: Table() {
    val id = integer("id")
    override val primaryKey = PrimaryKey(id)
}

object PokemonStats: Table() {
    val pokemonId = PokemonTable.id
    val trainerId = Trainers.id
    val experience = integer("experience")
    val rarity = enumeration("rarity", Rarity::class)
    override val primaryKey = PrimaryKey(pokemonId, trainerId)
}

enum class Rarity {
    COMMON,
    LEGENDARY
}
