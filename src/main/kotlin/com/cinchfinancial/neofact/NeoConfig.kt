package com.cinchfinancial.neofact

import org.neo4j.ogm.session.SessionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement



/**
 * Created by mark on 7/5/16.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = arrayOf("com.cinchfinancial.neofact.repository"))
@EnableTransactionManagement
open class NeoConfig {

    @Bean
    open fun sessionConfig() : org.neo4j.ogm.config.Configuration {
        val config = org.neo4j.ogm.config.Configuration()
        config
                .driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .uri = "http://neo4j:password@localhost:7474"
        return config
    }

    @Bean
    open fun getSessionFactory() : SessionFactory {
        return SessionFactory(sessionConfig(), "com.cinchfinancial.neofact.model")
    }

    @Bean
    open fun transactionManager(): Neo4jTransactionManager {
        return Neo4jTransactionManager(getSessionFactory())
    }
}