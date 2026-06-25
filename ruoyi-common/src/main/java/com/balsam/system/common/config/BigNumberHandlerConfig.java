package com.balsam.system.common.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;

/**
 * @DESCRIPTION:
 * @USER: liuyanbin
 * @DATE: 2024/6/28 0028 10:33
 */
@Configuration
public class BigNumberHandlerConfig {

    /**
     * 解决 long 类型返回前端丢失精度的问题。
     * 使用 JsonMapperBuilderCustomizer 而非自定义 MappingJackson2HttpMessageConverter，
     * 避免破坏默认 HttpMessageConverter 顺序导致 /v3/api-docs 返回 base64 编码。
     */
    @Bean
    public JsonMapperBuilderCustomizer bigNumberJsonCustomizer() {
        return builder -> {
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            simpleModule.addSerializer(Float.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Float.TYPE, ToStringSerializer.instance);
            simpleModule.addSerializer(Double.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Double.TYPE, ToStringSerializer.instance);
            simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            builder.addModule(simpleModule);
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }
}