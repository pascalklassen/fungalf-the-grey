package io.github.pascalklassen.fungalf

fun main() {
    FungalfBot().also {
        it.start()
        Runtime
            .getRuntime()
            .addShutdownHook(Thread(it::stop))
    }
}
