package io.github.pascalklassen.fungalf.command

import com.google.common.base.CaseFormat
import io.github.pascalklassen.fungalf.Command
import io.github.pascalklassen.fungalf.Context
import io.github.pascalklassen.pokefuture.pokemon.Pokemon
import net.dv8tion.jda.api.EmbedBuilder

class PokeCommand: Command(
    name = "poke",
    description = "Test",
    usage = "[name]") {

    override fun execute(context: Context) {
        val name = context.args.first()

        Pokemon.fetch(name)
            .onSuccess { embedPokemon(it, context) }
            .onFailure { fail(context) }
    }

    private fun embedPokemon(pokemon: Pokemon, context: Context) {
        val toCamel: String.() -> String = { CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, this) }

        val embed = EmbedBuilder()
            .setTitle(pokemon.name.toCamel())
            .setThumbnail(pokemon.sprites.frontDefault)
            .setDescription(
                """
                    Größe: ${pokemon.height / 10f}m
                    Gewicht: ${pokemon.weight / 10f}kg
                    Typ: ${pokemon.types.joinToString(", ") { it.type.name.toCamel() }}
                    Moves: ${pokemon.moves.joinToString(", ") { it.move.name.toCamel() }}
                """.trimIndent()
            )

        context.respond(embed.build())
    }

    private fun fail(context: Context) =
        context.respond("Pokemon nicht gefunden!")
}