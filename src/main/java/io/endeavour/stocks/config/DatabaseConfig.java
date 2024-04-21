package io.endeavour.stocks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    /**
     * Method to create a datasource representing StocksDB
     * The prefix spring.datasource will tell Spring to pickup 4 corresponding parameters from the applications.properties file
     * @return DataSource
     */
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource getDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * Method to create a datasource representing CrudDB
     * The prefix spring.datasource will tell Spring to pickup 4 corresponding parameters from the applications.properties file
     * @return DataSource
     */
    @Bean(name = "dataSourceCrud")
    @ConfigurationProperties(prefix = "spring.datasource-crudjpa")
    public DataSource getDataSourceCrud(){
        return DataSourceBuilder.create().build();
    }
}
