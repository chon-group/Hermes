package jason.hermes;

import jason.asSemantics.Message;
import jason.hermes.middlewares.CommunicationMiddleware;

import java.util.ArrayList;
import java.util.List;

public class InComingMessages {

    public static List<Message> getMessages(List<String> receivedEncryptedMessages,
                                            CommunicationMiddleware communicationMiddleware) {
        List<Message> decryptedMessagesList = new ArrayList<>();
        for (String receivedEncryptedMessage : receivedEncryptedMessages) {
            Message decryptedMessage = communicationMiddleware.getCommunicationSecurity()
                    .decrypt(receivedEncryptedMessage);
            decryptedMessagesList.add(decryptedMessage);
        }
        communicationMiddleware.cleanReceivedMessages();

        return decryptedMessagesList;
    }

    public static List<Message> getMessages(List<CommunicationMiddleware> communicationMiddlewares) {
        List<Message> receivedMessagesList = new ArrayList<>();
        for (CommunicationMiddleware communicationMiddleware : communicationMiddlewares) {
            if (communicationMiddleware.isConnected() && communicationMiddleware.hasMessages()) {
                List<String> receivedMessages = communicationMiddleware.getReceivedMessages();
                List<Message> decryptedMessages = getMessages(receivedMessages, communicationMiddleware);
                receivedMessagesList.addAll(decryptedMessages);
            }
        }

        return receivedMessagesList;
    }


}
