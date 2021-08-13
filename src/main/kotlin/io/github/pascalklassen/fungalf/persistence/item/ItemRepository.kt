package io.github.pascalklassen.fungalf.persistence.item

import io.github.pascalklassen.fungalf.persistence.Database
import io.github.pascalklassen.fungalf.persistence.Repository
import io.github.pascalklassen.fungalf.pokecord.item.Item
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.sqlclient.Tuple

class ItemRepository: Repository<Int, Item> {

    fun findFor(trainer: Trainer): Future<List<Item>> {
        val promise = Promise.promise<List<Item>>()
        Database
            .preparedQuery(
                """
                    
                """.trimIndent()
            )
            .execute(Tuple.of(trainer.id.value))
        return promise.future()
    }

    override fun findByKey(key: Int): Future<Item> {
        val promise = Promise.promise<Item>()
        return promise.future()
    }

    override fun save(value: Item): Future<Item> {
        val promise = Promise.promise<Item>()
        return promise.future()
    }

    override fun delete(value: Item): Future<Item> {
        val promise = Promise.promise<Item>()
        return promise.future()
    }
}
