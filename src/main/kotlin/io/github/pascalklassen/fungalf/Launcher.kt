package io.github.pascalklassen.fungalf

fun main(args: Array<String>) {
    FungalfBot(args).also {
        it.start()
        Runtime
            .getRuntime()
            .addShutdownHook(Thread(it::stop))
    }
}
