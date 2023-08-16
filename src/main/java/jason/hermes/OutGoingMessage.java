package jason.hermes;

import jason.asSemantics.Message;
import jason.hermes.bioinspired.dto.AgentTransferMessageDto;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.utils.HermesUtils;

public class OutGoingMessage {

    public static void sendMessage(Message message, CommunicationMiddleware communicationMiddleware) {
        String messageEncrypted = communicationMiddleware.getCommunicationSecurity().encrypt(message);
        communicationMiddleware.sendMessage(messageEncrypted, message.getReceiver());
    }

    public static void sendMessageBioinspiredMessage(AgentTransferMessageDto agentTransferRequestMessageDto,
                                                     CommunicationMiddleware communicationMiddleware, String receiverIdentifier) {

        String agentTransferRequestMessageDtoSerialized = HermesUtils.serializeMessage(agentTransferRequestMessageDto);

        String messageEncrypted = communicationMiddleware.getCommunicationSecurity()
                .encrypt(agentTransferRequestMessageDtoSerialized);
        communicationMiddleware.sendMessage(messageEncrypted, receiverIdentifier);

    }

}
