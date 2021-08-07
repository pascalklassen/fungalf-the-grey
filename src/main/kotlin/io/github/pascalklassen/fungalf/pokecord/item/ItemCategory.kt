package io.github.pascalklassen.fungalf.pokecord.item

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
