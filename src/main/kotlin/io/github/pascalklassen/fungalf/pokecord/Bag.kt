package io.github.pascalklassen.fungalf.pokecord

import io.github.pascalklassen.fungalf.pokecord.item.Item
import io.github.pascalklassen.fungalf.pokecord.item.ItemCategory

typealias ItemList = MutableList<Item>

class Bag(private val content: MutableMap<ItemCategory, ItemList> = mutableMapOf()): Map<ItemCategory, ItemList> by content
