package io.github.pascalklassen.fungalf

import io.github.pascalklassen.fungalf.dsl.message.buildEmbed
import io.github.pascalklassen.fungalf.dsl.message.createMessage
import java.awt.Color

val ILLEGAL_ARGUMENTS by lazy {
    buildEmbed {
        title { +"Ungültige Eingabe" }
        color = Color.RED
    }
}

val WRONG_CHANNEL_TYPE by lazy {
    createMessage {
        +"""
            Du kannst diesen Befehl nur in einem der vorgesehenen Channel benutzen!
            Siehe `${PREFIX}hilfe`.
        """.trimIndent()
    }
}
