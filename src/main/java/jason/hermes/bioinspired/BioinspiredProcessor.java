package jason.hermes.bioinspired;

import jason.Hermes;
import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import jason.hermes.OutGoingMessage;
import jason.hermes.bioinspired.criogenic.MasBuilderContent;
import jason.hermes.bioinspired.criogenic.MasBuilderStructure;
import jason.hermes.bioinspired.dto.AgentTransferConfirmationMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferContentMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferRequestMessageDto;
import jason.hermes.bioinspired.dto.AgentTransferResponseMessageDto;
import jason.hermes.exception.ErrorCryogeningMASException;
import jason.hermes.exception.ErrorReadingFileException;
import jason.hermes.middlewares.CommunicationMiddleware;
import jason.hermes.utils.BeliefUtils;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.FileUtils;
import jason.hermes.utils.HermesUtils;
import jason.infra.local.LocalAgArch;
import jason.infra.local.RunLocalMAS;
import jason.runtime.RuntimeServicesFactory;

import java.io.File;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BioinspiredProcessor {

    public static BioinspiredData getBioinspiredDataToStartTheTransference(BioinspiredProtocolsEnum bioinspiredProtocol,
                                                                           Term[] args,
                                                                           String connectionIdentifier,
                                                                           TrophicLevelEnum trophicLevelEnum) throws JasonException {
        if (args.length == 2) {
            List<String> nameOfAgentsToBeTransferred;
            if (BioinspiredProtocolsEnum.MUTUALISM.equals(bioinspiredProtocol)){
                nameOfAgentsToBeTransferred = BioInspiredUtils.getAgentsNameExceptHermesAgentName();
            } else {
                nameOfAgentsToBeTransferred = BioInspiredUtils.getAllAgentsName();
            }
            return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                    BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                    trophicLevelEnum, true);
        } else if (args.length == 3){
            boolean entireMAS = false;
            List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
            if (args[2].isList()) {
                ListTerm agentNamesList = HermesUtils.getParameterInList(args[2]);
                nameOfAgentsToBeTransferred.addAll(HermesUtils.getAgentNamesInList(agentNamesList));
                entireMAS = nameOfAgentsToBeTransferred.size() == BioInspiredUtils.getAllAgentsName().size();
            } else {
                String agentNameOrConnectionIdentifier = HermesUtils.getParameterInString(args[2]);
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
            return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                    BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                    trophicLevelEnum, entireMAS);
        } else {
            boolean entireMAS = false;
            List<String> nameOfAgentsToBeTransferred = new ArrayList<>();
            if (args[2].isList()) {
                ListTerm agentNamesList = HermesUtils.getParameterInList(args[2]);
                nameOfAgentsToBeTransferred.addAll(HermesUtils.getAgentNamesInList(agentNamesList));
                entireMAS = nameOfAgentsToBeTransferred.size() == BioInspiredUtils.getAllAgentsName().size();
            } else {
                String agentName = HermesUtils.getParameterInString(args[2]);
                nameOfAgentsToBeTransferred.add(agentName);
            }
            connectionIdentifier = HermesUtils.getParameterInString(args[3]);
            return new BioinspiredData(nameOfAgentsToBeTransferred, bioinspiredProtocol,
                    BioInspiredUtils.hasHermesAgents(nameOfAgentsToBeTransferred),
                    connectionIdentifier, BioinspiredRoleEnum.SENDER, BioinspiredStageEnum.TRANSFER_REQUEST,
                    trophicLevelEnum, entireMAS);
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
                        aslTransferenceModel = AslFileGenerator.generateAslContentWithoutIntentions(
                                localAgArch.getFirstAgArch());
                    } else if (BioinspiredProtocolsEnum.CLONING.equals(bioinspiredData.getBioinspiredProtocol())) {
                        if (bioinspiredData.isEntireMAS()) {
                            if (localAgArch.getFirstAgArch() instanceof Hermes) {
                                boolean isAgentSender = ((Hermes) localAgArch.getFirstAgArch())
                                        .getCommunicationMiddlewareHashMap().values().stream().anyMatch(
                                                communicationMiddleware2 -> communicationMiddleware2.getAgentIdentification()
                                                        .equals(bioinspiredData.getSenderIdentification()));
                                if (isAgentSender) {
                                    aslTransferenceModel = AslFileGenerator.generateAslContent(
                                            localAgArch.getFirstAgArch());
                                } else {
                                    aslTransferenceModel = AslFileGenerator.generateAslContentWithRandomUUID(
                                            localAgArch.getFirstAgArch());
                                }
                            } else {
                                aslTransferenceModel = AslFileGenerator.generateAslContentWithRandomUUID(
                                        localAgArch.getFirstAgArch());
                            }
                        } else {
                            aslTransferenceModel = AslFileGenerator.generateAslContentWithRandomUUID(
                                    localAgArch.getFirstAgArch());
                        }
                    } else {
                        aslTransferenceModel = AslFileGenerator.generateAslContent(
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

                AslFileGenerator.createAslFile(path, aslTransferenceModel);

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
                autoLocalization(ts.getAgArch().getAgName(), notHermesAgentsTransferred, true);
            } else {
                autoLocalization(ts.getAgArch().getAgName(), notHermesAgentsTransferred, true);
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
                stopMAS(1000);
            }

            bioinspiredData.setBioinspiredStage(BioinspiredStageEnum.FINISHED);
        } else {
            BioInspiredUtils.log(Level.SEVERE, "Agents did not successfully arrive at the destination MAS");
        }
    }


    public static void autoConnection(List<Agent> hermesAgentsTransferredList) {
        Timer timer = new Timer();
        final List<Agent> hermesAgentsTransferredListClone = new ArrayList<>(hermesAgentsTransferredList);
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                for (Agent hermesAgentTransferred : hermesAgentsTransferredListClone) {
                    Hermes agArch = (Hermes) hermesAgentTransferred.getTS().getAgArch();
                    for (String connectionIdentifier : agArch.getCommunicationMiddlewareHashMap().keySet()) {
                        CommunicationMiddleware communicationMiddleware = agArch
                                .getCommunicationMiddleware(connectionIdentifier);
                        if (communicationMiddleware.isConnected()) {
                            communicationMiddleware.connect();
                            BioInspiredUtils.log(Level.INFO, "The Hermes transferred agent '"
                                    + agArch.getAgName() + "' automatically connected successfully");
                        } else {
                            BioInspiredUtils.log(Level.INFO, "The Hermes transferred agent '"
                                    + agArch.getAgName() + "' was not connected before the transfer");
                        }
                    }
                }
            }
        };

        timer.schedule(tarefa, 5000);
    }

    public static void autoConnection(Hermes hermes) {
        Timer timer = new Timer();
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                for (String connectionIdentifier : hermes.getCommunicationMiddlewareHashMap().keySet()) {
                    if (!hermes.wasMoved()) {
                        CommunicationMiddleware communicationMiddleware = hermes
                                .getCommunicationMiddleware(connectionIdentifier);
                        if (communicationMiddleware.isConnected()) {
                            communicationMiddleware.connect();
                            BioInspiredUtils.log(Level.INFO, "The Hermes was not moved '"
                                    + hermes.getAgName() + "' and automatically connected successfully");
                        } else {
                            BioInspiredUtils.log(Level.INFO, "The Hermes was not moved '"
                                    + hermes.getAgName() + "' but it believes it is not connected");
                        }
                    } else {
                        BioInspiredUtils.log(Level.INFO, "The Hermes was moved '"
                                + hermes.getAgName() + "'.");
                    }
                }
            }
        };

        timer.schedule(tarefa, 1000);
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

    public static void autoLocalization(String agentName, List<Agent> agents, boolean reload) {
        String masName = RunLocalMAS.getRunner().getProject().getSocName();

        for (Agent agent : agents) {
            if (!agentName.equals(agent.getTS().getAgArch().getAgName())) {
                if (reload) {
                    BeliefUtils.replaceAllBelief(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE,
                            new Atom(agentName), masName, agent);
                } else {
                    BeliefUtils.addBeliefIfAbsent(BeliefUtils.MY_MAS_BELIEF_PREFIX, BeliefUtils.MY_MAS_BELIEF_VALUE,
                            new Atom(agentName), masName, agent);
                }
            }
        }
    }

    public static File cryogenate(String aslSrc) throws ErrorCryogeningMASException {
        String masName = RunLocalMAS.getRunner().getProject().getSocName();
        List<Agent> allMasAgents = RunLocalMAS.getRunner().getAgs().values().stream()
                .map(localAgArch -> localAgArch.getTS().getAg()).collect(Collectors.toList());

        MasBuilderStructure mas = new MasBuilderStructure(masName, allMasAgents);
        File masPath = FileUtils.getMasPath(aslSrc);
        File masFile = null;
        if (masPath != null) {
            try {
                masFile = MasBuilderContent.buildMas(mas, masPath.getPath());
            } catch (Exception e) {
                throw new ErrorCryogeningMASException(masPath.getPath(), e);
            }
        } else {
            throw new ErrorReadingFileException(aslSrc);
        }
        try {
            RuntimeServicesFactory.get().stopMAS();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return masFile;
    }

    public static boolean checkCryogenicFile(String aslSrc) {
        boolean hasCryogenicFile = false;
        File masPath = FileUtils.getMasPath(aslSrc);
        if (masPath != null && masPath.exists()) {
            File[] files = masPath.listFiles();
            if (files != null && files.length > 0) {
                File cryogenicFile = new File(masPath.getPath(), FileUtils.CRYOGENIC_FILE);
                List<File> list = Arrays.stream(files).toList();
                hasCryogenicFile = list.contains(cryogenicFile);
            }
        }
        return hasCryogenicFile;
    }

}
