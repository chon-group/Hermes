package jason.hermes.bioinspired;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Term;
import jason.hermes.OutGoingMessage;
import jason.hermes.bioinspired.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferContentMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferRequestMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferResponseMessageDto;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import jason.infra.local.LocalAgArch;
import jason.infra.local.RunLocalMAS;
import jason.runtime.RuntimeServicesFactory;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BioinspiredProcessor {

    public static BioinspiredData getBioinspiredDataToStartTheTransference(BioinspiredProtocolsEnum bioinspiredProtocol,
                                                                           Term[] args,
                                                                           String connectionIdentifier,
                                                                           DominanceDegrees dominanceDegrees) {
        if (args.length == 2) {
            boolean hasHermesAgent;
            List<String> nameOfAgentsToBeTransferred;
            if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)){
                nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptCommunicatorAgentName();
                hasHermesAgent = false;
            } else {
                nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
                hasHermesAgent = true;
            }
            return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol, hasHermesAgent,
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST, dominanceDegrees);
        } else if (args.length == 3){
            boolean hasHermesAgent;
            List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
            String agentNameOrConnectionIdentifier = HermesUtils.getParameterInString(args[2]);
            if (BioInspiredUtils.verifyAgentExist(agentNameOrConnectionIdentifier)) {
                nameOfAgentsToBeTransferred.add(agentNameOrConnectionIdentifier);
                hasHermesAgent = false;
            } else {
                connectionIdentifier = agentNameOrConnectionIdentifier;
                if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)){
                    nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptCommunicatorAgentName();
                    hasHermesAgent = false;
                } else {
                    nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
                    hasHermesAgent = true;
                }
            }
            return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol, hasHermesAgent,
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST, dominanceDegrees);
        } else {
            String agentName = HermesUtils.getParameterInString(args[2]);
            connectionIdentifier = HermesUtils.getParameterInString(args[3]);
            boolean hasHermesAgent = false;
            List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
            nameOfAgentsToBeTransferred.add(agentName);
            return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol, hasHermesAgent,
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST, dominanceDegrees);
        }
    }

    public static BioinspiredData getBioinspiredRole(AgentTransferRequestMessageDto agentTransferRequestMessageDto,
                                                     BioinspiredData bioinspiredData) {
        if (agentTransferRequestMessageDto != null) {
            return getBioinspiredData(agentTransferRequestMessageDto, bioinspiredData.getMyDominanceDegree());
        }

        return bioinspiredData;
    }

    public static BioinspiredData getBioinspiredData(AgentTransferRequestMessageDto agentTransferRequestMessageDto,
                                                     DominanceDegrees myDominanceDegree) {
        // TODO: Refatorar depois de fazer o Mapper.
        return new BioinspiredData(agentTransferRequestMessageDto.getNameOfAgentsToBeTransferred(),
                agentTransferRequestMessageDto.getBioinspiredProtocol(),
                agentTransferRequestMessageDto.getSenderIdentification(),
                agentTransferRequestMessageDto.isHasHermesAgentTransferred(),
                BioinspiredRoleEnum.RECEIVED, BioinspiredStageEnum.RECEIVE_TRANSFER_REQUEST, myDominanceDegree,
                agentTransferRequestMessageDto.getDominanceDegree());
    }

    public static void updateBioinspiredStage(BioinspiredData bioinspiredData) {
        if (BioinspiredStageEnum.TRANSFER_REQUEST.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.RECEIVE_RESPONSE_TO_TRANSFER);
        } else if (BioinspiredStageEnum.RESPONSE_TO_TRANSFER.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.RECEIVE_CONTENT_TRANSFER);
        } else if (BioinspiredStageEnum.CONTENT_TRANSFER.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.RECEIVE_CONFIRMATION_TRANSFER);
        } else if (BioinspiredStageEnum.CONFIRMATION_TRANSFER.equals(bioinspiredData.getBioinspiredStage())) {
            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.FINISHED);
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
                receiveTransferRequest(bioinspiredData, communicationMiddleware, agentTransferRequestMessageDto);
            } else if (BioinspiredStageEnum.RECEIVE_CONTENT_TRANSFER.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferContentMessageDto != null) {
                receiveAgentsContent(ts, bioinspiredData, communicationMiddleware, agentTransferContentMessageDto);
            }
        } else if (BioinspiredRoleEnum.SENDER.equals(bioinspiredData.getBioinspiredRole())) {
            if (BioinspiredStageEnum.RECEIVE_RESPONSE_TO_TRANSFER.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferResponseMessageDto != null) {
                receiveResponseToTransfer(bioinspiredData, communicationMiddleware, agentTransferResponseMessageDto);
            } else if (BioinspiredStageEnum.RECEIVE_CONFIRMATION_TRANSFER.equals(bioinspiredData.getBioinspiredStage())
                    && agentTransferConfirmationMessageDto != null) {
                receiveConfirmation(ts, bioinspiredData, agentTransferConfirmationMessageDto);
            }
        } else {
            // TODO: Criar uma exceção para quando o papel do Bioinspired não for nem sender e nem receiver.
        }

    }

    private static boolean canTransfer(BioinspiredData bioinspiredData) {
        boolean canTransfer = true;

        if (BioinspiredProtocolsEnum.PREDATION.equals(bioinspiredData.getBioinspiredProtocol())) {
            int comparison = DominanceDegrees.dominanceComparation(bioinspiredData.getOtherMASDominanceDegree(),
                    bioinspiredData.getMyDominanceDegree());
            return comparison > 0;
        }

        return canTransfer;
    }

    private static boolean canKillOriginCopy() {
        return true;
    }

    private static void receiveTransferRequest(BioinspiredData bioinspiredData,
                                              CommunicationMiddleware communicationMiddleware,
                                              AgentTransferRequestMessageDto agentTransferRequestMessageDto) {
        boolean canTransfer = canTransfer(bioinspiredData);
        AgentTransferResponseMessageDto agentTransferResponseMessageDto =
                new AgentTransferResponseMessageDto(canTransfer);

        bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.RESPONSE_TO_TRANSFER);

        BioInspiredUtils.LOGGER.log(Level.INFO, "Sending the agents transfer response: " + canTransfer);
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferResponseMessageDto,
                communicationMiddleware,
                bioinspiredData.getReceiverIdentification());

        if (!canTransfer) {
            bioinspiredData.clean();
            BioInspiredUtils.LOGGER.info("The execution of the protocol ended at "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")));
        }
    }

    private static void receiveResponseToTransfer(BioinspiredData bioinspiredData,
                                                  CommunicationMiddleware communicationMiddleware,
                                                  AgentTransferResponseMessageDto agentTransferResponseMessageDto) {
        if (agentTransferResponseMessageDto.isCanBeTransferred()) {
            HashMap<String, AslTransferenceModel> agentsSourceCode = new HashMap<>();
            Map<String, LocalAgArch> agentsOfTheSMA = RunLocalMAS.getRunner().getAgs();
            int qtdAgents = 0;
            for (LocalAgArch localAgArch : agentsOfTheSMA.values()) {
                if (bioinspiredData.getNameOfAgentsToBeTransferred().contains(localAgArch.getAgName())) {
                    AslTransferenceModel aslTransferenceModel;
                    if (BioinspiredProtocolsEnum.INQUILINISM.equals(bioinspiredData.getBioinspiredProtocol())) {
                        aslTransferenceModel = AslFileGenerator.generateAslContentWithoutIntentions(
                                localAgArch.getFirstAgArch());
                    } else {
                        aslTransferenceModel = AslFileGenerator.generateAslContent(
                                localAgArch.getFirstAgArch());
                    }

                    agentsSourceCode.put(localAgArch.getAgName(), aslTransferenceModel);
                    qtdAgents++;
                }
            }

            // Verificando se a quantidade de agentes está de acordo com o que se deseja enviar.
            if (qtdAgents == bioinspiredData.getNameOfAgentsToBeTransferred().size()) {
                AgentTransferContentMessageDto agentTransferContentMessageDto =
                        new AgentTransferContentMessageDto(agentsSourceCode);
                bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.CONTENT_TRANSFER);
                BioInspiredUtils.LOGGER.log(Level.INFO, "Sending the agents content.");
                OutGoingMessage.sendMessageBioinspiredMessage(agentTransferContentMessageDto,
                        communicationMiddleware,
                        bioinspiredData.getReceiverIdentification());
            } else {
                BioInspiredUtils.LOGGER.log(Level.SEVERE, "[ERRO]: Não é possível realizar a transferência porque a quantidade de "
                        + "agentes esperados para envio não foi satisfeita.");
            }
        } else {
            BioInspiredUtils.LOGGER.log(Level.INFO, "The Receiver MAS did not allow the agents transference with the protocol "
                    + BioinspiredProtocolsEnum.PREDATION + ".");
            if (BioinspiredProtocolsEnum.PREDATION.equals(bioinspiredData.getBioinspiredProtocol())) {
                BioInspiredUtils.LOGGER.log(Level.INFO, "So, changing to " + BioinspiredProtocolsEnum.INQUILINISM + " protocol.");
                bioinspiredData.setBioinspiredProtocol(BioinspiredProtocolsEnum.INQUILINISM);
                bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.TRANSFER_REQUEST);
                AgentTransferRequestMessageDto anotherAgentTransferRequestMessageDto = new AgentTransferRequestMessageDto(
                        bioinspiredData.getSenderIdentification(),
                        bioinspiredData.getNameOfAgentsToBeTransferred(),
                        bioinspiredData.isHasHermesAgentTransferred(),
                        bioinspiredData.getBioinspiredProtocol(),
                        bioinspiredData.getMyDominanceDegree());

                BioInspiredUtils.LOGGER.log(Level.INFO, "Sending the agent transfer request again.");
                OutGoingMessage.sendMessageBioinspiredMessage(anotherAgentTransferRequestMessageDto,
                        communicationMiddleware, bioinspiredData.getReceiverIdentification());
            } else {
                bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.FINISHED);
            }
        }

    }

    private static void receiveAgentsContent(TransitionSystem ts, BioinspiredData bioinspiredData,
                                            CommunicationMiddleware communicationMiddleware,
                                            AgentTransferContentMessageDto agentTransferContentMessageDto) {
        boolean agentTransferSuccess = false;
        boolean canKill = false;

        List<String> nameOfAgentsToBeTransferredList = bioinspiredData.getNameOfAgentsToBeTransferred();
        int qtdAgentsInstantiated = 0;

        List<String> nameOfAgentsInstantiated = new ArrayList<>();
        // TODO: tratar se tiver erro para instanciar os agentes.
        for (String agentName : nameOfAgentsToBeTransferredList) {
            AslTransferenceModel aslTransferenceModel = agentTransferContentMessageDto.getAgentsSourceCode()
                    .get(agentName);
            String agentNameInstantiated = null;
            try {
                agentNameInstantiated = RuntimeServicesFactory.get().getNewAgentName(aslTransferenceModel.getName());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            if (agentNameInstantiated != null) {
                String path = BioInspiredUtils.getPath(agentNameInstantiated);
                String agArchClass = aslTransferenceModel.getAgentArchClass();

                AslFileGenerator.createAslFile(path, aslTransferenceModel);

                qtdAgentsInstantiated = BioInspiredUtils.startAgent(ts, agentNameInstantiated, path, agArchClass, qtdAgentsInstantiated);
                nameOfAgentsInstantiated.add(agentNameInstantiated);
            }
        }

        if (qtdAgentsInstantiated == nameOfAgentsToBeTransferredList.size()) {
            // Todos os agentes instanciados
            agentTransferSuccess = true;
            canKill = canKillOriginCopy();

        }

        bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.CONFIRMATION_TRANSFER);
        AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto =
                new AgentTransferConfirmationMessageDto(agentTransferSuccess, canKill);
        BioInspiredUtils.LOGGER.log(Level.INFO, "Sending the agent transfer confirmation that is: " + agentTransferSuccess);
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferConfirmationMessageDto,
                communicationMiddleware,
                bioinspiredData.getReceiverIdentification());

        if (BioinspiredProtocolsEnum.PREDATION.equals(bioinspiredData.getBioinspiredProtocol())){
            BioInspiredUtils.LOGGER.log(Level.INFO, "Dominating the MAS!");
            BioInspiredUtils.killAgentsNotTransferred(ts, nameOfAgentsInstantiated);
        }

    }

    private static void receiveConfirmation(TransitionSystem ts, BioinspiredData bioinspiredData,
                                            AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto) {
        if (agentTransferConfirmationMessageDto.isAgentTransferSuccess()) {

            if (agentTransferConfirmationMessageDto.isCanKill()) {
                BioInspiredUtils.killTransferredAgents(ts, bioinspiredData.getNameOfAgentsToBeTransferred());
            }

            if (BioinspiredProtocolsEnum.PREDATION.equals(bioinspiredData.getBioinspiredProtocol())
                    || BioinspiredProtocolsEnum.INQUILINISM.equals(bioinspiredData.getBioinspiredProtocol())) {
                try {
                    RuntimeServicesFactory.get().stopMAS();
                } catch (Exception e) {
                    BioInspiredUtils.LOGGER.log(Level.SEVERE, "Error stopping MAS execution");
                    throw new RuntimeException(e);
                }
            }

            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.FINISHED);
        } else {
            BioInspiredUtils.LOGGER.log(Level.SEVERE, "Agents did not successfully arrive at the destination MAS");
        }
    }

}
