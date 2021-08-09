package io.github.pascalklassen.fungalf

import io.vertx.core.Future
import io.vertx.core.Promise
import net.dv8tion.jda.api.requests.RestAction

fun <T> RestAction<T>.future(): Future<T> {
    val promise = Promise.promise<T>()

    queue({ promise.complete(it) }, { promise.fail(it) })

    return promise.future()
}
