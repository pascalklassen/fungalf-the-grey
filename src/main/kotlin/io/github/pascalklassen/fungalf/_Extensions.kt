package io.github.pascalklassen.fungalf

import com.google.common.cache.Cache
import io.vertx.core.Future
import io.vertx.core.Promise
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction

fun Message.removeComponents() = editMessageComponents(listOf()).queue()

fun <T> RestAction<T>.future(): Future<T> {
    val promise = Promise.promise<T>()

    queue({ promise.complete(it) }, { promise.fail(it) })

    return promise.future()
}

operator fun <K: Any, V> Cache<K, V>.contains(key: K) = asMap().containsKey(key)

fun <K: Any, V> Cache<K, V>.get(key: K, other: Future<V>): Future<V> {
    val result = getIfPresent(key)
    val promise = Promise.promise<V>()

    if (result == null) {
        other.onSuccess {
            put(key, it)
            promise.complete(it)
        }
    } else {
        promise.complete(result)
    }

    return promise.future()
}
