package com.flowmind.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flowmind")
public record FlowMindRuntimeProperties(
    String applicationName, Integration integration, Storage storage) {

  public record Integration(boolean redisEnabled, boolean openaiEnabled) {}

  public record Storage(boolean databaseEnabled, boolean flywayEnabled) {}
}
