package com.hongcha.pigeon.spring;

import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.service.annotations.PigeonReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;


public class PigeonReferenceBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
    @Autowired
    Pigeon pigeon;

    public PigeonReferenceBeanPostProcessor(Pigeon pigeon) {
        this.pigeon = pigeon;
    }


    private List<Field> getAllField(Class<?> beanClass) {
        List<Field> fieldList = new LinkedList<>();
        while (!beanClass.equals(Object.class)) {
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                fieldList.add(declaredField);
            }
            beanClass = beanClass.getSuperclass();
        }
        return fieldList;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        List<Field> list = getAllField(bean.getClass());
        for (Field field : list) {
            PigeonReference annotation = field.getAnnotation(PigeonReference.class);
            if (annotation != null) {
                field.setAccessible(true);
                String group = annotation.group();
                String version = annotation.version();
                try {
                    field.set(bean, pigeon.getProxy(field.getType(), group, version));
                } catch (IllegalAccessException e) {
                    throw new BeanInitializationException("PigeonReference Error", e);
                }
            }
        }
        return bean;
    }


}
