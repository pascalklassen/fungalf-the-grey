package io.github.pascalklassen.fungalf.handler

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import io.vertx.core.AsyncResult
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.*
import mu.KotlinLogging
import java.util.function.Function

private val LOGGER = KotlinLogging.logger {}

object MySQLHandler: ConfigSpec("MYSQL"), MySQLPool {

    private val host by optional(name = "HOST", default = "localhost")
    private val port by optional(name = "PORT", default = 3306)
    private val database by optional(name = "DATABASE", default = "fungalf_the_grey")
    private val username by required<String>(name = "USERNAME")
    private val password by required<String>(name = "PASSWORD")

    private val maxPoolSize by optional(5)

    private val config = Config { addSpec(this@MySQLHandler) }.from.env()

    private val pool = MySQLPool.pool(
        mySQLConnectOptionsOf(
            host = config[host],
            port = config[port],
            database = config[database],
            user = config[username],
            password = config[password]
        ),
        poolOptionsOf(
            maxSize = config[maxPoolSize]
        )
    )

    init {
        LOGGER.info { "Connecting to database '${config[database]}' on ${config[host]}:${config[port]} with user '${config[username]}'." }
    }

    override fun query(sql: String?): Query<RowSet<Row>> =
        pool.query(sql)

    override fun preparedQuery(sql: String?): PreparedQuery<RowSet<Row>> =
        pool.preparedQuery(sql)

    override fun close(handler: Handler<AsyncResult<Void>>?) =
        pool.close(handler)

    override fun close(): Future<Void> = pool.close()

    override fun getConnection(handler: Handler<AsyncResult<SqlConnection>>?) =
        pool.getConnection(handler)

    override fun getConnection(): Future<SqlConnection> = pool.connection

    override fun connectHandler(handler: Handler<SqlConnection>?): MySQLPool =
        pool.connectHandler(handler)

    override fun connectionProvider(provider: Function<Context, Future<SqlConnection>>?): MySQLPool =
        pool.connectionProvider(provider)

    override fun size() = pool.size()
}
