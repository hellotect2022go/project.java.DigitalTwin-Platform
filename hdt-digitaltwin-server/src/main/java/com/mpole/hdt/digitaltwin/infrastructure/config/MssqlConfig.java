package com.mpole.hdt.digitaltwin.infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
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

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.mpole.hdt.digitaltwin.infrastructure.external.mssql.repository",
        entityManagerFactoryRef = "mssqlEntityManagerFactory",
        transactionManagerRef = "mssqlTransactionManager"
)
public class MssqlConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.mssql")
    public DataSource mssqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mssqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, Object> props = new HashMap<>();
        // @primary 가 아니기 때문에 dialect 를 직접 지정해줘야함
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        //props.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect"); // MSSQL 전용 방언

        return builder
                .dataSource(mssqlDataSource())
                .packages("com.mpole.hdt.digitaltwin.infrastructure.external.mssql.model") // MSSQL 엔티티 위치
                .persistenceUnit("mssql")
                .properties(props)
                .build();
    }

    @Bean
    public PlatformTransactionManager mssqlTransactionManager(
            @Qualifier("mssqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
