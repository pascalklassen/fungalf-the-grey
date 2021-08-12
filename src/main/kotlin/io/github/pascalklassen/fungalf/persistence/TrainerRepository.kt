package io.github.pascalklassen.fungalf.persistence

import io.github.pascalklassen.fungalf.pokecord.trainer.Snowflake
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import io.vertx.core.Future

object TrainerRepository: Repository<Snowflake, Trainer> {

    override fun findByKey(key: Snowflake): Future<Trainer> {
        TODO("Not yet implemented")
    }

    override fun save(value: Trainer): Future<Trainer> {
        TODO("Not yet implemented")
    }

    override fun delete(value: Trainer): Future<Trainer> {
        TODO("Not yet implemented")
    }
}