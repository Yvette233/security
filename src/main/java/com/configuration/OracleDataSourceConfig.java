package com.configuration;


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
@EnableJpaRepositories(entityManagerFactoryRef = "oracleEntityManagerFactoryBean",
        transactionManagerRef = "oracleTransactionManager",
        basePackages = {"com.dao"})
public class OracleDataSourceConfig {



    private DataSource oracleDataSource;

    @Primary
    @Bean(name = "oracleDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSource oracleDataSource() {
        this.oracleDataSource = DataSourceBuilder.create().build();
        return this.oracleDataSource;
    }

    //    @Primary
    @Bean("oracleEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(oracleDataSource).packages(new String[]{"com.pojo"}).properties(hibernateProperties()).build();
    }

    //    @Primary
    @Bean("oracleTransactionManager")
    public PlatformTransactionManager oracleTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(oracleEntityManagerFactoryBean(builder).getObject());
    }

    private final static Map<String, Object> hibernateProperties() {
        Map<String, Object> hibernateProperties = new HashMap<String, Object>();
//        hibernateProperties.setProperty(
//                "hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.put(
                "hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
        hibernateProperties.put("hibernate.connection.autocommit", "true");
        hibernateProperties.put("hibernate.format_sql", "false");
        hibernateProperties.put("hibernate.show_sql", "false");
        //下面这个已经在application.properties钟配置了，两者均可成功解决javax.persistence.TransactionRequiredException: no transaction is in progress这个问题。
        hibernateProperties.put("hibernate.allow_update_outside_transaction", "true");
        return hibernateProperties;
    }
}
