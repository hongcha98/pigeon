package com.hongcha.pigeon.spring;

import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.PigeonConfig;
import com.hongcha.pigeon.core.error.PigeonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class PigeonSpring extends Pigeon implements BeanFactoryAware {
    private static final Logger LOG = LoggerFactory.getLogger(PigeonSpring.class);

    private BeanFactory beanFactory;

    public PigeonSpring(PigeonConfig pigeonConfig) {
        super(pigeonConfig);
    }

    @PostConstruct
    public void init() {
        LOG.info("pigeon start");
        try {
            super.start();
        } catch (Exception e) {
            throw new PigeonException("pigeon start error", e);
        }

        LOG.info("pigeon complete");
    }

    @PreDestroy
    public void destroy() {
        super.close();
    }


    @Override
    protected Object serviceHandler(Class<?> cl) throws InstantiationException, IllegalAccessException {
        return beanFactory.getBean(cl);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
