package io.endeavour.stocks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"io.endeavour.stocks.repository.crud"},
    entityManagerFactoryRef = "entityManagerFactoryCrud", transactionManagerRef = "transactionManagerCrud")
@EntityScan(basePackages = {"io.endeavour.stocks.entity.crud"})
public class CrudDBConfig {
    @Autowired
    DataSource dataSourceCrud;

    @Bean(name = "entityManagerFactoryCrud")
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryCrud(){
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSourceCrud);
        emf.setPackagesToScan("io.endeavour.stocks.entity.crud");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        emf.setJpaVendorAdapter(vendorAdapter);

        return emf;
    }

    @Bean(name = "transactionManagerCrud")
    public JpaTransactionManager getTransactionManagerCrud(@Qualifier(value = "entityManagerFactoryCrud") EntityManagerFactory entityManagerFactory){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        return jpaTransactionManager;
    }



















}
