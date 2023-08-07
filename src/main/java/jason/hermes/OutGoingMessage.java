package jason.hermes;

import jason.asSemantics.Message;
import jason.hermes.middlewares.CommunicationMiddleware;

public class OutGoingMessage {

    public static void sendMessage(Message message, CommunicationMiddleware communicationMiddleware) {
        String messageEncrypted = communicationMiddleware.getCommunicationSecurity().encrypt(message);
        communicationMiddleware.sendMessage(messageEncrypted, message.getReceiver());
    }

}
