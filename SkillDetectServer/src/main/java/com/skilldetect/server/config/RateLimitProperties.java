package com.skilldetect.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rate-limit")
@Component
public class RateLimitProperties {

    private boolean enabled = true;
    private int windowSeconds = 60;
    private int hmiRequestsPerMinute = 120;
    private int m2mRequestsPerMinute = 60;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getWindowSeconds() { return windowSeconds; }
    public void setWindowSeconds(int windowSeconds) { this.windowSeconds = windowSeconds; }
    public int getHmiRequestsPerMinute() { return hmiRequestsPerMinute; }
    public void setHmiRequestsPerMinute(int hmiRequestsPerMinute) { this.hmiRequestsPerMinute = hmiRequestsPerMinute; }
    public int getM2mRequestsPerMinute() { return m2mRequestsPerMinute; }
    public void setM2mRequestsPerMinute(int m2mRequestsPerMinute) { this.m2mRequestsPerMinute = m2mRequestsPerMinute; }
}
