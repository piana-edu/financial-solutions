package ir.piana.financial.solutions.isoserver.config;

import ir.piana.financial.solutions.isoserver.ChannelType;

public class IsoServerConfig {
    private String host;
    private int port;
    private int maxSessionCount;
    private ChannelType channelType;
    private String isoPackagerQualifiedClassName;
    private String outRequestSubject;
    private String inResponseSubject;

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

    public int getMaxSessionCount() {
        return maxSessionCount;
    }

    public void setMaxSessionCount(int maxSessionCount) {
        this.maxSessionCount = maxSessionCount;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public String getIsoPackagerQualifiedClassName() {
        return isoPackagerQualifiedClassName;
    }

    public void setIsoPackagerQualifiedClassName(String isoPackagerQualifiedClassName) {
        this.isoPackagerQualifiedClassName = isoPackagerQualifiedClassName;
    }

    public String getOutRequestSubject() {
        return outRequestSubject;
    }

    public void setOutRequestSubject(String outRequestSubject) {
        this.outRequestSubject = outRequestSubject;
    }

    public String getInResponseSubject() {
        return inResponseSubject;
    }

    public void setInResponseSubject(String inResponseSubject) {
        this.inResponseSubject = inResponseSubject;
    }
}
