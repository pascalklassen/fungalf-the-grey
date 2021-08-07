package io.github.pascalklassen.fungalf.pokecord.trainer

import kotlin.random.Random

data class TrainerId(val value: Int, val snowflake: Snowflake) {

    override fun toString() = value.toString().padStart(6, '0')

    companion object {
        fun random() = Random.nextInt(1_000_000)
    }
}
