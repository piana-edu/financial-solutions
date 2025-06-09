package ir.piana.financial.solutions.common.config;

import java.util.List;

public class NatsConfig {
    private String host;
    private int port;
    private List<JetStreamConfig> jetStreams;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<JetStreamConfig> getJetStreams() {
        return jetStreams;
    }

    public void setJetStreams(List<JetStreamConfig> jetStreams) {
        this.jetStreams = jetStreams;
    }
}
