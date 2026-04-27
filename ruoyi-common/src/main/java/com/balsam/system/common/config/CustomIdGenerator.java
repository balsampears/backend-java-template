package com.balsam.system.common.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.balsam.system.common.utils.SnowflakeIdWorkerUtil;

/**
 * 自定义id生成器
 */
public class CustomIdGenerator implements IdentifierGenerator {

    private final SnowflakeIdWorkerUtil snowflakeIdWorkerUtil = new SnowflakeIdWorkerUtil();

    @Override
    public Long nextId(Object entity) {
        return snowflakeIdWorkerUtil.nextId();
    }
}
