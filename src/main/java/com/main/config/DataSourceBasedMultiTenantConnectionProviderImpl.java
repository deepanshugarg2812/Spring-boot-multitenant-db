package com.main.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration("multiDataSourceConnectionProvider")
@Slf4j
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
    private static final String DEFAULT_TENANT_ID = "codingworld";

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, DataSource> map = new HashMap<>();

    boolean init = false;

    @PostConstruct
    public void load() {
        if (map.isEmpty()) {
            map.put("master", applicationContext.getBean("master", HikariDataSource.class));
            map.put("slave", applicationContext.getBean("slave", HikariDataSource.class));
            log.info("Init {}", map);
        }
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return map.get("master");
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        if (!init) {
            init = true;
            map.put("master", applicationContext.getBean("master", HikariDataSource.class));
            map.put("slave", applicationContext.getBean("slave", HikariDataSource.class));
        }
        log.info("Received {} in tenentIdentifier", tenantIdentifier);
        return map.get(tenantIdentifier) != null ? map.get(tenantIdentifier) : map.get("slave");
    }
}
