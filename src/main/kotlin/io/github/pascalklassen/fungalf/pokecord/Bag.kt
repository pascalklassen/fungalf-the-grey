package io.github.pascalklassen.fungalf.pokecord

import io.github.pascalklassen.fungalf.pokecord.item.Item
import io.github.pascalklassen.fungalf.pokecord.item.ItemCategory

class Bag(private val content: MutableMap<ItemCategory, MutableList<Item>> = mutableMapOf())
