package com.hhf.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

//此对象映射到properties文件中，以mydata开头的信息。
//该文件中命名方式不能是驼峰命名法、须小写
@Component
@ConfigurationProperties(prefix = "mydata")
public class MyMoney {

    private  String name;

    private BigDecimal minMoeny;

    private BigDecimal maxMoney;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinMoeny() {
        return minMoeny;
    }

    public void setMinMoeny(BigDecimal minMoeny) {
        this.minMoeny = minMoeny;
    }

    public BigDecimal getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(BigDecimal maxMoney) {
        this.maxMoney = maxMoney;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
