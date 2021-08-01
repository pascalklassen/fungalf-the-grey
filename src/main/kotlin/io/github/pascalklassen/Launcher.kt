package io.github.pascalklassen

import net.dv8tion.jda.api.JDABuilder

fun main(args: Array<String>) {
    val instance = JDABuilder
        .createDefault(args.first())
        .build()
}