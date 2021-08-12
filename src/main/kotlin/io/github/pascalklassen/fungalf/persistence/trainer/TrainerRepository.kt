package io.github.pascalklassen.fungalf.persistence.trainer

import io.github.pascalklassen.fungalf.persistence.Database
import io.github.pascalklassen.fungalf.persistence.Repository
import io.github.pascalklassen.fungalf.pokecord.Bag
import io.github.pascalklassen.fungalf.pokecord.Pokedollar
import io.github.pascalklassen.fungalf.pokecord.trainer.Snowflake
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import io.github.pascalklassen.fungalf.pokecord.trainer.TrainerId
import io.github.pascalklassen.fungalf.pokecord.trainer.snowflakeOf
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple

// TODO: map correct trainer ids to db table
object TrainerRepository: Repository<Snowflake, Trainer> {

    override fun findByKey(key: Snowflake): Future<Trainer> {
        val promise = Promise.promise<Trainer>()
        Database
            .preparedQuery("SELECT * FROM trainer WHERE id=?")
            .execute(Tuple.of(key.value))
            .onSuccess { promise.complete(mapAsTrainer(it.first())) }
            .onFailure { promise.fail(it) }
        return promise.future()
    }

    override fun save(value: Trainer): Future<Trainer> {
        val promise = Promise.promise<Trainer>()
        Database
            .preparedQuery(
                """
                    INSERT INTO trainer (id, snowflake, pokedollar) VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE id=VALUES(id), snowflake=VALUES(snowflake), pokedollar=VALUES(pokedollar)
                """.trimIndent()
            ).execute(mapAsTuple(value))
            .onSuccess { promise.complete(value) }
            .onFailure { promise.fail(it) }
        return promise.future()
    }

    override fun delete(value: Trainer): Future<Trainer> {
        val promise = Promise.promise<Trainer>()
        Database
            .preparedQuery("DELETE FROM trainer WHERE id=?")
            .execute(Tuple.of(value.id.snowflake))
            .onSuccess { promise.complete(value) }
            .onFailure { promise.fail(it) }
        return promise.future()
    }

    private fun mapAsTuple(trainer: Trainer) =
        Tuple.of(
            trainer.id.value,
            trainer.id.snowflake,
            trainer.pokedollar
        )

    private fun mapAsTrainer(row: Row) =
        Trainer(
            id = TrainerId(
                value = row.getInteger("id"),
                snowflake = snowflakeOf(row.getLong("snowflake"))
            ),
            bag = Bag(),
            pokedollar = Pokedollar(
                amount = row.getInteger("pokedollar")
            )
        )
}
