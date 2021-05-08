package com.hongcha.pigeon.spring;


import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.PigeonConfig;
import com.hongcha.pigeon.core.registry.RegistryConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class PigeonConfigAuto implements EnvironmentAware {
    private static final String PREFIX = "pigeon.config.";

    private Environment environment;

//    @Bean
//    @ConfigurationProperties(prefix = PigeonConfigAuto.PREFIX)
//    public PigeonConfig pigeonConfig() {
//        return new PigeonConfig();
//    }
//
//
//    @Bean
//    public PigeonBeanFactoryPostProcessor pigeonBeanFactoryPostProcessor() {
//        return new PigeonBeanFactoryPostProcessor();
//    }
//
//    @Bean
//    public Pigeon pigeon(PigeonConfig pigeonConfig) {
//        return new PigeonSpring(pigeonConfig);
//    }


//    @Bean
//    public PigeonReferenceBeanPostProcessor pigeonReferenceBeanPostProcessor() {
//        return new PigeonReferenceBeanPostProcessor();
//    }
    @Bean
    @ConditionalOnMissingBean
    public PigeonConfig pigeonConfig() {
        return new PigeonConfig(
                environment.getRequiredProperty(PREFIX + "port", Integer.class),
                environment.getRequiredProperty(PREFIX + "packages", String.class).split(","),
                new RegistryConfig(
                        environment.getRequiredProperty(PREFIX + "address", String.class),
                        environment.getRequiredProperty(PREFIX + "username", String.class),
                        environment.getRequiredProperty(PREFIX + "password", String.class)
                )
        );
    }

    @Bean
    public PigeonBeanFactoryPostProcessor pigeonBeanFactoryPostProcessor(PigeonConfig pigeonConfig) {
        return new PigeonBeanFactoryPostProcessor(pigeonConfig);
    }

    @Bean
    public Pigeon pigeon(PigeonConfig pigeonConfig) {
        return new PigeonSpring(pigeonConfig);
    }

    @Bean
    public PigeonReferenceBeanPostProcessor pigeonReferenceBeanPostProcessor(Pigeon pigeon) {
        return new PigeonReferenceBeanPostProcessor(pigeon);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
