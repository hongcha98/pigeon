package io.github.hongcha98.pigeon.common.service;

import java.io.Serializable;
import java.util.Objects;

public class Service implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 分组
     */
    private String group;
    /**
     * 版本号
     */
    private String version;

    public Service(String serviceName) {
        this(serviceName, "default", "default");
    }

    public Service(String serviceName, String group, String version) {
        this.serviceName = serviceName;
        this.group = group;
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(serviceName, service.serviceName) && Objects.equals(group, service.group) && Objects.equals(version, service.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, group, version);
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
