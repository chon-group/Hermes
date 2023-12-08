package jason.hermes.capabilities.bioinspiredProtocols;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import jason.hermes.InComingMessages;
import jason.hermes.OutGoingMessage;
import jason.hermes.capabilities.autoLocalization.AutoLocalizationProcessor;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferContentMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferRequestMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.dto.AgentTransferResponseMessageDto;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredProtocolsEnum;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredRoleEnum;
import jason.hermes.capabilities.bioinspiredProtocols.enums.BioinspiredStageEnum;
import jason.hermes.capabilities.manageConnections.autoconnection.AutoConnectionProcessor;
import jason.hermes.capabilities.manageConnections.configuration.ContextNetConfiguration;
import jason.hermes.capabilities.manageConnections.middlewares.CommunicationMiddleware;
import jason.hermes.capabilities.manageConnections.middlewares.ContextNetMiddleware;
import jason.hermes.capabilities.manageTrophicLevel.TrophicLevelEnum;
import jason.hermes.utils.ArgsUtils;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import jason.hermes.utils.aslFiles.AslFileGeneratorUtils;
import jason.hermes.utils.aslFiles.AslTransferenceModel;
import jason.infra.local.LocalAgArch;
import jason.infra.local.RunLocalMAS;
import jason.runtime.RuntimeServicesFactory;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;

public abstract class BioinspiredProcessor {

