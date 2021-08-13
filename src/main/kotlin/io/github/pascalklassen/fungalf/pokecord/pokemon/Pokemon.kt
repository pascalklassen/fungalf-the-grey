package io.github.pascalklassen.fungalf.pokecord.pokemon

import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import java.time.LocalDateTime

class Pokemon(
    val id: PokemonId,
    val trainer: Trainer,
    val xp: Experience,
    val dateCaught: LocalDateTime,
    val moves: MutableList<Move>
)
