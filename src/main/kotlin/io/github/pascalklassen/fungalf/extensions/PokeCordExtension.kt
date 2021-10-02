package io.github.pascalklassen.fungalf.extensions

import com.google.common.base.CaseFormat
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatGroupCommand
import com.kotlindiscord.kord.extensions.utils.respond
import com.kotlindiscord.kord.extensions.utils.runSuspended
import dev.kord.rest.builder.message.create.embed
import io.vertx.kotlin.coroutines.await
import mu.KotlinLogging
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import io.github.pascalklassen.pokefuture.pokemon.Pokemon as PokemonData

private val LOGGER = KotlinLogging.logger {}

class PokeCordExtension: Extension() {
    override val name = "pokecord"
    override val bundle = "fungalf.strings"

    override suspend fun setup() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(PokemonMetaTable, PokemonTranslationTable)
        }

        chatGroupCommand {
            name = "extensions.pokecord.commandName"
            aliasKey = "extensions.pokecord.commandAliases"
            description = "extensions.pokecord.commandDescription"

            chatCommand(::ClaimArguments) {
                name = "extensions.pokecord.claim.commandName"
                description = "extensions.pokecord.claim.commandDescription"

                action {
                    val meta = PokemonMeta.findByName(arguments.name.lowercase())

                    with(meta) {
                        message.respond {
                            embed {
                                title = "${name.camelcase()} #${id.value.pad()}"
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

    private fun String.camelcase() = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, this)

    private fun Number.pad(length: Int = 4, char: Char = '0') = toString().padStart(length, char)
}

object PokemonMetaTable: IntIdTable("pokemon_meta") {
    val name = varchar("name", 255)
    val baseExperience = integer("base_experience")
    val weight = integer("weight")
    val height = integer("height")
    val maleSprite = varchar("male_sprite", 255).nullable()
    val femaleSprite = varchar("female_sprite", 255).nullable()
    val artwork = varchar("artwork", 255)

    override val primaryKey = PrimaryKey(id)
}

class PokemonMeta(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<PokemonMeta>(PokemonMetaTable) {

        suspend fun findByName(pokemonName: String) = newSuspendedTransaction {
            val pokeId = PokemonTranslationTable
                .select { PokemonTranslationTable.name eq pokemonName }
                .map { it[PokemonTranslationTable.pokemonId] }
                .map { it.value }
                .firstOrNull()

            pokeId?.let { findById(pokeId) } ?: runSuspended {
                val data = PokemonData.fetch(pokemonName).await()
                val species = data.species.fetch().await()
                val translations = species.names
                    .onEach { LOGGER.info { "Found translation '${it.language.name}': $pokemonName -> ${it.name.lowercase()} " } }

                PokemonMeta.new(data.id) {
                    name = data.name
                    baseExperience = data.baseExperience
                    weight = data.weight
                    height = data.height
                    maleSprite = data.sprites.frontDefault
                    femaleSprite = data.sprites.frontFemale
                    artwork = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"

                    translations.forEach { translation ->
                        PokemonTranslationTable.insert {
                            it[name] = translation.name.lowercase()
                            it[pokemonId] = id
                            it[language] = translation.language.name
                        }
                    }
                }
            }
        }
    }

    var name by PokemonMetaTable.name
    var baseExperience by PokemonMetaTable.baseExperience
    var weight by PokemonMetaTable.weight
    var height by PokemonMetaTable.height
    var maleSprite by PokemonMetaTable.maleSprite
    var femaleSprite by PokemonMetaTable.femaleSprite
    var artwork by PokemonMetaTable.artwork
}

object PokemonTranslationTable: Table("pokemon_translation") {
    val name = varchar("name", 255)
    val pokemonId = reference("pokemon", PokemonMetaTable)
    val language = varchar("language", 255)
    override val primaryKey = PrimaryKey(pokemonId, language)
}

object TrainerTable: LongIdTable("trainer") {
    val timeStarted = timestamp("time_started")
    val timeLastMessage = timestamp("time_last_message")
    val messageStreak = integer("message_streak")
    val messages = integer("messages")
    val experience = integer("experience")
}

enum class Rarity {
    COMMON,
    LEGENDARY
}
