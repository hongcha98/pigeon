package com.hongcha.pigeon.common.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PigeonReference {
    /**
     * 分组
     *
     * @return
     */
    String group() default "default";

    /**
     * 版本号
     *
     * @return
     */
    String version() default "default";

    /**
     * 服务提供者名称
     *
     * @return
     */
    String applicationName();

    /**
     * 负载均衡,默认轮询,全局单例
     */
    String loadBalance() default "polling";
}
