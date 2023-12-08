package jason.hermes.capabilities.manageConnections.middlewares;

import jason.hermes.capabilities.manageConnections.configuration.Configuration;
import jason.hermes.capabilities.manageConnections.sec.CommunicationSecurity;

import java.util.List;

public interface CommunicationMiddleware extends Cloneable {

    void setConfiguration(Configuration configuration);

    Configuration getConfiguration();

    void connect();

    void disconnect();

    boolean hasMessages();

    void newMessageReceived();

    void sendMessage(String message, String receiver);

    List<String> getReceivedMessages();

    void cleanReceivedMessages();

    boolean isConnected();

    String getAgentIdentification();

    CommunicationSecurity getCommunicationSecurity();

    CommunicationMiddleware clone();

}
