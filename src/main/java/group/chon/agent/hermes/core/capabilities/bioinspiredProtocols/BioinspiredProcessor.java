package group.chon.agent.hermes.core.capabilities.bioinspiredProtocols;

import group.chon.agent.hermes.Hermes;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import group.chon.agent.hermes.core.InComingMessages;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferContentMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.dto.AgentTransferResponseMessageDto;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredRoleEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.enums.BioinspiredStageEnum;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.mapper.BioinspiredDataMapper;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers.AgentTransferConfirmationMessageReceiverProcessor;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers.AgentTransferContentMessageReceiverProcessor;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers.AgentTransferRequestMessageReceiverProcessor;
import group.chon.agent.hermes.core.capabilities.bioinspiredProtocols.receivers.AgentTransferResponseMessageReceiverProcessor;
import group.chon.agent.hermes.core.capabilities.manageConnections.autoconnection.AutoConnectionProcessor;
import group.chon.agent.hermes.core.capabilities.manageConnections.configuration.Configuration;
import group.chon.agent.hermes.core.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import group.chon.agent.hermes.core.capabilities.manageTrophicLevel.TrophicLevelEnum;
import group.chon.agent.hermes.core.utils.BeliefUtils;
import group.chon.agent.hermes.core.utils.BioInspiredUtils;
import jason.runtime.RuntimeServicesFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public abstract class BioinspiredProcessor {

    public static void processMessages(Hermes hermes, InComingMessages inComingMessages) {
        if (!hermes.getBioinspiredData().bioinspiredTransferenceActive()) {
            hermes.setBioinspiredData(BioinspiredProcessor.getBioinspiredRole(
                    inComingMessages.getAgentTransferRequestMessageDto(),
                    hermes.getBioinspiredData()));
        }

        if (hermes.getBioinspiredData().bioinspiredTransferenceActive()) {
            if (hermes.getBioinspiredData().getSenderIdentification() == null) {
                hermes.getBioinspiredData().setConnectionIdentifier(inComingMessages.getBioinspiredConnectionIdentifier());
                String myIdentification = hermes.getCommunicationMiddlewareHashMap().get(inComingMessages.getBioinspiredConnectionIdentifier()).getAgentIdentification();
                hermes.getBioinspiredData().setSenderIdentification(myIdentification);
            }
            BioinspiredProcessor.updateBioinspiredStage(hermes.getBioinspiredData());
            if (!BioinspiredStageEnum.FINISHED.equals(hermes.getBioinspiredData().getBioinspiredStage())) {
                BioinspiredProcessor.receiveBioinspiredMessage(hermes.getTS(), hermes.getBioinspiredData(),
                        hermes.getCommunicationMiddleware(hermes.getBioinspiredData().getConnectionIdentifier()),
                        inComingMessages.getAgentTransferRequestMessageDto(),
                        inComingMessages.getAgentTransferResponseMessageDto(),
                        inComingMessages.getAgentTransferContentMessageDto(),
                        inComingMessages.getAgentTransferConfirmationMessageDto());
            } else {
                if (BioinspiredRoleEnum.RECEIVED.equals(hermes.getBioinspiredData().getBioinspiredRole())) {
                    if(hermes.getBioinspiredData().isHasHermesAgentTransferred()) {
                        if (hermes.getBioinspiredData().isEntireMAS() &&
                                BioinspiredProtocolsEnum.CLONING.equals(hermes.getBioinspiredData().getBioinspiredProtocol())) {
                            String receiverIdentification = hermes.getBioinspiredData().getReceiverIdentification();
                            List<Agent> hermesAgentsTransferred = hermes.getBioinspiredData().getHermesAgentsTransferred();
                            Agent agentCloned = hermesAgentsTransferred.stream().filter(agent -> (
                                            (Hermes) agent.getTS().getAgArch()).getCommunicationMiddlewareHashMap().values()
                                            .stream().anyMatch(communicationMiddleware -> communicationMiddleware
                                                    .getAgentIdentification().equals(receiverIdentification))).findFirst()
                                    .orElse(null);
                            if (agentCloned != null) {
                                Hermes hermesAgentCloned = (Hermes) agentCloned.getTS().getAgArch();
                                CommunicationMiddleware agentClonedCommunicationMiddleware = hermesAgentCloned
                                        .getCommunicationMiddlewareHashMap().values().stream().filter(
                                                communicationMiddleware -> communicationMiddleware
                                                        .getAgentIdentification().equals(receiverIdentification))
                                        .findFirst().orElse(null);
                                if (agentClonedCommunicationMiddleware != null) {
                                    Configuration agentClonedConfiguration = agentClonedCommunicationMiddleware.getConfiguration();
                                    CommunicationMiddleware agentReceiverCommunicationMiddleware =
                                            hermes.getCommunicationMiddlewareHashMap().values().stream().filter(
                                                            communicationMiddleware -> communicationMiddleware.getAgentIdentification()
                                                                    .equals(hermes.getBioinspiredData().getSenderIdentification()))
                                                    .findFirst().orElse(null);
                                    if (agentReceiverCommunicationMiddleware != null) {
                                        Configuration agentReceiverConfiguration = agentReceiverCommunicationMiddleware.getConfiguration();
                                        Configuration agentReceiverConfigurationClone = agentReceiverConfiguration.clone();
                                        BeliefUtils.replaceBelief(agentReceiverConfigurationClone.toBelief(),
                                                agentClonedConfiguration.toBelief(), agentCloned);
                                        agentClonedCommunicationMiddleware.setConfiguration(agentReceiverConfigurationClone);
                                        agentReceiverCommunicationMiddleware.disconnect();
                                    }
                                }
                            }

                        }
                        AutoConnectionProcessor.autoConnection(hermes.getBioinspiredData().getHermesAgentsTransferred());
                    }

                    if (BioinspiredProtocolsEnum.PREDATION.equals(hermes.getBioinspiredData().getBioinspiredProtocol())
                            || (BioinspiredProtocolsEnum.CLONING.equals(hermes.getBioinspiredData().getBioinspiredProtocol())
                            && hermes.getBioinspiredData().isEntireMAS())
                    ){
                        BioInspiredUtils.log(Level.INFO, "Dominating the MAS!");
                        BioInspiredUtils.killAgentsNotTransferred(hermes.getTS(), hermes.getBioinspiredData().getNameOfAgentsInstantiated());
                    }
                }
                hermes.getBioinspiredData().clean();
                String logMessage = "The execution of the protocol ended at "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
                BioInspiredUtils.log(Level.INFO, logMessage);

            }
        }
    }


    public static BioinspiredData getBioinspiredRole(AgentTransferRequestMessageDto agentTransferRequestMessageDto,
                                                     BioinspiredData bioinspiredData) {
        if (agentTransferRequestMessageDto != null) {
            return BioinspiredDataMapper.mapperByAgentTransferRequestMessageDto(agentTransferRequestMessageDto,
                    bioinspiredData.getMyTrophicLevel());
        }

        return bioinspiredData;
    }

    public static void updateBioinspiredStage(BioinspiredData bioinspiredData) {
        if (BioinspiredStageEnum.TRANSFER_REQUEST.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
        } else if (BioinspiredStageEnum.RESPONSE_TO_TRANSFER.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
        } else if (BioinspiredStageEnum.CONTENT_TRANSFER.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
        } else if (BioinspiredStageEnum.CONFIRMATION_TRANSFER.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(bioinspiredData.getBioinspiredStage().getNextStage());
        }



    }

    public static void receiveBioinspiredMessage(TransitionSystem ts, BioinspiredData bioinspiredData,
                                                 CommunicationMiddleware communicationMiddleware,
                                                 AgentTransferRequestMessageDto agentTransferRequestMessageDto,
                                                 AgentTransferResponseMessageDto agentTransferResponseMessageDto,
                                                 AgentTransferContentMessageDto agentTransferContentMessageDto,
                                                 AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto) {
        if (BioinspiredRoleEnum.RECEIVED.equals(bioinspiredData.getBioinspiredRole())) {
            if (BioinspiredStageEnum.RECEIVE_TRANSFER_REQUEST.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferRequestMessageDto != null) {
                AgentTransferRequestMessageReceiverProcessor.receiveTransferRequest(bioinspiredData,
                        communicationMiddleware, agentTransferRequestMessageDto);
            } else if (BioinspiredStageEnum.RECEIVE_CONTENT_TRANSFER.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferContentMessageDto != null) {
                AgentTransferContentMessageReceiverProcessor.receiveAgentsContent(ts, bioinspiredData,
                        communicationMiddleware, agentTransferContentMessageDto);
            }
        } else if (BioinspiredRoleEnum.SENDER.equals(bioinspiredData.getBioinspiredRole())) {
            if (BioinspiredStageEnum.RECEIVE_RESPONSE_TO_TRANSFER.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferResponseMessageDto != null) {
                AgentTransferResponseMessageReceiverProcessor.receiveResponseToTransfer(bioinspiredData,
                        communicationMiddleware, agentTransferResponseMessageDto);
            } else if (BioinspiredStageEnum.RECEIVE_CONFIRMATION_TRANSFER.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferConfirmationMessageDto != null) {
                AgentTransferConfirmationMessageReceiverProcessor.receiveConfirmation(ts, bioinspiredData,
                        agentTransferConfirmationMessageDto);
            }
        } else {
            // TODO: Criar uma exceção para quando o papel do Bioinspired não for nem sender e nem receiver.
            String erroMessage = "Error: Protocol stage ('" + bioinspiredData.getBioinspiredStage().name()
                    + "') incompatible with the agent's role ('" + bioinspiredData.getBioinspiredRole().name()
                    + "') in the protocol.";
            BioInspiredUtils.log(Level.SEVERE, erroMessage);
        }

    }

    public static boolean canTransfer(BioinspiredData bioinspiredData) {
        boolean canTransfer = true;

        if (BioinspiredProtocolsEnum.PREDATION.equals(bioinspiredData.getBioinspiredProtocol())) {
            int comparison = TrophicLevelEnum.trophicLevelComparation(bioinspiredData.getOtherMASTrophicLevel(),
                    bioinspiredData.getMyTrophicLevel());
            return comparison > 0;
        }

        return canTransfer;
    }

    public static boolean canKillOriginCopy(BioinspiredData bioinspiredData) {
        return !BioinspiredProtocolsEnum.CLONING.equals(bioinspiredData.getBioinspiredProtocol());
    }

    public static void stopMAS(int timeInMillis) {
        Timer timer = new Timer();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                try {
                    RuntimeServicesFactory.get().stopMAS();
                } catch (Exception e) {
                    BioInspiredUtils.log(Level.SEVERE, "Error stopping MAS execution: " + e);
                }
            }
        };
        timer.schedule(tarefa, timeInMillis);
    }

}
