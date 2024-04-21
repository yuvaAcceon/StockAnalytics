package io.endeavour.stocks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"io.endeavour.stocks.repository.stocks"},
    entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
@EntityScan(basePackages = {"io.endeavour.stocks.entity.stocks"})
public class StocksDBConfig {

    @Autowired
    DataSource dataSource;

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJDBCTemplate(){
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate getNamedParamJDBCTemplate(){
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /***
     * To create an Entity Manager Factory on the Stocks DB, the following steps need to be done:
     * 1) Create an object of a class implementing EntityManagerFactory (EMF)
     * 2) Set the appropriate datasource to be tied to the EMF
     * 3) Set the packages to scan for Entities for this EMF (Stocks entity package structure)
     * 4) Create a Vendor Adapter class that defines who implements JPA spec (Hibernate in our case)
     * 5) Setting the database type to connect to, for the Vendor adapter and set it into the EMF
     * @return EntityManagerFactory
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(){

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("io.endeavour.stocks.entity.stocks");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        emf.setJpaVendorAdapter(vendorAdapter);

        return emf;
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager getTransactionManager(@Qualifier(value = "entityManagerFactory") EntityManagerFactory entityManagerFactory){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        return jpaTransactionManager;
    }





























}
