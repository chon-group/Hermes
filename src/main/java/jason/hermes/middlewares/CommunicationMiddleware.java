package jason.hermes.middlewares;

import jason.hermes.config.Configuration;
import jason.hermes.sec.CommunicationSecurity;

import java.util.List;

public interface CommunicationMiddleware {

    void setConfiguration(Configuration configuration);

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

}
