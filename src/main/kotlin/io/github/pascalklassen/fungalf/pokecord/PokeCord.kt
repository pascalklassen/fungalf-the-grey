package io.github.pascalklassen.fungalf.pokecord

class Snowflake(val value: Long)

class TrainerId(val value: Int, val snowflake: Snowflake) {
    override fun toString(): String {
        return value.toString().padStart(6, '0')
    }
}

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

    fun getOrCreateTrainerById(id: Snowflake) =
        trainers.getOrPut(id) {
            Trainer(
                id = TrainerId(
                    value = trainers.size + 1,
                    snowflake = id
                ),
                bag = Bag(),
                pokedollar = Pokedollar(
                    amount = 1000
                )
            )
        }
}
