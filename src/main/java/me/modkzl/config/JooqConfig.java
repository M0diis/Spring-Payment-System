package me.modkzl.config;

import org.jooq.DSLContext;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public Settings jooqSettings() {
        Settings settings = new Settings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);
        return settings;
    }

    @Bean
    public AbstractDependsOnBeanFactoryPostProcessor jooqDependsOn() {
        return new AbstractDependsOnBeanFactoryPostProcessor(DSLContext.class,
                ListFactoryBean.class, "liquibase") {
        };
    }
}
