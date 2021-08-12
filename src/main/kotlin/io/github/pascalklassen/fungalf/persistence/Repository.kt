package io.github.pascalklassen.fungalf.persistence

import io.vertx.core.CompositeFuture
import io.vertx.core.Future

interface Repository<K, V> {

    fun findByKey(key: K): Future<V>

    fun save(value: V): Future<V>

    fun saveAll(values: Collection<V>): CompositeFuture {
        return CompositeFuture.all(values.map { save(it) })
    }

    fun delete(value: V): Future<V>
}