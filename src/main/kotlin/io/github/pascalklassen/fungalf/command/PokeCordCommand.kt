package io.github.pascalklassen.fungalf.command

import com.google.common.base.CaseFormat
import io.github.pascalklassen.fungalf.PREFIX
import io.github.pascalklassen.fungalf.TEST_TEMPLATE
import io.github.pascalklassen.fungalf.createMessage
import io.github.pascalklassen.fungalf.persistence.trainer.TrainerRegistry
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import io.github.pascalklassen.fungalf.pokecord.trainer.snowflakeOf
import io.github.pascalklassen.pokefuture.pokemon.Pokemon
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import java.awt.Color
import java.time.Instant

class PokeCordCommand: Command(
    name = "pc",
    description = "Test",
    usage = "[start | claim | help]") {

    private val validStarterPokemon = listOf(
        "bulbasaur",
        "charmander",
        "squirtle",
        "chikorita",
        "cyndaquil",
        "totodile"
    )

    override fun execute(context: Context) {
        require(context.args.isNotEmpty()) { "Siehe `${PREFIX}$name hilfe` für mehr Informationen über diesen Befehl." }

        val newContext = Context(
            context.event,
            context.args.copyOfRange(1, context.args.size),
            context.jda
        )

        when (context.args.first()) {
            "start" -> start(newContext)
            "claim" -> claim(newContext)
            "help" -> help(newContext)
        }
    }

    private fun start(context: Context) {
        val author = context.event.author
        val snowflake = snowflakeOf(author.idLong)

        // TODO: implement correct way to handle if trainer already exists
        // fail silently if user is already a trainer
        if (snowflake in TrainerRegistry) return

        TrainerRegistry.getById(snowflake)
            .onSuccess { sendWelcomeMessage(context, it) }
            .onFailure { TODO("implement error handling for user") }
    }

    private fun sendWelcomeMessage(context: Context, trainer: Trainer) {
        val author = context.event.author
        context.respond(
            with (EmbedBuilder()) {
                setTitle("Willkommen ${author.name}! | Trainer No. ${trainer.id}")
                setColor(Color.RED)
                setDescription(
                    """
                        Willkommen in der Welt von **PokéCord**, ${author.asMention}!
                        
                        > **Dein erstes Pokémon**
                        Um dir einen sicheren Weg in das hohe Grass gewährleisten zu können,
                        hat dir Prof. Eich einen Koffer mit Pokébällen vorbeibringen lassen.
                        
                        In diesem Koffer findest du 6 starter Pokémon:
                        -> __***Bulbasaur***__
                        -> __***Charmander***__
                        -> __***Squirtle***__
                        -> __***Chikorita***__
                        -> __***Cyndaquil***__
                        -> __***Totodile***__
                        
                        Wähle mit `${PREFIX}pc claim [name]` dein erstes Pokémon!
                    """.trimIndent()
                )
                return@with build()
            }
        ).queue()
    }

    private fun help(context: Context) {
        val channel = context.event.channel
        val message = createMessage(TEST_TEMPLATE) {
            actionRow {

                button("accept-terms") {
                    label = "Accept"
                    style = ButtonStyle.SUCCESS
                    onClick = {
                        println("called...")
                        it.reply("You have accepted the terms!")
                            .setEphemeral(true)
                            .queue()
                        it.message
                            ?.editMessageComponents(listOf())
                            ?.queue()
                    }
                }

                button("decline-terms") {
                    label = "Decline"
                    style = ButtonStyle.DANGER
                    onClick = {
                        it.reply("You have declined the terms!")
                            .setEphemeral(true)
                            .queue()
                        it.message
                            ?.editMessageComponents(listOf())
                            ?.queue()
                    }
                }
            }
        }

        channel.sendMessage(message).queue()
    }

    private fun claim(context: Context) {
        require(context.args.size == 1) { "Siehe `${PREFIX}$name help claim` für mehr Informationen über diesen Befehl." }

        val starterName = context.args.first().lowercase()

        if (starterName !in validStarterPokemon) fail("Deine Eingabe war kein gültiges starter Pokémon.")

        Pokemon.fetch(starterName)
            .onSuccess { claimPokemon(it, context) }
            .onFailure { it.printStackTrace() }
    }

    private fun claimPokemon(pokemon: Pokemon, context: Context) {
        context.respond(
            with (EmbedBuilder()) {
                setTitle("#${pokemon.id.toString().padStart(3, '0')} | Level 1 ${pokemon.name.toCamel()}")
                setDescription(
                    """
                        Glückwunsch, du hast dein starter Pokémon erhalten!
                        Um den nächsten Schritt zu machen, kannst du eins der folgenden starter Gebiete
                        besuchen um dein Pokémon zu trainieren.
                        
                        > **Wähle ein starter Gebiet aus**
                        Gib dazu `${PREFIX}$name walk [area]` ein um eins der oben aufgelisteten Gebiete zu betreten.
                    """.trimIndent()
                )
                setColor(Color.RED)
                setThumbnail("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png")
                setFooter("Gefangen am")
                setTimestamp(Instant.now())
                return@with build()
            }
        ).queue()
    }

    private fun String.toCamel() = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, this)
}
