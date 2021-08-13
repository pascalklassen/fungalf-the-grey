package io.github.pascalklassen.fungalf.pokecord

class Pokedollar(var amount: Int = 0) {

    operator fun plus(amount: Int) {
        this.amount += amount
    }

    operator fun minus(amount: Int) {
        val result = this.amount - amount
        if (result < 0) {
            this.amount = 0
        } else {
            this.amount = result
        }
    }
}
