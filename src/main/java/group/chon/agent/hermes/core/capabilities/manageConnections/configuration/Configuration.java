package group.chon.agent.hermes.core.capabilities.manageConnections.configuration;

import jason.asSyntax.Term;
import group.chon.agent.hermes.core.capabilities.manageConnections.sec.CommunicationSecurity;

import java.util.Observable;

public abstract class Configuration extends Observable implements Cloneable, ConfigurationAsBelief {

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
        setChanged();
        notifyObservers();
    }

    public CommunicationSecurity getSecurity() {
        return security;
    }

    public void setSecurity(CommunicationSecurity security) {
        this.security = security;
    }

    public abstract Configuration get(Term[] args, int minArgs, int maxArgs);

    public abstract Configuration clone();

}
