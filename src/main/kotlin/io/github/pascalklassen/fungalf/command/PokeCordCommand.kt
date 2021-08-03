package io.github.pascalklassen.fungalf.command

import io.github.pascalklassen.fungalf.Command
import io.github.pascalklassen.fungalf.Context
import io.github.pascalklassen.fungalf.pokecord.Snowflake
import io.github.pascalklassen.fungalf.pokecord.TrainerRepository
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class PokeCordCommand: Command(
    name = "pc",
    description = "Test",
    usage = "[name]") {

    private val trainers = TrainerRepository()

    override fun execute(context: Context) {
        if (context.args.first() != "start") return

        val author = context.event.author
        val trainer = trainers.getOrCreateTrainerById(Snowflake(author.idLong))

        context.respond(
            with (EmbedBuilder()) {
                setTitle("Trainer ${author.name} | ID No. ${trainer.id}")
                setDescription(
                    """
                        Welcome to the World of PokéCord ${author.asMention}!
                        
                        > **Select your first Starter Pokémon**
                        Type **?pc start** to start your Journey! 
                    """.trimIndent()
                )
                setColor(Color.RED)
                return@with build()
            }
        )
    }
}