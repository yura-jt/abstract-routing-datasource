package com.examine.connector.config;

import com.examine.connector.dao.WorldDaoImpl;
import com.examine.connector.service.GreetingService;
import com.examine.connector.tenant.DbType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultiTenantDatasourceConfiguration {

    @Bean
    public GreetingService greetingService(Environment env) {
        return new GreetingService(new WorldDaoImpl(clientDatasource(env)));
    }

    public DataSource clientDatasource(Environment env) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource clientADatasource = firstDataSource(env);
        DataSource clientBDatasource = secondDataSource(env);
        targetDataSources.put(DbType.FIRST, clientADatasource);
        targetDataSources.put(DbType.SECOND, clientBDatasource);

        MultiTenantDatasourceRouter clientRoutingDatasource
                = new MultiTenantDatasourceRouter();
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        clientRoutingDatasource.setDefaultTargetDataSource(clientADatasource);
        clientRoutingDatasource.afterPropertiesSet();
        return clientRoutingDatasource;
    }

    @Bean("first-db")
    @Primary
    public JdbcTemplate firstJDBCTemplate(@Qualifier("first-datasource") DataSource firstDataSource) {
        return new JdbcTemplate(firstDataSource);
    }

    @Bean("second-db")
    public JdbcTemplate secondJDBCTemplate(@Qualifier("second-datasource") DataSource secondDataSource) {
        return new JdbcTemplate(secondDataSource);
    }

    @Bean("first-datasource")
    @Primary
    public DataSource firstDataSource(Environment env) {
        return buildDataSource(env, "");
    }

    @Bean("second-datasource")
    public DataSource secondDataSource(Environment env) {
        return buildDataSource(env, "second-");
    }

    private DataSource buildDataSource(Environment env, String datasourcePrefix) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring." + datasourcePrefix + "datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring." + datasourcePrefix + "datasource.jdbc-url"));
        dataSource.setUsername(env.getProperty("spring." + datasourcePrefix + "datasource.username"));
        dataSource.setPassword(env.getProperty("spring." + datasourcePrefix + "datasource.password"));

        return dataSource;
    }

    @Bean(name = "txManager1")
    @Autowired
    DataSourceTransactionManager txManager1(@Qualifier("first-datasource") DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }

    @Bean(name = "txManager2")
    @Autowired
    DataSourceTransactionManager txManager2(@Qualifier("second-datasource") DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }

    @Bean(name = "chainedTransactionManager")
    @Primary
    public ChainedTransactionManager transactionManager(
            @Qualifier("txManager1") PlatformTransactionManager fooTx,
            @Qualifier("txManager2") PlatformTransactionManager barTx
    ) {
        return new ChainedTransactionManager(fooTx, barTx);
    }

}
