package io.github.pascalklassen.fungalf.pokecord

import kotlin.random.Random

data class Snowflake(val value: Long)

fun snowflakeOf(input: Any) =
    when (input) {
        is Long -> Snowflake(input)
        is String -> Snowflake(input.toLong())
        else -> throw IllegalArgumentException("'$input' was no suitable input for Snowflake")
    }

data class TrainerId(val value: Int, val snowflake: Snowflake) {

    override fun toString() = value.toString().padStart(6, '0')
}

fun randomTrainerId() = Random.nextInt(1_000_000)

sealed class ItemCategory private constructor(val name: String) {
    class BattleItems(): ItemCategory("Battle Items")
    class Berries(): ItemCategory("Berries")
    class GeneralItems(): ItemCategory("General Items")
    class HoldItems(): ItemCategory("Hold Items")
    class Machines(): ItemCategory("Machines")
    class Medicine(): ItemCategory("Medicine")
    class Pokeballs(): ItemCategory("Pokeballs")
    class EventItems(): ItemCategory("Event Items")
}

class Pokedollar(val amount: Int = 0)
class Item(val name: String, val price: Pokedollar = Pokedollar(), val category: ItemCategory)

class Bag(private val content: MutableMap<ItemCategory, MutableList<Item>> = mutableMapOf())

class Trainer(val id: TrainerId, val bag: Bag, val pokedollar: Pokedollar)

class TrainerRepository(trainers: List<Trainer> = listOf()) {
    private val trainers = trainers.associateByTo(mutableMapOf()) { it.id.snowflake }

    fun contains(id: Snowflake) = trainers.containsKey(id)

    fun getOrCreateTrainerById(id: Snowflake) =
        trainers.getOrPut(id) {
            Trainer(
                id = TrainerId(
                    value = randomTrainerId(),
                    snowflake = id
                ),
                bag = Bag(),
                pokedollar = Pokedollar(
                    amount = 1000
                )
            )
        }
}
