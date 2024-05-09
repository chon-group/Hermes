package group.chon.agent.hermes.core;

import jason.asSemantics.Message;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredData;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferContentMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferResponseMessageDto;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.utils.MessageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InComingMessages {

    private static final Logger LOGGER = Logger.getLogger("IN_COMING_MESSAGES");
    private Map<String, List<Message>> messages;

    private AgentTransferRequestMessageDto agentTransferRequestMessageDto;

    private AgentTransferResponseMessageDto agentTransferResponseMessageDto;

    private AgentTransferContentMessageDto agentTransferContentMessageDto;

    private AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto;

    private HashMap<String, CommunicationMiddleware> communicationMiddlewares;

    private String bioinspiredConnectionIdentifier;

    public InComingMessages(BioinspiredData bioinspiredData, HashMap<String, CommunicationMiddleware> communicationMiddlewares) {
        if (bioinspiredData != null) {
            this.bioinspiredConnectionIdentifier = bioinspiredData.getConnectionIdentifier();
        }
        this.communicationMiddlewares = communicationMiddlewares;
        this.messages = new HashMap<>();
        this.agentTransferRequestMessageDto = null;
        this.agentTransferResponseMessageDto = null;
        this.agentTransferContentMessageDto = null;
        this.agentTransferConfirmationMessageDto = null;
        this.getAllMessages();
    }

    public void getAllMessages() {
        for (String connectionIdentifier : this.communicationMiddlewares.keySet()) {
            CommunicationMiddleware communicationMiddleware = this.communicationMiddlewares.get(connectionIdentifier);
            if (communicationMiddleware.isConnected() && communicationMiddleware.hasMessages()) {
                List<String> receivedMessages = communicationMiddleware.getReceivedMessages();
                this.classifyMessages(receivedMessages, communicationMiddleware, connectionIdentifier);
            }
        }

    }

    public void classifyMessages(List<String> receivedEncryptedMessages,
                                 CommunicationMiddleware communicationMiddleware,
                                 String connectionIdentifier) {
        for (String receivedEncryptedMessage : receivedEncryptedMessages) {
            Object decryptedObjectMessage = communicationMiddleware.getCommunicationSecurity()
                    .decrypt(receivedEncryptedMessage);
            InComingMessages.log(Level.FINE, "New message received.");

            Message decryptedMessage = MessageUtils.getJasonMessage(decryptedObjectMessage);
            if (decryptedMessage != null) {
                this.addMessage(connectionIdentifier, decryptedMessage);
                continue;
            }

            AgentTransferRequestMessageDto agentTransferRequestMessageDto = MessageUtils
                    .getAgentTransferRequestMessage(decryptedObjectMessage);
            if (agentTransferRequestMessageDto != null &&
                    (this.bioinspiredConnectionIdentifier == null || connectionIdentifier.equals(this.bioinspiredConnectionIdentifier))) {
                this.agentTransferRequestMessageDto = agentTransferRequestMessageDto;
                InComingMessages.log(Level.INFO, "Received an agent transfer request!");
                this.bioinspiredConnectionIdentifier = connectionIdentifier;
                continue;
            }

            AgentTransferResponseMessageDto agentTransferResponseMessageDto = MessageUtils
                    .getAgentTransferResponseMessage(decryptedObjectMessage);
            if (agentTransferResponseMessageDto != null && connectionIdentifier.equals(this.bioinspiredConnectionIdentifier)) {
                this.agentTransferResponseMessageDto = agentTransferResponseMessageDto;
                InComingMessages.log(Level.INFO, "The response to the transfer of agents was: "
                        + agentTransferResponseMessageDto.isCanBeTransferred());
                continue;
            }

            AgentTransferContentMessageDto agentTransferContentMessageDto = MessageUtils
                    .getAgentTransferContentMessage(decryptedObjectMessage);
            if (agentTransferContentMessageDto != null && connectionIdentifier.equals(this.bioinspiredConnectionIdentifier)) {
                this.agentTransferContentMessageDto = agentTransferContentMessageDto;
                InComingMessages.log(Level.INFO, "Received the agents content");
                continue;
            }

            AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto = MessageUtils
                    .getAgentTransferConfirmationMessage(decryptedObjectMessage);
            if (agentTransferConfirmationMessageDto != null && connectionIdentifier.equals(this.bioinspiredConnectionIdentifier)) {
                this.agentTransferConfirmationMessageDto = agentTransferConfirmationMessageDto;
                InComingMessages.log(Level.INFO, "The agent transfer confirmation was: "
                        + agentTransferConfirmationMessageDto.isAgentTransferSuccess());
                continue;
            }

            // TODO: verificar oque fazer se não for uma mensagem Jason e nem uma mensagem dos Bioinspired protocols.
            InComingMessages.log(Level.INFO, "Unable to classify the received message: " + decryptedObjectMessage);

        }
        communicationMiddleware.cleanReceivedMessages();
    }

    public static void log(Level level, String message) {
        try {
            LOGGER.log(level, message);
        } catch (Exception e) {
            //ignore
        }
    }

    private void addMessage(String connectionIdentifier, Message newMessage) {
        List<Message> messageList = new ArrayList<>();
        if (this.messages.containsKey(connectionIdentifier)) {
            messageList = this.messages.get(connectionIdentifier);
        }
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        messageList.add(newMessage);
        this.messages.put(connectionIdentifier, messageList);
    }

    public Map<String, List<Message>> getMessages() {
        return messages;
    }

    public AgentTransferRequestMessageDto getAgentTransferRequestMessageDto() {
        return agentTransferRequestMessageDto;
    }

    public AgentTransferResponseMessageDto getAgentTransferResponseMessageDto() {
        return agentTransferResponseMessageDto;
    }

    public AgentTransferContentMessageDto getAgentTransferContentMessageDto() {
        return agentTransferContentMessageDto;
    }

    public AgentTransferConfirmationMessageDto getAgentTransferConfirmationMessageDto() {
        return agentTransferConfirmationMessageDto;
    }

    public String getBioinspiredConnectionIdentifier() {
        return bioinspiredConnectionIdentifier;
    }
}
