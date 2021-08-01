package io.github.pascalklassen

fun main(args: Array<String>) {
    FungalfBot(args).also {
        it.start()
        Runtime.getRuntime().addShutdownHook(Thread(it::stop))
    }
}
