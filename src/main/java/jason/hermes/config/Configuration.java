package jason.hermes.config;

import jason.asSyntax.Literal;
import jason.hermes.sec.CommunicationSecurity;

import java.util.Observable;

public abstract class Configuration extends Observable implements Cloneable {

    private String connectionIdentifier;

    private boolean connected = false;

    private CommunicationSecurity security;

    public abstract Literal toBelief();

    public abstract Configuration getByBelief(Literal belief);


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

    public abstract Configuration clone();

}
