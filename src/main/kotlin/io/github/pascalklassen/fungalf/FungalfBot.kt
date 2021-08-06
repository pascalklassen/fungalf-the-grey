package io.github.pascalklassen.fungalf

import io.github.pascalklassen.fungalf.command.Context
import io.github.pascalklassen.fungalf.command.PokeCordCommand
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

const val BOT_NAME = "Fungalf the Grey"
const val PREFIX = "?"

private val LOGGER = KotlinLogging.logger {}

class FungalfBot(args: Array<String>): ListenerAdapter() {

    private val botToken = args.first()
    private val dbUserName = args[1]
    private val dbPassword = args[2]

    private val jda = JDABuilder
        .createDefault(botToken)
        .build()

    private val dbClient = MySQLPool.pool(
        mySQLConnectOptionsOf(
            host = "localhost",
            port = 3306,
            database = "fungalf_the_grey",
            user = dbUserName,
            password = dbPassword
        ),
        poolOptionsOf(
            maxSize = 5
        )
    )

    private val command = PokeCordCommand()

    fun start() {
        LOGGER.info { "$BOT_NAME is starting!" }
        jda.addEventListener(this)
        dbClient
            .query("SELECT * FROM TABLES;")
            .execute {
                if (it.succeeded()) {
                    LOGGER.info { it.result().forEach { row -> row.toJson() } }
                } else {
                    LOGGER.error { it.cause().message }
                }
            }
    }

    fun stop() {
        LOGGER.info { "$BOT_NAME is shutting down!" }
        jda.shutdown()
        dbClient.close()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.channel.type != ChannelType.TEXT) return
        if (!event.message.contentRaw.startsWith(PREFIX)) return

        val tokens = tokensOf(event.message.contentRaw.lowercase())

        try {
            command.execute(Context(event, tokens.copyOfRange(1, tokens.size), jda))
        } catch (ex: IllegalArgumentException) {
            event.channel.sendMessageEmbeds(
                with (EmbedBuilder()) {
                    setTitle("Ungültige Eingabe")
                    setDescription(ex.message)
                    setColor(Color.RED)
                    return@with build()
                }
            ).queue()
        }
    }

    private fun tokensOf(input: String) =
        input
            .replace("\\s+", " ")
            .split(" ")
            .toTypedArray()
}

