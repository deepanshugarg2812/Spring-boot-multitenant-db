package com.main.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
public class DataSourceConfig {
    @Autowired
    private JpaProperties jpaProperties;

    @Bean(name = "master")
    @Primary
    @ConfigurationProperties(prefix = "master.datasource")
    public HikariDataSource getHikariDataSourceMaster() {
        log.info("Creating master datasource");
        return new HikariDataSource();
    }

    @Bean(name = "slave")
    @ConfigurationProperties(prefix = "slave.datasource")
    public HikariDataSource getHikariDataSourceSlave() {
        log.info("Creating slave datasource");
        return new HikariDataSource();
    }

    @Bean(name = "currentTenantIdentifierResolver")
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver(){
        return new ConfigCurrentTenantIdentifierResolver();
    }

    @Bean("entityManagerFactory")
    @ConditionalOnBean(name = "multiDataSourceConnectionProvider")
    public LocalContainerEntityManagerFactoryBean todosEntityManagerFactory(
             @Qualifier("multiDataSourceConnectionProvider")
             MultiTenantConnectionProvider multiTenantConnectionProvider,
            @Qualifier("currentTenantIdentifierResolver")
            CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        log.info("Creating entity manager");
        LocalContainerEntityManagerFactoryBean localEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localEntityManagerFactoryBean.setPackagesToScan("com.main");
        Map<String, Object> map = new HashMap<>(jpaProperties.getProperties());
        map.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        map.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        map.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
        map.put(Environment.FORMAT_SQL, true);
        map.put(Environment.SHOW_SQL, true);
        map.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        localEntityManagerFactoryBean.setJpaPropertyMap(map);
        localEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        log.info("Created entity manager");
        return localEntityManagerFactoryBean;
    }

    @Bean(name = "transactionManager")
    @DependsOn("entityManagerFactory")
    public JpaTransactionManager todosTransactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        System.out.println(entityManagerFactory);
        JpaTransactionManager jpaTransactionManager =
                new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

}
