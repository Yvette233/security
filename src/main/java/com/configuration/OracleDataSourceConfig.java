package com.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "oracleEntityManagerFactoryBean",
        transactionManagerRef = "oracleTransactionManager",
        basePackages = {"com.dao"}
)
public class OracleDataSourceConfig {

    @Primary
    @Bean(name = "oracleDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSource oracleDataSource() {
        // 直接返回构建的数据源，不要赋值给类的全局变量
        return DataSourceBuilder.create().build();
    }

    @Primary // 必须加上，作为默认的实体管理器
    @Bean(name = "oracleEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier("oracleDataSource") DataSource dataSource) { // 必须通过参数让 Spring 自动注入数据源！

        return builder
                .dataSource(dataSource)
                .packages("com.pojo")
                .properties(hibernateProperties())
                .build();
    }

    @Primary // 必须加上
    @Bean(name = "oracleTransactionManager")
    public PlatformTransactionManager oracleTransactionManager(
            @org.springframework.beans.factory.annotation.Qualifier("oracleEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    private static Map<String, Object> hibernateProperties() {
        Map<String, Object> hibernateProperties = new HashMap<>();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        hibernateProperties.put("hibernate.connection.autocommit", "false");
        hibernateProperties.put("hibernate.format_sql", "false");
        hibernateProperties.put("hibernate.show_sql", "false");
        hibernateProperties.put("hibernate.allow_update_outside_transaction", "true");
        return hibernateProperties;
    }
}