package jason.hermes.capabilities.bioinspiredProtocols.receivers;

import jason.Hermes;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredData;
import jason.hermes.capabilities.bioinspiredProtocols.BioinspiredProcessor;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.utils.BioInspiredUtils;

import java.util.logging.Level;

public abstract class AgentTransferConfirmationMessageReceiverProcessor {

    public static void receiveConfirmation(TransitionSystem ts, BioinspiredData bioinspiredData,
                                            AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto) {
        if (agentTransferConfirmationMessageDto.isAgentTransferSuccess()) {

            for (Agent hermesAgentTransferred : bioinspiredData.getHermesAgentsTransferred()) {
                Hermes agArch = (Hermes) hermesAgentTransferred.getTS().getAgArch();
                for (String connectionIdentifier : agArch.getCommunicationMiddlewareHashMap().keySet()) {
                    CommunicationMiddleware communicationMiddleware = agArch
                            .getCommunicationMiddleware(connectionIdentifier);
                    if (communicationMiddleware.isConnected()) {
                        communicationMiddleware.disconnect();
                        BioInspiredUtils.log(Level.INFO, "The Hermes transferred agent '"
                                + agArch.getAgName() + "' was disconected in the Sender MAS.");
                    }
                }
            }

            if (agentTransferConfirmationMessageDto.isCanKill()) {
                BioInspiredUtils.killTransferredAgents(ts, bioinspiredData.getNameOfAgentsToBeTransferred());
            }

            if (BioinspiredProtocolsEnum.PREDATION.equals(bioinspiredData.getBioinspiredProtocol())
                    || BioinspiredProtocolsEnum.INQUILINISM.equals(bioinspiredData.getBioinspiredProtocol())) {
                BioinspiredProcessor.stopMAS(1000);
            }

            bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
        } else {
            BioInspiredUtils.log(Level.SEVERE, "Agents did not successfully arrive at the destination MAS");
        }
    }

}
