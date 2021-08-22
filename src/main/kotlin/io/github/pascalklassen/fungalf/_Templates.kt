package io.github.pascalklassen.fungalf

import io.github.pascalklassen.fungalf.dsl.message.buildMessage

val TEST_TEMPLATE by lazy {
    buildMessage {
        +"This is a Test!"
    }
}
