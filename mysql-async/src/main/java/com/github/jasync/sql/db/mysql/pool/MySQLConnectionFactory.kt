package com.github.jasync.sql.db.mysql.pool

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.pool.ConnectionFactory
import mu.KotlinLogging
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}

/**
 *
 * Connection pool factory for <<com.github.mauricio.sql.db.mysql.MySQLConnection>> objects.
 *
 * @param configuration a valid configuration to connect to a MySQL server.
 *
 */

open class MySQLConnectionFactory(val configuration: Configuration) : ConnectionFactory<MySQLConnection>() {
    /**
     *
     * Creates a valid object to be used in the pool. This method can block if necessary to make sure a correctly built
     * is created.
     *
     * @return
     */
    override fun create(): CompletableFuture<MySQLConnection> {
        return configuration.resolveCredentials()
            .thenCompose { credentials ->
                val completeConfiguration = configuration.copy(username = credentials.username, password = credentials.password)

                logger.debug {
                    "Creating MySQL connection with configuration $completeConfiguration"
                }
                val connection = MySQLConnection(completeConfiguration)
                connection.connect()
            }
            .toCompletableFuture()
    }
}
