package io.github.pascalklassen.fungalf.persistence

import io.github.pascalklassen.fungalf.pokecord.*
import io.github.pascalklassen.fungalf.pokecord.trainer.Snowflake
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import io.github.pascalklassen.fungalf.pokecord.trainer.TrainerId

object TrainerRegistry {
    private val trainers = mutableMapOf<Snowflake, Trainer>()

    operator fun contains(id: Snowflake) = trainers.containsKey(id)

    fun getOrCreateTrainerById(id: Snowflake) =
        trainers.getOrPut(id) {
            Trainer(
                id = TrainerId(
                    value = TrainerId.random(),
                    snowflake = id
                ),
                bag = Bag(),
                pokedollar = Pokedollar(
                    amount = 1000
                )
            )
        }
}
