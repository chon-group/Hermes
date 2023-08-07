package jason.hermes.config;

import jason.hermes.sec.CommunicationSecurity;

public abstract class Configuration {

    private String connectionIdentifier;

    private boolean connected = false;

    private CommunicationSecurity security;


    public String getConnectionIdentifier() {
        return connectionIdentifier;
    }

    public void setConnectionIdentifier(String connectionIdentifier) {
        this.connectionIdentifier = connectionIdentifier;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public CommunicationSecurity getSecurity() {
        return security;
    }

    public void setSecurity(CommunicationSecurity security) {
        this.security = security;
    }
}
