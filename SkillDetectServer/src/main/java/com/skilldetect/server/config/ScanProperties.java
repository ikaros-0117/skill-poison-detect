package com.skilldetect.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scan")
public class ScanProperties {

    private int riskThreshold = 50;
    private int retentionDays = 30;
    private final Concurrency concurrency = new Concurrency();
    private final Engine engine = new Engine();
    private final Storage storage = new Storage();

    public int getRiskThreshold() { return riskThreshold; }
    public void setRiskThreshold(int riskThreshold) { this.riskThreshold = riskThreshold; }

    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }

    public Concurrency getConcurrency() { return concurrency; }
    public Engine getEngine() { return engine; }
    public Storage getStorage() { return storage; }

    public static class Concurrency {
        private int maxActive = 8;
        public int getMaxActive() { return maxActive; }
        public void setMaxActive(int maxActive) { this.maxActive = maxActive; }
    }

    public static class Engine {
        private String baseUrl = "http://localhost:8000";
        private int timeoutSeconds = 720;
        private int circuitFailureThreshold = 5;
        private int circuitOpenSeconds = 60;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        public int getCircuitFailureThreshold() { return circuitFailureThreshold; }
        public void setCircuitFailureThreshold(int circuitFailureThreshold) { this.circuitFailureThreshold = circuitFailureThreshold; }
        public int getCircuitOpenSeconds() { return circuitOpenSeconds; }
        public void setCircuitOpenSeconds(int circuitOpenSeconds) { this.circuitOpenSeconds = circuitOpenSeconds; }
    }

    public static class Storage {
        private String baseDir = "/data";
        public String getBaseDir() { return baseDir; }
        public void setBaseDir(String baseDir) { this.baseDir = baseDir; }
    }
}
