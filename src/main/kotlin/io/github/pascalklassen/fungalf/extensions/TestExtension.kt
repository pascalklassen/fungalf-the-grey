package io.github.pascalklassen.fungalf.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.utils.respond
import mu.KotlinLogging

private val LOGGER = KotlinLogging.logger {}

class TestExtension: Extension() {
    override val name = "test"

    override suspend fun setup() {
        chatCommand {
            name = "slap"
            description = "Get slapped!"

            action {
                message.respond("*slaps you with a large, smelly trout!*")
            }
        }
    }
}
