package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers;

import jason.asSemantics.TransitionSystem;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredData;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredProcessor;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import group.chon.agent.hermes.core.utils.BioInspiredUtils;

import java.util.logging.Level;

public abstract class AgentTransferConfirmationMessageReceiverProcessor {

    public static void receiveConfirmation(TransitionSystem ts, BioinspiredData bioinspiredData,
                                            AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto) {
        if (agentTransferConfirmationMessageDto.isAgentTransferSuccess()) {
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
