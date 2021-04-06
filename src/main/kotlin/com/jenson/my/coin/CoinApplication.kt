package com.jenson.my.coin

import io.r2dbc.spi.ConnectionFactory
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class CoinApplication

fun main(args: Array<String>) {
    runApplication<CoinApplication>(*args)
}

@Configuration
class Config {

    @Bean
    fun initialize(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        val populator = CompositeDatabasePopulator()

        populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("./sql/schema.sql")))
        initializer.setDatabasePopulator(populator)
        return initializer
    }
}