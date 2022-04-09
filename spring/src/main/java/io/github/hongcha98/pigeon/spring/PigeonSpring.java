package io.github.hongcha98.pigeon.spring;

import io.github.hongcha98.pigeon.core.Pigeon;
import io.github.hongcha98.pigeon.core.PigeonConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


public class PigeonSpring extends Pigeon implements BeanFactoryAware, InitializingBean, DisposableBean {
    private BeanFactory beanFactory;

    public PigeonSpring(PigeonConfig pigeonConfig) {
        super(pigeonConfig);
    }


    @Override
    protected Object serviceHandler(Class<?> cl) throws InstantiationException, IllegalAccessException {
        return beanFactory.getBean(cl);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void destroy() throws Exception {
        super.close();
    }
}
