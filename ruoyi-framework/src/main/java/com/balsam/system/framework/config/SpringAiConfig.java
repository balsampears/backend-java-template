package com.balsam.system.framework.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.openai.api-key")
public class SpringAiConfig
{
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder.build();
    }
}
