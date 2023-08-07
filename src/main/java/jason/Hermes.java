package jason;

import jason.architecture.AgArch;
import jason.asSemantics.Message;
import jason.hermes.InComingMessages;
import jason.hermes.config.Configuration;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.middlewares.ContextNetMiddleware;

import java.util.HashMap;
import java.util.List;

public class Hermes extends AgArch {

    private HashMap<String, CommunicationMiddleware> communicationMiddlewareHashMap = new HashMap<>();

    public CommunicationMiddleware getCommunicationMiddleware(String connectionIdentifier) {
        return this.communicationMiddlewareHashMap.get(connectionIdentifier);
    }

    public void addConnectionConfiguration(Configuration configuration) {
        // TODO: criar uma abstração para identificar a instancia correta.
        ContextNetMiddleware contextNetMiddleware = new ContextNetMiddleware(configuration);
        this.communicationMiddlewareHashMap.put(configuration.getConnectionIdentifier(), contextNetMiddleware);
    }

    public String getFirstConnectionAvailable() {
        for (String connectionIdentifier : this.communicationMiddlewareHashMap.keySet()) {
            if (this.communicationMiddlewareHashMap.get(connectionIdentifier).isConnected()) {
                return connectionIdentifier;
            }
        }
        return "";
    }

    @Override
    public void checkMail() {
        super.checkMail();

        List<CommunicationMiddleware> connections = this.communicationMiddlewareHashMap.values().stream().toList();

        List<Message> allReceivedMessages = InComingMessages.getMessages(connections);

        for (Message message : allReceivedMessages) {
            getTS().getC().addMsg(message);
        }
    }
}
