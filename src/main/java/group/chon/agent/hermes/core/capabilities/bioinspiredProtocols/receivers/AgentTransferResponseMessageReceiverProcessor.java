package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers;

import group.chon.agent.hermes.Hermes;
import jason.asSemantics.Agent;
import group.chon.agent.hermes.core.OutGoingMessage;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.BioinspiredData;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferContentMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferResponseMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.utils.BioInspiredUtils;
import group.chon.agent.hermes.core.utils.aslFiles.AslFileGeneratorUtils;
import group.chon.agent.hermes.core.utils.aslFiles.AslTransferenceModel;
import jason.infra.local.LocalAgArch;
import jason.infra.local.RunLocalMAS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class AgentTransferResponseMessageReceiverProcessor {

    public static void receiveResponseToTransfer(BioinspiredData bioinspiredData,
                                                  CommunicationMiddleware communicationMiddleware,
                                                  AgentTransferResponseMessageDto agentTransferResponseMessageDto) {
        if (agentTransferResponseMessageDto.isCanBeTransferred()) {
            HashMap<String, AslTransferenceModel> agentsSourceCode = new HashMap<>();
            Map<String, LocalAgArch> agentsOfTheSMA = RunLocalMAS.getRunner().getAgs();
            int qtdAgents = 0;
            List<Agent> hermesAgentsTransferred = new ArrayList<>();
            for (LocalAgArch localAgArch : agentsOfTheSMA.values()) {
                if (bioinspiredData.getNameOfAgentsToBeTransferred().contains(localAgArch.getAgName())) {
                    AslTransferenceModel aslTransferenceModel;
                    if (BioinspiredProtocolsEnum.INQUILINISM.equals(bioinspiredData.getBioinspiredProtocol())) {
                        aslTransferenceModel = AslFileGeneratorUtils.generateAslContentWithoutIntentions(
                                localAgArch.getFirstAgArch());
                    } else if (BioinspiredProtocolsEnum.CLONING.equals(bioinspiredData.getBioinspiredProtocol())) {
                        if (bioinspiredData.isEntireMAS()) {
                            if (localAgArch.getFirstAgArch() instanceof Hermes) {
                                boolean isAgentSender = ((Hermes) localAgArch.getFirstAgArch())
                                        .getCommunicationMiddlewareHashMap().values().stream().anyMatch(
                                                communicationMiddleware2 -> communicationMiddleware2.getAgentIdentification()
                                                        .equals(bioinspiredData.getSenderIdentification()));
                                if (isAgentSender) {
                                    aslTransferenceModel = AslFileGeneratorUtils.generateAslContent(
                                            localAgArch.getFirstAgArch());
                                } else {
                                    aslTransferenceModel = AslFileGeneratorUtils.generateAslContentWithRandomUUID(
                                            localAgArch.getFirstAgArch());
                                }
                            } else {
                                aslTransferenceModel = AslFileGeneratorUtils.generateAslContentWithRandomUUID(
                                        localAgArch.getFirstAgArch());
                            }
                        } else {
                            aslTransferenceModel = AslFileGeneratorUtils.generateAslContentWithRandomUUID(
                                    localAgArch.getFirstAgArch());
                        }
                    } else {
                        aslTransferenceModel = AslFileGeneratorUtils.generateAslContent(
                                localAgArch.getFirstAgArch());
                    }

                    agentsSourceCode.put(localAgArch.getAgName(), aslTransferenceModel);
                    if (Hermes.class.getName().equals(aslTransferenceModel.getAgentArchClass())) {
                        hermesAgentsTransferred.add(localAgArch.getTS().getAg());
                    }
                    qtdAgents++;
                }
            }

            // Verificando se a quantidade de agentes está de acordo com o que se deseja enviar.
            if (qtdAgents == bioinspiredData.getNameOfAgentsToBeTransferred().size()) {
                AgentTransferContentMessageDto agentTransferContentMessageDto =
                        new AgentTransferContentMessageDto(agentsSourceCode);
                bioinspiredData.setHermesAgentsTransferred(hermesAgentsTransferred);
                bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
                BioInspiredUtils.log(Level.INFO, "Sending the agents content.");
                OutGoingMessage.sendMessageBioinspiredMessage(agentTransferContentMessageDto,
                        communicationMiddleware,
                        bioinspiredData.getReceiverIdentification());
            } else {
                BioInspiredUtils.log(Level.SEVERE, "[ERRO]: Não é possível realizar a transferência porque a quantidade de "
                        + "agentes esperados para envio não foi satisfeita.");
            }
        } else {
            BioInspiredUtils.log(Level.INFO, "The Receiver MAS did not allow the agents transference with the protocol "
                    + BioinspiredProtocolsEnum.PREDATION + ".");
            bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStageIfHasAnError());
        }

    }

}
