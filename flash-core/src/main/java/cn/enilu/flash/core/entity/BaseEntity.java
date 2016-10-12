package cn.enilu.flash.core.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class BaseEntity {

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
