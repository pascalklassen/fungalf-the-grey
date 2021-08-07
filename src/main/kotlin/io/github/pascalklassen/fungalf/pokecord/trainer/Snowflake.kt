package io.github.pascalklassen.fungalf.pokecord.trainer

data class Snowflake(val value: Long)

fun snowflakeOf(input: Any) =
    when (input) {
        is Long -> Snowflake(input)
        is String -> Snowflake(input.toLong())
        else -> throw IllegalArgumentException("'$input' was no suitable input for Snowflake")
    }
