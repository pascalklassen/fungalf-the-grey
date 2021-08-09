package io.github.pascalklassen.fungalf.pokecord.pokemon

import io.github.pascalklassen.fungalf.pokecord.trainer.TrainerId
import java.time.LocalDateTime

class Pokemon(
    val id: PokemonId,
    val trainer: TrainerId,
    val xp: Experience,
    val dateCaught: LocalDateTime,
    val moves: MutableList<Move>
)
