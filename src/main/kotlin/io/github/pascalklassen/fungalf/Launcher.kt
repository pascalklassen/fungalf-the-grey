package io.github.pascalklassen.fungalf

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.i18n.SupportedLocales
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.pascalklassen.fungalf.extensions.PokeCordExtension
import io.github.pascalklassen.fungalf.extensions.TestExtension
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database

private val LOGGER = KotlinLogging.logger {}

object BotSpec: ConfigSpec("BOT") {
    val token by required<String>(name = "TOKEN")
}

object DatabaseSpec: ConfigSpec("MYSQL") {
    val host by optional(name = "HOST", default = "localhost")
    val port by optional(name = "PORT", default = 3306)
    val database by optional(name = "DATABASE", default = "fungalf_the_grey")
    val username by required<String>(name = "USERNAME")
    val password by required<String>(name = "PASSWORD")

    val maxPoolSize by optional(name = "MAX_POOL_SIZE", default = 5)
}

suspend fun main() {
    val config = Config {
        addSpec(BotSpec)
        addSpec(DatabaseSpec)
    }.from.env()

    val dbHost = config[DatabaseSpec.host]
    val dbPort = config[DatabaseSpec.port]
    val dbName = config[DatabaseSpec.database]
    val dbUsername = config[DatabaseSpec.username]

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://${dbHost}:${dbPort}/${dbName}?serverTimezone=Europe/Berlin"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = dbUsername
        password = config[DatabaseSpec.password]
        maximumPoolSize = config[DatabaseSpec.maxPoolSize]
        initializationFailTimeout = 30 * 1_000
    }

    val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    val bot = ExtensibleBot(config[BotSpec.token]) {
        extensions {
            add(::TestExtension)
            add(::PokeCordExtension)
        }

        i18n {
            defaultLocale = SupportedLocales.GERMAN
        }

        chatCommands {
            defaultPrefix = "?"
            enabled = true
        }

        presence {
            listening("?help")
        }
    }

    bot.start()
}
