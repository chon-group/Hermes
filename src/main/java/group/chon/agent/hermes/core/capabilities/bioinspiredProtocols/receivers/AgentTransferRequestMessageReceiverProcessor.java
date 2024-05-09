package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers;

import group.chon.agent.hermes.core.OutGoingMessage;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredData;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredProcessor;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferResponseMessageDto;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.utils.BioInspiredUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public abstract class AgentTransferRequestMessageReceiverProcessor {

    public static void receiveTransferRequest(BioinspiredData bioinspiredData,
                                              CommunicationMiddleware communicationMiddleware,
                                              AgentTransferRequestMessageDto agentTransferRequestMessageDto) {
        boolean canTransfer = BioinspiredProcessor.canTransfer(bioinspiredData);
        AgentTransferResponseMessageDto agentTransferResponseMessageDto =
                new AgentTransferResponseMessageDto(canTransfer);

        bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());

        BioInspiredUtils.log(Level.INFO, "Sending the agents transfer response: " + canTransfer);
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferResponseMessageDto,
                communicationMiddleware,
                bioinspiredData.getReceiverIdentification());

        if (!canTransfer) {
            bioinspiredData.clean();
            BioInspiredUtils.log(Level.INFO,"The transference was recused, so the execution of the protocol ended at "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));
        }
    }

}
