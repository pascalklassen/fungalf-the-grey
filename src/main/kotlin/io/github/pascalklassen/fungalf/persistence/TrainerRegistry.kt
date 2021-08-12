package io.github.pascalklassen.fungalf.persistence

import com.google.common.cache.Cache
import io.github.pascalklassen.fungalf.pokecord.trainer.Snowflake
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer

typealias TrainerCache = Cache<Snowflake, Trainer>

object TrainerRegistry: Closable {

    override fun close() {
        TODO()
    }
}