    public static BioinspiredData getBioinspiredDataByArgs(Term[] args, Hermes hermes) throws JasonException {
        BioinspiredData bioinspiredData;

        String receiver = ArgsUtils.getInString(args[0]);
        String protocolName = ArgsUtils.getInString(args[1]).toUpperCase();
        BioinspiredProtocolsEnum bioinspiredProtocol = BioinspiredProtocolsEnum.getBioInspiredProtocol(protocolName);

        String connectionIdentifier = hermes.getFirstConnectionAvailable();
        TrophicLevelEnum trophicLevelEnum = hermes.getBioinspiredData().getMyTrophicLevel();

        if (args.length == 2) {
            List<String> nameOfAgentsToBeTransferred;
            if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)){
                nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptHermesAgentName();
            } else {
                nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
            }
            bioinspiredData = new  BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                    BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                    trophicLevelEnum, true);
        } else if (args.length == 3){
            boolean entireMAS = false;
            List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
            if (args[2].isList()) {
                ListTerm agentNamesList = ArgsUtils.getInListTerm(args[2]);
                nameOfAgentsToBeTransferred.addAll(HermesUtils.getAgentNamesInList(agentNamesList));
                entireMAS = nameOfAgentsToBeTransferred.size() == BioInspiredUtils.getAllAgentsName().size();
            } else {
                String agentNameOrConnectionIdentifier = ArgsUtils.getInString(args[2]);
                if (HermesUtils.verifyAgentExist(agentNameOrConnectionIdentifier)) {
                    nameOfAgentsToBeTransferred.add(agentNameOrConnectionIdentifier);
                    entireMAS = false;
                } else {
                    connectionIdentifier = agentNameOrConnectionIdentifier;
                    if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)) {
                        nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptHermesAgentName();
                    } else {
                        nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
                    }
                    entireMAS = true;
                }
            }
            bioinspiredData = new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                    BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                    trophicLevelEnum, entireMAS);
        } else {
            boolean entireMAS = false;
            List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
            if (args[2].isList()) {
                ListTerm agentNamesList = ArgsUtils.getInListTerm(args[2]);
                nameOfAgentsToBeTransferred.addAll(HermesUtils.getAgentNamesInList(agentNamesList));
                entireMAS = nameOfAgentsToBeTransferred.size() == BioInspiredUtils.getAllAgentsName().size();
            } else {
                String agentName = ArgsUtils.getInString(args[2]);
                nameOfAgentsToBeTransferred.add(agentName);
            }
            connectionIdentifier = ArgsUtils.getInString(args[3]);
            bioinspiredData = new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                    BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                    trophicLevelEnum, entireMAS);
        }


        String myIdentification = hermes.getCommunicationMiddleware(
                bioinspiredData.getConnectionIdentifier()).getAgentIdentification();
        bioinspiredData.setSenderIdentification(myIdentification);
        bioinspiredData.setReceiverIdentification(receiver);
        hermes.setBioinspiredData(bioinspiredData);

        return bioinspiredData;
    }

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
                                if (agentClonedCommunicationMiddleware != null
                                        && agentClonedCommunicationMiddleware instanceof ContextNetMiddleware) {
                                    ContextNetConfiguration agentClonedConfiguration = (ContextNetConfiguration)
                                            agentClonedCommunicationMiddleware.getConfiguration();
                                    CommunicationMiddleware agentReceiverCommunicationMiddleware =
                                            hermes.getCommunicationMiddlewareHashMap().values().stream().filter(
                                                            communicationMiddleware -> communicationMiddleware.getAgentIdentification()
                                                                    .equals(hermes.getBioinspiredData().getSenderIdentification()))
                                                    .findFirst().orElse(null);
                                    if (agentReceiverCommunicationMiddleware != null
                                            && agentReceiverCommunicationMiddleware instanceof ContextNetMiddleware) {
                                        ContextNetConfiguration agentReceiverConfiguration = (ContextNetConfiguration)
                                                agentReceiverCommunicationMiddleware.getConfiguration();
                                        ContextNetConfiguration agentReceiverConfigurationClone = agentReceiverConfiguration.clone();
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
                        String logMessage = "The execution of the protocol ended at "
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
                        BioInspiredUtils.log(Level.INFO, logMessage);
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
            return getBioinspiredData(agentTransferRequestMessageDto, bioinspiredData.getMyTrophicLevel());
        }

        return bioinspiredData;
    }

    public static BioinspiredData getBioinspiredData(AgentTransferRequestMessageDto agentTransferRequestMessageDto,
                                                     TrophicLevelEnum myTrophicLevelEnum) {
        // TODO: Refatorar depois de fazer o Mapper.
        return new BioinspiredData(agentTransferRequestMessageDto.getNameOfAgentsToBeTransferred(),
                agentTransferRequestMessageDto.getBioinspiredProtocol(),
                agentTransferRequestMessageDto.getSenderIdentification(),
                agentTransferRequestMessageDto.isHasHermesAgentTransferred(),
                BioinspiredRoleEnum.RECEIVED, BioinspiredStageEnum.RECEIVE_TRANSFER_REQUEST, myTrophicLevelEnum,
                agentTransferRequestMessageDto.getTrophicLevelEnum(), agentTransferRequestMessageDto.isEntireMAS());
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
            int comparison = TrophicLevelEnum.trophicLevelComparation(bioinspiredData.getOtherMASTrophicLevel(),
                    bioinspiredData.getMyTrophicLevel());
            return comparison > 0;
        }

        return canTransfer;
    }

    private static boolean canKillOriginCopy(BioinspiredData bioinspiredData) {
        return !BioinspiredProtocolsEnum.CLONING.equals(bioinspiredData.getBioinspiredProtocol());
    }

    private static void receiveTransferRequest(BioinspiredData bioinspiredData,
                                              CommunicationMiddleware communicationMiddleware,
                                              AgentTransferRequestMessageDto agentTransferRequestMessageDto) {
        boolean canTransfer = canTransfer(bioinspiredData);
        AgentTransferResponseMessageDto agentTransferResponseMessageDto =
                new AgentTransferResponseMessageDto(canTransfer);

        bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.RESPONSE_TO_TRANSFER);

        BioInspiredUtils.log(Level.INFO, "Sending the agents transfer response: " + canTransfer);
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferResponseMessageDto,
                communicationMiddleware,
                bioinspiredData.getReceiverIdentification());

        if (!canTransfer) {
            bioinspiredData.clean();
            BioInspiredUtils.log(Level.INFO,"The execution of the protocol ended at "
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
                bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.CONTENT_TRANSFER);
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
            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.FINISHED);
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
        List<Agent> hermesAgentsTransferred = new ArrayList<>();
        List<Agent> notHermesAgentsTransferred = new ArrayList<>();
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

                AslFileGeneratorUtils.createAslFile(path, aslTransferenceModel);

                qtdAgentsInstantiated = BioInspiredUtils.startAgent(ts, agentNameInstantiated, path, agArchClass, qtdAgentsInstantiated);
                Agent agent = RunLocalMAS.getRunner().getAg(agentNameInstantiated).getTS().getAg();
                if (Hermes.class.getName().equals(aslTransferenceModel.getAgentArchClass())) {
                    try {
                        Hermes hermes = HermesUtils.checkArchClass(agent.getTS().getAgArch());
                        hermes.setMoved(true);
                    } catch (JasonException e) {
                        throw new RuntimeException(e);
                    }
                    hermesAgentsTransferred.add(agent);
                } else {
                    notHermesAgentsTransferred.add(agent);
                }

                nameOfAgentsInstantiated.add(agentNameInstantiated);
            }
        }

        if (qtdAgentsInstantiated == nameOfAgentsToBeTransferredList.size()) {
            // Todos os agentes instanciados
            agentTransferSuccess = true;
            if (bioinspiredData.isHasHermesAgentTransferred()) {
                bioinspiredData.setHermesAgentsTransferred(hermesAgentsTransferred);
                AutoLocalizationProcessor.autoLocalization(ts.getAgArch().getAgName(), notHermesAgentsTransferred, true);
            } else {
                AutoLocalizationProcessor.autoLocalization(ts.getAgArch().getAgName(), notHermesAgentsTransferred, true);
            }
            canKill = canKillOriginCopy(bioinspiredData);

        }

        bioinspiredData.setNameOfAgentsInstantiated(nameOfAgentsInstantiated);
        bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.CONFIRMATION_TRANSFER);
        AgentTransferConfirmationMessageDto agentTransferConfirmationMessageDto =
                new AgentTransferConfirmationMessageDto(agentTransferSuccess, canKill);
        BioInspiredUtils.log(Level.INFO, "Sending the agent transfer confirmation that is: " + agentTransferSuccess);
        OutGoingMessage.sendMessageBioinspiredMessage(agentTransferConfirmationMessageDto,
                communicationMiddleware,
                bioinspiredData.getReceiverIdentification());

    }

    private static void receiveConfirmation(TransitionSystem ts, BioinspiredData bioinspiredData,
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

            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.FINISHED);
        } else {
            BioInspiredUtils.log(Level.SEVERE, "Agents did not successfully arrive at the destination MAS");
        }
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
