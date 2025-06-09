package ir.piana.financial.solutions.serverclientraw.config;

public class ServerSocketConfig {
    private String host;
    private int port;
    private int backlog;
    private String destinationSubject;
    private boolean bigEndian;
    private int readTimeoutMillis;

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

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public String getDestinationSubject() {
        return destinationSubject;
    }

    public void setDestinationSubject(String destinationSubject) {
        this.destinationSubject = destinationSubject;
    }

    public boolean isBigEndian() {
        return bigEndian;
    }

    public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }
}
