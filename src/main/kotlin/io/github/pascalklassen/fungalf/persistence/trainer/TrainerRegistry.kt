package io.github.pascalklassen.fungalf.persistence.trainer

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.github.pascalklassen.fungalf.extension.contains
import io.github.pascalklassen.fungalf.extension.get
import io.github.pascalklassen.fungalf.persistence.Closable
import io.github.pascalklassen.fungalf.pokecord.trainer.Snowflake
import io.github.pascalklassen.fungalf.pokecord.trainer.Trainer
import io.vertx.core.Future
import java.util.concurrent.TimeUnit

typealias TrainerCache = Cache<Snowflake, Trainer>

object TrainerRegistry: Closable {
    private val cache: TrainerCache = CacheBuilder
        .newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build()

    fun getById(id: Snowflake): Future<Trainer> {
        return cache.get(id, TrainerRepository.findByKey(id))
    }

    fun remove(trainer: Trainer) {
        cache.invalidate(trainer.id)
        TrainerRepository.delete(trainer)
    }

    operator fun contains(id: Snowflake) = id in cache

    override fun close() {
        TrainerRepository.saveAll(cache.asMap().values)
        cache.cleanUp()
    }
}
