package io.github.pascalklassen.fungalf.pokecord.trainer

import io.github.pascalklassen.fungalf.persistence.entity.Entity
import io.github.pascalklassen.fungalf.persistence.entity.EntityRetriever
import io.github.pascalklassen.fungalf.persistence.trainer.TrainerRepository
import io.github.pascalklassen.fungalf.pokecord.Bag
import io.github.pascalklassen.fungalf.pokecord.Pokedollar
import io.vertx.core.Future

class Trainer(override val id: Snowflake, val bag: Bag, val pokedollar: Pokedollar): Entity<Snowflake, Trainer> {

    override fun save(): Future<Trainer> =
        TrainerRepository.save(this)

    override fun delete(): Future<Trainer> =
        TrainerRepository.delete(this)

    companion object: EntityRetriever<Snowflake, Trainer> {

        override fun getById(id: Snowflake): Future<Trainer> =
            TrainerRepository.findByKey(id)
    }
}
