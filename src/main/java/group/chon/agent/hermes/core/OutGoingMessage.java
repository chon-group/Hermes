package group.chon.agent.hermes.core;

import jason.asSemantics.Message;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferMessageDto;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.utils.MessageUtils;

public class OutGoingMessage {

    public static void sendMessage(Message message, CommunicationMiddleware communicationMiddleware) {
        String messageEncrypted = communicationMiddleware.getCommunicationSecurity().encrypt(message);
        communicationMiddleware.sendMessage(messageEncrypted, message.getReceiver());
    }

    public static void sendMessageBioinspiredMessage(AgentTransferMessageDto agentTransferRequestMessageDto,
                                                     CommunicationMiddleware communicationMiddleware, String receiverIdentifier) {

        String agentTransferRequestMessageDtoSerialized = MessageUtils.serializeMessage(agentTransferRequestMessageDto);

        String messageEncrypted = communicationMiddleware.getCommunicationSecurity()
                .encrypt(agentTransferRequestMessageDtoSerialized);
        communicationMiddleware.sendMessage(messageEncrypted, receiverIdentifier);

    }

}
