package com.hongcha.pigeon.spring;


import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.PigeonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PigeonConfigAuto {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(PigeonSpringConstant.PREFIX)
    public PigeonConfig pigeonConfig() {
        return new PigeonConfig();
    }

    @Bean
    public PigeonBeanFactoryPostProcessor pigeonBeanFactoryPostProcessor() {
        return new PigeonBeanFactoryPostProcessor();
    }

    @Bean
    public Pigeon pigeon(PigeonConfig pigeonConfig) {
        return new PigeonSpring(pigeonConfig);
    }

    @Bean
    public PigeonReferenceBeanPostProcessor pigeonReferenceBeanPostProcessor(Pigeon pigeon) {
        return new PigeonReferenceBeanPostProcessor(pigeon);
    }


}
