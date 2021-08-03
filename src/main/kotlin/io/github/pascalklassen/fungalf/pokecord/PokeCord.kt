package io.github.pascalklassen.fungalf.pokecord

data class Snowflake(val value: Long) {

    companion object {
        fun of(input: Any) =
            when (input) {
                is Long -> Snowflake(input)
                is String -> Snowflake(input.toLongOrNull() ?: -1)
                else -> null
            }
    }
}

class TrainerId(val value: Int, val snowflake: Snowflake)

class ItemCategory(val name: String)
class Item()

class Bag(val content: Map<ItemCategory, List<Item>>)

class Trainer(val id: TrainerId, val bag: Bag)
